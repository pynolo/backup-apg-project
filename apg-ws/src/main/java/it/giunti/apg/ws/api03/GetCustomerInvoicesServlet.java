package it.giunti.apg.ws.api03;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_GET_CUSTOMER_INVOICES)*/
public class GetCustomerInvoicesServlet extends ApiServlet {
	private static final long serialVersionUID = 4962260012898323782L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_CUSTOMER_INVOICES;
	private static final Logger LOG = LoggerFactory.getLogger(GetCustomerInvoicesServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/get_customer_invoices?access_key=1234&id_customer=Q090NQ
	*/

    public GetCustomerInvoicesServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
    }

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	//@Override
	//protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//	doPost(request, response);
	//}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BaseUrlSingleton.get().setBaseUrl(request);
		request.setCharacterEncoding(AppConstants.CHARSET_UTF8);
		JsonObject result = null;
		//acquire access key
		String accessKey = request.getParameter(Constants.PARAM_ACCESS_KEY);
		ApiServices service = null;
		if (accessKey == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_ACCESS_KEY, ErrorEnum.WRONG_ACCESS_KEY.getErrorDescr());
		} else {
			try {
				service = ValidationBusiness.validateAccessKey(accessKey);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, ErrorEnum.INTERNAL_ERROR.getErrorDescr());
				LOG.error(e.getMessage(), e);
			}
			if (service == null) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_ACCESS_KEY, ErrorEnum.WRONG_ACCESS_KEY.getErrorDescr());
			} else {
				LOG.debug(FUNCTION_NAME+" chiamata da "+service.getNome());
			}
		}
		//acquire idCustomer
		String idCustomer = request.getParameter(Constants.PARAM_ID_CUSTOMER);
		if (idCustomer == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER+" is empty");
		}
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				Anagrafiche ana = new AnagraficheDao().findByUid(ses, idCustomer);
				if (ana == null) throw new BusinessException(idCustomer+" has no match");
				List<Fatture> fList = new FattureDao().findByAnagrafica(ses, ana.getId(), false, true);
				//nulls id's of prints if prints are not available
				for (Fatture f:fList) {
					FattureStampe sf = null;
					if (f.getIdFatturaStampa() != null) {
						sf = GenericDao.findById(ses, FattureStampe.class, f.getIdFatturaStampa());
					}
					if (sf == null) f.setIdFatturaStampa(null);
				}
				JsonObjectBuilder joBuilder = schemaBuilder(fList);
				result = BaseJsonFactory.buildBaseObject(joBuilder);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.DATA_NOT_FOUND, ErrorEnum.DATA_NOT_FOUND.getErrorDescr());
				LOG.info(e.getMessage(), e);
			} finally {
				ses.close();
			}
		}
		//send response
		response.setContentType("application/json");
		response.setCharacterEncoding(AppConstants.CHARSET_UTF8);
		PrintWriter out = response.getWriter();
		out.print(result.toString());
		out.flush();
	}

	private JsonObjectBuilder schemaBuilder(List<Fatture> fList) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (Fatture f:fList) {
			Double paidAmount = f.getTotaleFinale();
			String paidString = ServerConstants.FORMAT_INTEGER.format(Math.floor(paidAmount))+"."+
					ServerConstants.FORMAT_INTEGER.format(Math.round((paidAmount-Math.floor(paidAmount))*100));
			String availableString = "true";
			if (f.getIdFatturaStampa() == null) availableString = "false";
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, Constants.PARAM_ID_INVOICE, f.getNumeroFattura());
			add(ob, "date", f.getDataFattura());
			add(ob, "total_amount", paidString);
			add(ob, "id_document_type", f.getIdTipoDocumento());
			add(ob, "is_file_available", availableString);
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("invoices", arrayBuilder);
		return objectBuilder;
	}

}
