package it.giunti.apg.server.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.UtilService;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.business.HttpClientBusiness;
import it.giunti.apg.core.persistence.FileUploadsDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.RinnoviMassiviDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.RinnoviMassivi;

public class UtilServiceImpl extends RemoteServiceServlet implements UtilService {
	private static final long serialVersionUID = -8020867457648219678L;
	private static final Logger LOG = LoggerFactory.getLogger(UtilServiceImpl.class);

	@Override
	public String getApgTitle() throws EmptyResultException {
		return PropertyReader.getApgTitle();
	}
	
	@Override
	public String getApgStatus() throws EmptyResultException {
		return PropertyReader.getApgStatus();
	}
	
	@Override
	public String getApgMenuImage() throws EmptyResultException {
		return PropertyReader.getApgMenuImage();
	}
	
	@Override
	public String getApgLoginImage() throws EmptyResultException {
		return PropertyReader.getApgLoginImage();
	}
	
	@Override
	public String getApgVersion() throws EmptyResultException {
		return PropertyReader.getApgVersion();
	}


	// Rinnovi massivi
	
	
	@Override
	public List<RinnoviMassivi> findRinnoviMassivi(Integer idPeriodico) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<RinnoviMassivi> result = null;
		try {
			result = new RinnoviMassiviDao().findByPeriodico(ses, idPeriodico);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Boolean saveOrUpdateRinnoviMassiviList(
			List<RinnoviMassivi> rinnoviMassiviList) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		RinnoviMassiviDao rmDao = new RinnoviMassiviDao();
		try {
			for (RinnoviMassivi rm:rinnoviMassiviList) {
				RinnoviMassivi persistent = null;
				if (rm.getId() != null) {
					persistent = GenericDao.findById(ses, RinnoviMassivi.class, rm.getId());
				}
				if (persistent != null) {
					rmDao.update(ses, rm);
				} else {
					rmDao.save(ses, rm);
				}
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}

	@Override
	public Boolean deleteRinnovoMassivo(Integer idRinnovoMassivo)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			RinnoviMassivi persistent = null;
			if (idRinnovoMassivo != null) {
				persistent = GenericDao.findById(ses, RinnoviMassivi.class, idRinnovoMassivo);
			}
			if (persistent != null) {
				new RinnoviMassiviDao().delete(ses, persistent);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}

	
	// FileUploads
	

	@Override
	public List<FileUploads> findFileUploadsStripped() throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<FileUploads> result = null;
		try {
			result = GenericDao.findByClass(ses, FileUploads.class, "id");
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				for (FileUploads fu:result) {
					fu.setContent(null);
				}
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	

	@Override
	public Boolean deleteFileUpload(Integer idFileUpload)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			FileUploads persistent = null;
			if (idFileUpload != null) {
				persistent = GenericDao.findById(ses, FileUploads.class, idFileUpload);
			}
			if (persistent != null) {
				new FileUploadsDao().delete(ses, persistent);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}

	
	// Install info
	
	
	@Override
	public String getApguiInstallInfo(String appBaseUrl) throws IOException {
		return getInstallInfo(appBaseUrl, AppConstants.URL_APG_UI_INSTALL_PAGE);
	}
	
	@Override
	public String getApgwsInstallInfo(String appBaseUrl) throws IOException {
		return getInstallInfo(appBaseUrl, AppConstants.URL_APG_WS_INSTALL_PAGE);
	}

	@Override
	public String getApgautomationInstallInfo(String appBaseUrl) throws IOException {
		return getInstallInfo(appBaseUrl, AppConstants.URL_APG_AUTOMATION_INSTALL_PAGE);
	}
	
	private String getInstallInfo(String appBaseUrl, String servletUrl) throws IOException {
		URL baseUrl = new URL(appBaseUrl);
		String destinationUrl = baseUrl.getProtocol()+"://"+baseUrl.getHost()+":"+
				baseUrl.getPort()+servletUrl;
		String response = HttpClientBusiness.sendGet(destinationUrl);
		String result = "<i>Dati non disponibili</i>";
		if (response != null) {
			Pattern p = Pattern.compile("<body>(\\S+)</body>");
			Matcher m = p.matcher(response);
			if (m.find()) {
				result = m.group(1);
			}
		}
		return result;
	}
	
}
