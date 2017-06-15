package it.giunti.apg.server.services;

import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.server.SerializationUtil;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.ServerUtil;
import it.giunti.apg.server.business.FascicoliBusiness;
import it.giunti.apg.server.business.PagamentiMatchBusiness;
import it.giunti.apg.server.business.RinnovoBusiness;
import it.giunti.apg.server.persistence.AbbonamentiDao;
import it.giunti.apg.server.persistence.ContatoriDao;
import it.giunti.apg.server.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.server.persistence.EvasioniFascicoliDao;
import it.giunti.apg.server.persistence.FascicoliDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.PagamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AbbonamentiServiceImpl extends RemoteServiceServlet implements AbbonamentiService  {
	private static final long serialVersionUID = 4728843414210656049L;

	private static final Logger LOG = LoggerFactory.getLogger(AbbonamentiServiceImpl.class);

	
	@Override
	public Integer countIstanzeByCodice(String codice) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer count = null;
		try {
			count = new IstanzeAbbonamentiDao().countIstanzeByCodice(ses, codice);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return count;
	}

	@Override
	public Integer countIstanzeByTipoAbbonamento(Integer idTipoAbbonamento, Date date)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer count = null;
		try {
			count = new IstanzeAbbonamentiDao().countIstanzeByTipoAbbonamento(ses, idTipoAbbonamento, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return count;
	}
	
	@Override
	public List<IstanzeAbbonamenti> quickSearchIstanzeAbbonamenti(String searchString, int offset, int size)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().quickSearchIstanzeAbbonamenti(ses, searchString, offset, size);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(result);
	}
	
	@Override
	public Abbonamenti findAbbonamentiByCodice(String codice)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Abbonamenti result = null;
		try {
			result = new AbbonamentiDao().findAbbonamentiByCodice(ses, codice);
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
	public Abbonamenti findAbbonamentiById(Integer id)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Abbonamenti result = null;
		try {
			result = GenericDao.findById(ses, Abbonamenti.class, id);
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
	public List<IstanzeAbbonamenti> findIstanzeByCodice(String codice,
			int offset, int size) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeByCodice(ses, codice, offset, size);
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
	public IstanzeAbbonamenti findIstanzeById(Integer id)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti result = null;
		try {
			result = GenericDao.findById(ses, IstanzeAbbonamenti.class, id);
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
	public List<IstanzeAbbonamenti> findIstanzeByAbbonamento(Integer idAbbonamento)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeByAbbonamento(ses, idAbbonamento);
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
	public List<IstanzeAbbonamenti> findIstanzeConCreditoBySocieta(String idSocieta, int monthsExpired, boolean regalo, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeConCreditoBySocieta(ses, idSocieta, monthsExpired, regalo, offset, pageSize);
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
	public List<IstanzeAbbonamenti> findIstanzeProprieByAnagrafica(
			Integer idAnagr, boolean onlyLatest, int offset, int size)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeProprieByAnagrafica(ses,
					idAnagr, onlyLatest, offset, size);
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
	public List<IstanzeAbbonamenti> findIstanzeRegalateByAnagrafica(
			Integer idAnagr, boolean onlyLatest, int offset, int size)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeRegalateByAnagrafica(ses,
					idAnagr, onlyLatest, offset, size);
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
	public List<IstanzeAbbonamenti> findIstanzePromosseByAnagrafica(
			Integer idAnagr, boolean onlyLatest, int offset, int size)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzePromosseByAnagrafica(ses,
					idAnagr, onlyLatest, offset, size);
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
	public IstanzeAbbonamenti createAbbonamentoAndIstanza(Integer idAbbonato, Integer idPagante, Integer idAgente, Integer idPeriodico)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		IstanzeAbbonamenti result = null;
		try {
			result = new IstanzeAbbonamentiDao().createAbbonamentoAndIstanza(ses, idAbbonato, idPagante, idAgente, idPeriodico);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
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
	public IstanzeAbbonamenti makeBasicRenewal(Integer idOldIstanza, String userId)
			throws BusinessException, EmptyResultException {
		IstanzeAbbonamenti result = RinnovoBusiness.makeBasicRenewal(idOldIstanza, true, true, userId);
		return SerializationUtil.makeSerializable(result);
	}
	
	@Override
	public IstanzeAbbonamenti makeBasicRegeneration(Integer idOldIstanza, String userId)
			throws BusinessException, EmptyResultException {
		IstanzeAbbonamenti result = RinnovoBusiness.makeBasicRenewal(idOldIstanza, false, false, userId);
		return SerializationUtil.makeSerializable(result);
	}
	
	@Override
	public String createCodiceAbbonamento(Integer idPeriodico) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		String result = null;
		try {
			result = new ContatoriDao().createCodiceAbbonamento(ses, idPeriodico);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
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
	public Boolean findCodiceAbbonamento(String codiceAbbonamento) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Boolean result = null;
		try {
			result = new AbbonamentiDao().findCodiceAbbonamento(ses, codiceAbbonamento);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Boolean findCodiceAbbonamentoIfDifferentAbbonato(String codiceAbbonamento, Integer idAbbonato) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Boolean result = null;
		try {
			result = new AbbonamentiDao().findCodiceAbbonamentoIfDifferentAbbonato(ses, codiceAbbonamento, idAbbonato);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Integer save(IstanzeAbbonamenti item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idIa = null;
		Transaction trx = ses.beginTransaction();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		try {
			idIa = iaDao.save(ses, item);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idIa;
	}
	
	@Override
	public Integer update(IstanzeAbbonamenti item, boolean assignNewCodiceAbbonamento) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idIa = null;
		Transaction trx = ses.beginTransaction();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		try {
			if (assignNewCodiceAbbonamento) {
				String codiceAbbonamento = new ContatoriDao().createCodiceAbbonamento(ses,
						item.getFascicoloInizio().getPeriodico().getId());
				item.getAbbonamento().setCodiceAbbonamento(codiceAbbonamento);
			}
			idIa = iaDao.update(ses, item, true);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idIa;
	}

	@Override
	public Integer saveWithPayment(IstanzeAbbonamenti item, Pagamenti pagamento) throws BusinessException, ValidationException {
		Integer idIa = save(item);
		Date now = new Date();
		pagamento.setDataAccredito(now);
		pagamento.setCodiceAbbonamentoMatch(item.getAbbonamento().getCodiceAbbonamento());
		pagamento.setIdSocieta(item.getAbbonamento().getPeriodico().getIdSocieta());
		Anagrafiche anag = item.getAbbonato();
		if (item.getPagante() != null) anag = item.getPagante();
		pagamento.setAnagrafica(anag);
		double dovuto;
	
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			//Salvataggio pagamento
			if (pagamento.getId() == null) {
				new PagamentiDao().save(ses, pagamento);
			}
			dovuto = PagamentiMatchBusiness.getIstanzaTotalPrice(ses, idIa);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//Tentativo di abbinamento (con fatturazione)
		ArrayList<Integer> idOpzList = new ArrayList<Integer>();
		if (item.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:item.getOpzioniIstanzeAbbonamentiSet()) {
				idOpzList.add(oia.getOpzione().getId());
			}
		}
		PagamentiServiceImpl impl = new PagamentiServiceImpl();

		if (Math.abs(pagamento.getImporto()-dovuto) < AppConstants.SOGLIA) {
			//Pagamento OK
			List<Integer> idPagList = new ArrayList<Integer>();
			idPagList.add(pagamento.getId());
			impl.processPayment(now, idPagList, null, idIa, idOpzList, pagamento.getIdUtente());
		} else {
			//Pagamento errato
			ses = SessionFactory.getSession();
			trx = ses.beginTransaction();
			try {
				pagamento.setIdErrore(AppConstants.PAGAMENTO_ERR_IMPORTO);
				new PagamentiDao().update(ses, pagamento);
				trx.commit();
			} catch (HibernateException e) {
				trx.rollback();
				LOG.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} finally {
				ses.close();
			}
		}
		return idIa;
	}

	@Override
	public String saveWithAnagraficaAndPayment(Anagrafiche anag,
			IstanzeAbbonamenti ia, Pagamenti pagamento)
			throws BusinessException, ValidationException {
		//Salvataggio anagrafica
		new AnagraficheServiceImpl().saveOrUpdate(anag);
		//anagrafica e pagamento
		ia.setAbbonato(anag);
		ia.setIdAbbonatoT(anag.getId()+"");
		if (pagamento != null) {
			saveWithPayment(ia, pagamento);
		} else {
			save(ia);
		}
		return ia.getAbbonamento().getCodiceAbbonamento();
	}
	
	@Override
	public Date calculateDataFine(Date inizio, Integer months) {
		if (inizio == null) {
			inizio = new Date();
		}
		inizio = ServerUtil.getMonthFirstDay(inizio);
		Calendar cal = new GregorianCalendar();
		cal.setTime(inizio);
		cal.add(Calendar.MONTH, months-1);
		Date fine = cal.getTime();
		return ServerUtil.getMonthLastDay(fine);
	}

	@Override
	public IstanzeAbbonamenti changePeriodico(IstanzeAbbonamenti istanzaT /*transient*/, 
			Integer idPeriodico, String siglaTipoAbbonamento) throws BusinessException {
		if (istanzaT == null) return null;
		Session ses = SessionFactory.getSession();
		try {
			istanzaT = FascicoliBusiness.changePeriodico(ses, istanzaT, idPeriodico, siglaTipoAbbonamento);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(istanzaT);
	}
	
	@Override
	public IstanzeAbbonamenti changeFascicoloInizio(IstanzeAbbonamenti istanzaT /*transient*/, 
			Integer idFascicolo, String siglaTipoAbbonamento) throws BusinessException {
		if (istanzaT == null) return null;
		Session ses = SessionFactory.getSession();
		try {
			FascicoliBusiness.changeFascicoloInizio(ses, istanzaT, idFascicolo, siglaTipoAbbonamento);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(istanzaT);
	}
	
	@Override
	public IstanzeAbbonamenti changeListino(IstanzeAbbonamenti istanzaT /*transient*/, 
			Integer idListino) throws BusinessException {
		Session ses = SessionFactory.getSession();
		FascicoliDao fasDao = new FascicoliDao();
		try {
			Listini lst = GenericDao.findById(ses, Listini.class, idListino);
			istanzaT.setListino(lst);
			Fascicoli fascicoloInizio = fasDao.findFascicoloByPeriodicoDataInizio(ses,
					lst.getTipoAbbonamento().getPeriodico().getId(),
					istanzaT.getFascicoloInizio().getDataInizio());
			
			//Verifica se il listino prevede un mese fisso di inizio istanza
			if (lst.getMeseInizio() != null) {
				fascicoloInizio = fasDao.changeFascicoloToMatchStartingMonth(ses,
						lst/*, fascicoloInizio*/);
				istanzaT.setFascicoloInizio(fascicoloInizio);
			}
			
			//Cambia fascicolo finale
			FascicoliBusiness.setupFascicoloFine(ses, istanzaT);
			
			//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
			if (istanzaT.getId() == null || (!istanzaT.getListino().equals(lst))) { 
				istanzaT.setListino(lst);
				istanzaT.setDataCambioTipo(new Date());
				istanzaT.setFascicoliTotali(lst.getNumFascicoli());
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(istanzaT);
	}

	@Override
	public String getStatusMessage(Integer idIstanza) throws BusinessException {
		Session ses = SessionFactory.getSession();
		String text = "";
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			text = buildStatusMessage(ia);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return text;
	}
	private String buildStatusMessage(IstanzeAbbonamenti ia) {
		String result = "";
		if (ia != null) {
				if (ia.getDataDisdetta() != null) {
					result += "con disdetta "+ServerConstants.FORMAT_DAY.format(ia.getDataDisdetta())+" ";
				} else {
					Date today = new Date();
					boolean spedibile = IstanzeStatusUtil.isSpedibile(ia);
					if (spedibile) {
						if (ia.getFascicoloFine().getDataInizio().before(today)) {
							result += "terminato con il n&deg; " + ia.getFascicoloFine().getTitoloNumero() +
									" del " + ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataInizio())+" ";
						}
					} else {
						if (!ia.getListino().getInvioSenzaPagamento()) {
							result += "non pagato ";
						}
						if (ia.getFascicoloFine().getDataInizio().before(today)) {
							result += "moroso ";
						}
					}
				}
			if (ia.getInvioBloccato()) result += " bloccato ";
		}
		return result;
	}

	@Override
	public Boolean deleteIstanza(Integer idIstanza) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			Abbonamenti abbo = ia.getAbbonamento();
			PagamentiDao pagaDao = new PagamentiDao();
			EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
			EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
			OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			//Pagamenti
			List<Pagamenti> pagList = pagaDao.findPagamentiByIstanzaAbbonamento(ses, ia.getId());
			for (Pagamenti p:pagList) pagaDao.delete(ses, p);
			//Fascicoli
			List<EvasioniFascicoli> fasList = efDao.findByIstanza(ses, ia);
			for (EvasioniFascicoli ef:fasList) efDao.delete(ses, ef);
			//Comunicazioni
			List<EvasioniComunicazioni> comList = ecDao.findByIstanza(ses, ia.getId());
			for (EvasioniComunicazioni ec:comList) ecDao.delete(ses, ec.getId());
			//Opzioni
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) oiaDao.delete(ses, oia);
			//Istanza
			iaDao.delete(ses, ia);
			//Se non ci sono altre istanze cancella anche Abbonamenti!!
			List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByAbbonamento(ses, abbo.getId());
			if (iaList == null) iaList = new ArrayList<IstanzeAbbonamenti>();
			if (iaList.size() == 0) {
				//cancella 'Abbonamenti'
				new AbbonamentiDao().delete(ses, abbo);
			} else {
				//se ci sono altre istanze allora marca l'ultima
				iaDao.markUltimaDellaSerie(ses, abbo);
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
	public List<IstanzeAbbonamenti> findIstanzeByLastModified(
			Integer idPeriodico, int offset,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeByLastModified(ses, idPeriodico, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(result);
	}

	//@Override
	//public Double calculateImportoTotale(Integer idTipoAbbonamentoListino,
	//		Integer copie, Set<Integer> idSuppSet) throws PagamentiException {
	//	Session ses = SessionFactory.getSession();
	//	Double result = null;
	//	try {
	//		Listini tal = GenericDao.findById(ses, Listini.class, idTipoAbbonamentoListino);
	//		result = tal.getPrezzo();
	//		for (Integer idSupp:idSuppSet) {
	//			Opzioni opz = GenericDao.findById(ses, Opzioni.class, idSupp);
	//			result += opz.getPrezzo();
	//		}
	//		result = new PagamentiDao().getImportoTotale(ses, idTipoAbbonamentoListino, copie, idSuppSet);
	//	} catch (HibernateException e) {
	//		LOG.error(e.getMessage(), e);
	//		throw new PagamentiException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	return result;
	//}

	@Override
	public Boolean verifyTotaleNumeri(Integer idIstanza)
			throws BusinessException, ValidationException {
		Session ses = SessionFactory.getSession();
		FascicoliDao fasDao = new FascicoliDao();
		Boolean corrisponde = false;
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			Integer numPrevisti = ia.getListino().getNumFascicoli();
			Integer numCalcolati = 0;
			List<Fascicoli> fasList = fasDao.findFascicoliBetweenDates(ses,
					ia.getAbbonamento().getPeriodico().getId(),
					ia.getFascicoloInizio().getDataInizio(),
					ia.getFascicoloFine().getDataFine());
			for (Fascicoli fas:fasList) {
				numCalcolati += fas.getFascicoliAccorpati();
			}
			corrisponde = (numPrevisti.equals(numCalcolati));
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (!corrisponde) {
			throw new ValidationException("Il totale dei numeri non è quello previsto dal tipo abbonamento.");
		}
		return false;
	}
	
	@Override
	public Boolean verifyPagante(Integer idIstanza)
			throws BusinessException, ValidationException {
		Session ses = SessionFactory.getSession();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			Anagrafiche pagante = ia.getPagante();
			Listini listino = ia.getListino();
			if (!listino.getTipoAbbonamento().getPermettiPagante() && (pagante != null)) {
				throw new ValidationException("E' stato inserito un pagante per " +
						"un abbonamento '"+listino.getTipoAbbonamento().getCodice()+"'");
			}
			if (listino.getTipoAbbonamento().getPermettiPagante() && (pagante == null)) {
				throw new ValidationException("L'abbonamento '"+listino.getTipoAbbonamento().getCodice()+"' " +
						"non ha un pagante");
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return false;
	}

	@Override
	public Boolean verifyMacroarea(Integer idIstanza)
			throws BusinessException, ValidationException {
		Session ses = SessionFactory.getSession();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			Macroaree macroareaAbbonato = ia.getAbbonato()
					.getIndirizzoPrincipale().getNazione().getMacroarea();
			Listini listino = ia.getListino();
			if (!listino.getIdMacroarea().equals(macroareaAbbonato.getId())) {
				throw new ValidationException("Il tipo abbonamento non e' compatibile con la zona geografica '" +
						macroareaAbbonato.getNome()+"'");
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return false;
	}

	@Override
	public Boolean isRenewable(Integer idIstanza) throws BusinessException {
		if (idIstanza == null) return false;
		Session ses = SessionFactory.getSession();
		Boolean result = false;
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			if (ia != null) {
				if (ia.getUltimaDellaSerie()) {
					if (ia.getAbbonamento().getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_VARIA)) {
						//Se è periodico VARIA allora:
						if (IstanzeStatusUtil.isInRegola(ia)) {
							//Devono essere passati 2 giorni dall'ultimo rinnovo
							Calendar cal = new GregorianCalendar();
							cal.setTime(ia.getDataCreazione());
							cal.add(Calendar.DAY_OF_MONTH, AppConstants.SOGLIA_TEMPORALE_GIORNI_RINNOVA);
							Date soglia = cal.getTime();
							if (new Date().after(soglia)) {
								result = true;
							}
						}
					} else {
						//Se invece è periodico scolastico, è rinnovabile solo se esiste un fascicolo a un anno dalla fine
						Calendar cal = new GregorianCalendar();
						cal.setTime(ia.getFascicoloFine().getDataInizio());
						cal.add(Calendar.YEAR, 1);
						Fascicoli fas = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses,
								ia.getFascicoloInizio().getPeriodico().getId(), cal.getTime());
						result = (fas != null);
					}
				}
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Boolean isRegenerable(Integer idIstanza) throws BusinessException {
		if (idIstanza == null) return false;
		Session ses = SessionFactory.getSession();
		Boolean result = false;
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			if (ia != null) {
				if (ia.getUltimaDellaSerie()) {
					//Devono essere passati almeno 6 mesi dall'ultimo rinnovo/creazione anche se non pagato
					Calendar cal = new GregorianCalendar();
					cal.setTime(ia.getFascicoloInizio().getDataInizio());
					cal.add(Calendar.MONTH, AppConstants.SOGLIA_TEMPORALE_MESI_RIGENERA);
					if (new Date().after(cal.getTime())) {
						if (ia.getAbbonamento().getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_VARIA)) {
							result = true;
						} else {
							//Se invece è periodico scolastico, è rinnovabile solo se esiste un fascicolo a un anno dalla fine
							cal = new GregorianCalendar();
							cal.setTime(ia.getFascicoloFine().getDataInizio());
							cal.add(Calendar.YEAR, 1);
							Fascicoli fas = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses,
									ia.getFascicoloInizio().getPeriodico().getId(), cal.getTime());
							result = (fas != null);
						}
					}
				}
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public IstanzeAbbonamenti changeListinoAndOpzioni(Integer idIa,
			Integer selectedIdListino, Integer copie, List<Integer> selectedIdOpzList,
			String idUtente)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
		IstanzeAbbonamenti ia = null;
		try {
			Date now = new Date();
			ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			//IA: set new listino
			if (selectedIdListino != null) {
				if (ia.getListino().getId() != selectedIdListino) {
					Listini lis = GenericDao.findById(ses, Listini.class, selectedIdListino);
					ia.setListino(lis);
					ia.setDataCambioTipo(now);
					ia.setDataModifica(now);
					iaDao.updateUnlogged(ses, ia);
				}
			}
			//IA: set copie
			if (copie != null) {
				if (ia.getCopie() != copie) {
					ia.setCopie(copie);
					ia.setDataModifica(now);
					iaDao.updateUnlogged(ses, ia);
				}
			}
			//IA: delete removed opzioni
			Set<OpzioniIstanzeAbbonamenti> oiaSet = new HashSet<OpzioniIstanzeAbbonamenti>();
			oiaSet.addAll(ia.getOpzioniIstanzeAbbonamentiSet());
			for (OpzioniIstanzeAbbonamenti oia:oiaSet) {
				boolean included = false;
				for (Integer idOpz:selectedIdOpzList) {
					if (oia.getOpzione().getId() == idOpz) included = true;
				}
				if (!included) {
					ia.getOpzioniIstanzeAbbonamentiSet().remove(oia);
					oiaDao.delete(ses, oia);
				}
			}
			//IA: add additional opzioni
			for (Integer idOpz:selectedIdOpzList) {
				boolean present = false;
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					if (oia.getOpzione().getId() == idOpz) present = true;
				}
				if (!present) {
					Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
					OpzioniIstanzeAbbonamenti newOia = new OpzioniIstanzeAbbonamenti();
					newOia.setIstanza(ia);
					newOia.setOpzione(opz);
					oiaDao.save(ses, newOia);
				}
			}
			ia.setIdUtente(idUtente);
			iaDao.update(ses, ia);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		SerializationUtil.makeSerializable(ia);
		return ia;
	}

	@Override
	public IstanzeAbbonamenti findLastIstanzaByCodice(String codice)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti ia = null;
		try {
			ia = new IstanzeAbbonamentiDao()
					.findUltimaIstanzaByCodice(ses, codice);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		SerializationUtil.makeSerializable(ia);
		return ia;
	}

}