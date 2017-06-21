package it.giunti.apg.server.services;

import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.persistence.AdesioniDao;
import it.giunti.apg.core.persistence.AliquoteIvaDao;
import it.giunti.apg.core.persistence.FileResourcesDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.RinnoviMassiviDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.RinnoviMassivi;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LookupServiceImpl extends RemoteServiceServlet implements LookupService  {
	private static final long serialVersionUID = 1685101630678662026L;
	
	private static final Logger LOG = LoggerFactory.getLogger(LookupServiceImpl.class);

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
				GenericDao.deleteGeneric(ses, idFileUpload, persistent);
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

	
	
	//Adesioni
	
	
	@Override
	public List<Adesioni> findAdesioni(String filterPrefix, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Adesioni> result = null;
		try {
			result = new AdesioniDao().findByPrefix(ses, filterPrefix, offset, pageSize);
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
	public Integer saveOrUpdateAdesione(Adesioni item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		AdesioniDao adesioniDao = new AdesioniDao();
		try {
			if (item.getId() != null) {
				adesioniDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) adesioniDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idReg;
	}
	
	@Override
	public Adesioni findAdesioneById(Integer idAdesione) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Adesioni result = null;
		try {
			result = GenericDao.findById(ses, Adesioni.class, idAdesione);
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
	public Adesioni createAdesione() {
		return new Adesioni();
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
				GenericDao.deleteGeneric(ses, idRinnovoMassivo, persistent);
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


}
