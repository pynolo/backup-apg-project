package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.business.FatturePdfBusiness;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FattureStampeServlet extends HttpServlet {
	private static final long serialVersionUID = 510346861934804813L;

	//Process the HTTP Get request
	//accetta come parametri sia id fattura che numero fattura
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Integer id = null;
		String idString = request.getParameter(AppConstants.PARAM_ID);
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) { }
		String name = request.getParameter(AppConstants.PARAM_NAME);
		if (name == null) name = "";
		if (id == null && name.length() < 6) {
			throw new ServletException("No id or name supplied");
		}
		Session ses = SessionFactory.getSession();
		try {
			Fatture fattura = null;
			if (id != null) {
				fattura = GenericDao.findById(ses, Fatture.class, id);
			}
			if (name != null) {
				List<Fatture> fList = new FattureDao().findByNumeroFattura(ses, name);
				if (fList.size() > 0) {
					fattura = fList.get(0);
				}
			}
			if (fattura == null) throw new ServletException("La fattura indicata non esiste");
			
			FattureStampe sf = null;
			if (fattura.getIdFatturaStampa() != null) {
				sf = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
			} else {
				if (fattura.getDataCreazione().getTime() < (DateUtil.now().getTime()-AppConstants.DAY*3))
					throw new ServletException("La fattura non e' disponibile");
				sf = FatturePdfBusiness.createTransientStampaFattura(ses, fattura);
			}
			if (sf == null) throw new ServletException("Incorrect id");
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
		} catch (JRException | BusinessException | HibernateException | IOException e) {
			throw new ServletException(e.getMessage());
		} finally {
			ses.close();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
