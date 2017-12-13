package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetInvoiceFileServlet extends HttpServlet {
	private static final long serialVersionUID = 510346861934804813L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_INVOICE_FILE;
	private static final Logger LOG = LoggerFactory.getLogger(GetInvoiceFileServlet.class);

	/*example testing url:
	 http://astest:81/apgws/api01/get_invoice_file?access_key=1234&id_invoice=FXS6017637
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetInvoiceFileServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
    }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//access key
		String accessKey = request.getParameter(Constants.PARAM_ACCESS_KEY);
		ApiServices service = null;
		if (accessKey == null) {
			throw new ServletException("Wrong access key");
		} else {
			try {
				service = ValidationBusiness.validateAccessKey(accessKey);
			} catch (BusinessException e) {
				throw new ServletException("Internal error");
			}
			if (service == null) {
				throw new ServletException("Wrong access key");
			} else {
				LOG.debug(FUNCTION_NAME+" chiamata da "+service.getNome());
			}
		}
		//id_invoice
		String idString = request.getParameter(Constants.PARAM_ID_INVOICE);
		try {
			idString = ValidationBusiness.cleanInput(idString, 20);
		} catch (ValidationException e1) {
			throw new ServletException("Wrong id_invoice supplied");
		}
		if (idString == null) throw new ServletException("No id_invoice supplied");
		Session ses = SessionFactory.getSession();
		try {
			List<Fatture> fList = new FattureDao().findByNumeroFattura(ses, idString);
			if (fList == null) throw new ServletException("Wrong id_invoice supplied");
			if (fList.size() == 0) throw new ServletException("Wrong id_invoice supplied");
			Fatture fat = fList.get(0);
			FattureStampe sf = null;
			if (fat.getIdFatturaStampa() != null) {
				sf = GenericDao.findById(ses, FattureStampe.class, fat.getIdFatturaStampa());
			} else {
				throw new ServletException("Invoice has no matching file");
			}
			if (sf == null) throw new ServletException("Incorrect id_invoice");
			if (sf.getContent() == null) throw new ServletException("No data bundled in object");
			if (sf.getContent().length < 2) throw new ServletException("No data bundled in object");
			if (sf.getMimeType() == null) throw new ServletException("No file content type available");
			if (sf.getMimeType().length() < 2) throw new ServletException("No file content type available");
			PrintWriter out = new PrintWriter(response.getOutputStream());
			ServletOutputStream binout = response.getOutputStream();
			response.setContentType(sf.getMimeType());
			response.setHeader("Content-Disposition", "attachment;filename="+sf.getFileName());
			byte[] sfBytes = sf.getContent();
			binout.write(sfBytes);
			out.close();
		} catch (HibernateException e) {
			throw new ServletException(e.getMessage());
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		} finally {
			ses.close();
		}
	}

}
