package it.giunti.apg.server.services;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.core.persistence.AliquoteIvaDao;
import it.giunti.apg.core.persistence.FileResourcesDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

public class LookupServiceImpl extends RemoteServiceServlet implements LookupService  {
	private static final long serialVersionUID = 1685101630678662026L;
	
	private static final Logger LOG = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Override
	public List<Periodici> findPeriodici() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Periodici> result = null;
		try {
			result = GenericDao.findByClass(ses, Periodici.class, "nome");
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
	public List<Periodici> findPeriodici(Date extractionDt) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Periodici> result = null;
		try {
			result = new PeriodiciDao().findByDate(ses, extractionDt);
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
	public List<Periodici> findPeriodici(Integer selectedId, Date extractionDt)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Periodici> result = null;
		try {
			result = new PeriodiciDao().findByDateOrId(ses, selectedId, extractionDt);
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
	public Societa findSocietaById(String idSocieta) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Societa result = null;
		try {
			result = GenericDao.findById(ses, Societa.class, idSocieta);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<Province> findProvince() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Province> result = null;
		try {
			result = GenericDao.findByClass(ses, Province.class, "id");
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
	public List<Nazioni> findNazioni() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Nazioni> result = null;
		try {
			result = GenericDao.findByClass(ses, Nazioni.class, "nomeNazione");
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
	public List<Professioni> findProfessioni() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Professioni> result = null;
		try {
			result = GenericDao.findByClass(ses, Professioni.class, "nome");
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
	public List<TitoliStudio> findTitoliStudio() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<TitoliStudio> result = null;
		try {
			result = GenericDao.findByClass(ses, TitoliStudio.class, "nome");
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
	public List<Macroaree> findMacroaree() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Macroaree> result = null;
		try {
			result = GenericDao.findByClass(ses, Macroaree.class, "nome");
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
	public List<TipiDisdetta> findTipiDisdetta() throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<TipiDisdetta> result = null;
		try {
			result = GenericDao.findByClass(ses, TipiDisdetta.class, "id");
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
	public List<AliquoteIva> findAliquoteIva(Date selectionDate) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<AliquoteIva> result = null;
		try {
			result = new AliquoteIvaDao().findByDate(ses, selectionDate);
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
		return null;
	}

	@Override
	public List<FileResources> findFileResources(String fileType)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<FileResources> result = null;
		try {
			result = new FileResourcesDao().findByType(ses, fileType);
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
	

}
