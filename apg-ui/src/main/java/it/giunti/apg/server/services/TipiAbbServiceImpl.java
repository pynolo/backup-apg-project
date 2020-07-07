package it.giunti.apg.server.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.core.SerializationUtil;
import it.giunti.apg.core.persistence.AliquoteIvaDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoDao;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

public class TipiAbbServiceImpl extends RemoteServiceServlet implements TipiAbbService  {
	private static final long serialVersionUID = 4531817124440920255L;
	
	private static final Logger LOG = LoggerFactory.getLogger(TipiAbbServiceImpl.class);

	
	//TipiAbbonamento
	

	
	@Override
	public List<TipiAbbonamento> findTipiAbbonamentoByPeriodicoDate(
			Integer idPeriodico, Integer selectedId, Date beginDate)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<TipiAbbonamento> result = null;
		try {
			result = new TipiAbbonamentoDao().findByPeriodicoDate(ses, idPeriodico, selectedId, beginDate);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<TipiAbbonamento> findTipiAbbonamentoByPeriodico(
			Integer idPeriodico, Integer selectedId)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<TipiAbbonamento> result = null;
		try {
			result = new TipiAbbonamentoDao().findByPeriodico(ses, idPeriodico, selectedId);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	
	
	//Listini
	
	
	
	@Override
	public Listini findListinoById(Integer id)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Listini result = null;
		try {
			result = GenericDao.findById(ses, Listini.class, id);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return SerializationUtil.makeSerializable(result);
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Listini findDefaultListinoByPeriodicoDate(
			Integer idPeriodico, Date date) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ListiniDao lstDao = new ListiniDao();
		Listini result = null;
		try {
			result = lstDao.findDefaultListinoByPeriodicoDate(ses,
					idPeriodico, AppConstants.DEFAULT_TIPO_ABBO, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return SerializationUtil.makeSerializable(result);
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Listini findDefaultListinoByInizio(
			Integer idPeriodico, Date dataInizio) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ListiniDao lstDao = new ListiniDao();
		Listini result = null;
		try {
			result = lstDao.findDefaultListinoByInizio(ses,
					idPeriodico, AppConstants.DEFAULT_TIPO_ABBO, dataInizio);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return SerializationUtil.makeSerializable(result);
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<Listini> findListiniByPeriodicoDate(Integer idPeriodico, Date dt, Integer selectedId, int offset, int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Listini> result = null;
		try {
			result = new ListiniDao().findListiniByPeriodicoDate(ses, idPeriodico, dt, selectedId, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<Listini> findListiniByInizio(Integer idPeriodico, Date dataInizio, Integer selectedId, int offset, int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Listini> result = null;
		try {
			result = new ListiniDao().findListiniByInizio(ses, idPeriodico, dataInizio, selectedId, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<Listini> findListiniByTipoAbb(Integer idTipoAbb, int offset,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Listini> result = null;
		try {
			result = new ListiniDao().findListiniByTipoAbb(ses, idTipoAbb, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Listini findListinoByTipoAbbDate(Integer idTipoAbb, Date dt) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Listini result = null;
		try {
			result = new ListiniDao().findListinoByTipoAbbDate(ses, idTipoAbb, dt);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return SerializationUtil.makeSerializable(result);
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Listini createListinoFromPeriodico(Integer idPeriodico)
			throws BusinessException, EmptyResultException {
		if (idPeriodico == null) throw new EmptyResultException("idPeriodico e' vuoto, impossibile creare il tipo abbonamento");
		return createListino(null, idPeriodico);
	}	
	@Override
	public Listini createListinoFromTipo(Integer idTipoAbbonamento)
			throws BusinessException, EmptyResultException {
		if (idTipoAbbonamento == null) throw new EmptyResultException("idTipoAbbonamento e' vuoto, impossibile creare il tipo abbonamento");
		return createListino(idTipoAbbonamento, null);
	}
	private Listini createListino(Integer idTipoAbbonamento, Integer idPeriodico)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Date today = DateUtil.now();
		Listini listino = new Listini();
		listino.setDurataMesi(12);
		TipiAbbonamento ta = null;
		Periodici periodico = null;
		if (idPeriodico != null) {
			//E' un listino+tipo totalmente nuovo
			ta = new TipiAbbonamento();
			periodico = (Periodici) ses.get(Periodici.class, idPeriodico);
			ta.setPeriodico(periodico);
			ta.setCodice("");
		}
		try {
			AliquoteIvaDao ivaDao = new AliquoteIvaDao();
			listino.setAliquotaIva(ivaDao.findDefaultAliquotaIvaByDate(ses, AppConstants.DEFAULT_ALIQUOTA_IVA, today));
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		listino.setTipoAbbonamento(ta);
		listino.setCartaceo(true);
		listino.setDataFine(null);
		listino.setDataInizio(today);
		listino.setIdMacroarea(AppConstants.DEFAULT_MACROAREA);
		listino.setInvioSenzaPagamento(false);
		listino.setFatturaDifferita(false);
		listino.setMeseInizio(null);
		listino.setGracingInizialeMesi(0);
		listino.setGracingFinaleMesi(0);
		listino.setPrezzo(0D);
		listino.setUid(null);
		return SerializationUtil.makeSerializable(listino);
	}

	@Override
	public Integer saveOrUpdate(Listini lst, List<Integer> tipiAbbRinnovoList) throws BusinessException {
		TipiAbbonamentoDao taDao = new TipiAbbonamentoDao();
		ListiniDao lstDao = new ListiniDao();
		TipiAbbonamento ta = lst.getTipoAbbonamento();
		Session ses = SessionFactory.getSession();
		//Codifica gli id in oggetti incapsulati
		try {
			Integer idPeriodico = ValueUtil.stoi(ta.getIdPeriodicoT());
			Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
			ta.setPeriodico(periodico);
			if (lst.getIdAliquotaIvaT() != null) {
				Integer id = Integer.valueOf(lst.getIdAliquotaIvaT());
				AliquoteIva iva = GenericDao.findById(ses, AliquoteIva.class, id);
				lst.setAliquotaIva(iva);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//Effettua il vero salvataggio
		ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Integer lstId;
		try {
			//Se Tipo abbonamento è nuovo lo salva
			if (ta.getId() == null) {
				Integer taId = (Integer) taDao.save(ses, ta);
				ta = GenericDao.findById(ses, TipiAbbonamento.class, taId);
				lst.setUid("");//aggiornato subito sotto
				lst.setTipoAbbonamento(ta);
			} else {
				taDao.update(ses, ta);
			}
			//Salva il listino (incluse OpzioniListini) 
			lstId = lstDao.saveOrUpdate(ses, lst);
			String uid = lstDao.createUidListino(lst);
			lst.setUid(uid);
			lstDao.saveOrUpdate(ses, lst);
			//Salva i tipi al rinnovo
			new TipiAbbonamentoRinnovoDao().replaceTipiRinnovoByListino(ses, lstId, tipiAbbRinnovoList);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (ValidationException e) {//Errori nell'assegnazione o rimozione opzioni
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return lstId;
	}
	
	@Override
	public Integer createVersion(Listini copiedLst, List<Integer> tipiAbbRinnovoList) throws BusinessException {
		//Crea una nuova istanza dai valori copiati
		ListiniDao lstDao = new ListiniDao();
		Listini newLst = new Listini();
		try {
			PropertyUtils.copyProperties(newLst, copiedLst);
		} catch (Exception e) {	}
		newLst.setId(null);
		newLst.setUid(null);
		//Effettua il vero salvataggio della nuova istanza
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		TipiAbbonamento ta = null;
		Integer newLstId;
		try {
			ta = newLst.getTipoAbbonamento();
			Listini oldLst = GenericDao.findById(ses, Listini.class, copiedLst.getId());
			if (oldLst.getDataInizio().equals(newLst.getDataInizio()) ||
					oldLst.getDataInizio().after(newLst.getDataInizio()) ) {
				throw new BusinessException("La data di inizio della nuova versione del deve essere successiva alla vecchia.");
			}
			//Aggiorna la testata (TipoAbbonamento)
			ses.merge(ta);//taDao.update(ses, ta.getId(), ta);
			//Aggiorna la vecchia versione con la dataFine-1
			Calendar cal = new GregorianCalendar();
			cal.setTime(newLst.getDataInizio());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			oldLst.setDataFine(cal.getTime());
			newLst.setUid("");
			ses.merge(oldLst); //taDao.update(ses, oldTal.getId(), oldTal);
			//Salva il nuovo listino
			newLstId = (Integer) lstDao.save(ses, newLst);
			//UID
			String uid = lstDao.createUidListino(newLst);
			newLst.setUid(uid);
			lstDao.saveOrUpdate(ses, newLst);
			//Salva i tipi al rinnovo
			new TipiAbbonamentoRinnovoDao().replaceTipiRinnovoByListino(ses, newLstId, tipiAbbRinnovoList);
			trn.commit();
		} catch (HibernateException | ValidationException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return newLstId;
	}


	@Override
	public List<TipiAbbonamentoRinnovo> findTipiAbbonamentoRinnovoByListino(
			Integer idListino) throws BusinessException, EmptyResultException {
		if (idListino == null) throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
		Session ses = SessionFactory.getSession();
		List<TipiAbbonamentoRinnovo> result = null;
		try {
			result = new TipiAbbonamentoRinnovoDao().findByIdListinoOrdine(ses, idListino);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<String> findCodiceTipiAbbonamentoRinnovoByListino(
			Integer idListino) throws BusinessException, EmptyResultException {
		List<String> list = new ArrayList<String>();
		List<TipiAbbonamentoRinnovo> result = findTipiAbbonamentoRinnovoByListino(idListino);
		for (TipiAbbonamentoRinnovo listino:result) {
			String codice = listino.getTipoAbbonamento().getCodice();
			if (!list.contains(codice)) list.add(codice);
		}
		return list;
	}

}
