package it.giunti.apg.server.services;

import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.core.SerializationUtil;
import it.giunti.apg.core.business.FattureBusiness;
import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.FattureStampeDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PagamentiServiceImpl extends RemoteServiceServlet implements PagamentiService  {
	private static final long serialVersionUID = 3955292914560995776L;

	private static final Logger LOG = LoggerFactory.getLogger(PagamentiServiceImpl.class);
	private static PagamentiDao pagDao = new PagamentiDao();
	private static PagamentiCreditiDao credDao = new PagamentiCreditiDao();
	
	@Override
	public List<Pagamenti> findPagamentiByIstanzaAbbonamento(IstanzeAbbonamenti ia)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Pagamenti> result = null;
		try {
			result = pagDao.findPagamentiByIstanzaAbbonamento(ses, ia.getId());
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
	public List<Pagamenti> findPagamentiByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Pagamenti> result = null;
		try {
			result = pagDao.findByAnagrafica(ses, idAnagrafica, null, null);
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
	public Pagamenti findPagamentoById(Integer idPagamento) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Pagamenti result = null;
		try {
			result = GenericDao.findById(ses, Pagamenti.class, idPagamento);
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
	public List<Pagamenti> findPagamentiById(Integer idPagamento)
			throws BusinessException, EmptyResultException {
		Pagamenti pag = findPagamentoById(idPagamento);
		List<Pagamenti> pList = new ArrayList<Pagamenti>();
		pList.add(pag);
		return pList;
	}
	
	@Override
	public Integer saveOrUpdate(Pagamenti item)	throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idPag = null;
		Date today = new Date();
		Transaction trx = ses.beginTransaction();
		try {
			if (item.getIstanzaAbbonamento() != null) {
				ses.evict(item.getIstanzaAbbonamento());
				item.setCodiceAbbonamentoMatch(item.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento());
			}
			if (item.getCodiceAbbonamentoMatch() == null) item.setCodiceAbbonamentoMatch("");
			if (item.getAnagrafica() != null) ses.evict(item.getAnagrafica());
			if (item.getId() != null) {
				pagDao.update(ses, item);
				idPag = item.getId();
			} else {
				//salva
				if (item.getDataPagamento() == null) item.setDataPagamento(today);
				if (item.getDataAccredito() == null) item.setDataAccredito(today);
				idPag = (Integer) pagDao.save(ses, item);
			}
			ses.flush();
			ses.clear();
			if (item.getAnagrafica() == null && item.getIstanzaAbbonamento() != null) {
				if (item.getIstanzaAbbonamento().getPagante() != null) {
					item.setAnagrafica(item.getIstanzaAbbonamento().getPagante());
				} else {
					item.setAnagrafica(item.getIstanzaAbbonamento().getAbbonato());
				}
			}
			pagDao.updateNoLog(ses, item);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idPag;
	}
	
	@Override
	public Pagamenti createPagamentoManuale(Integer idAnagrafica) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Pagamenti result = null;
		try {
			Date today = new Date();
			result = new Pagamenti();
			Anagrafiche anag = (Anagrafiche) ses.get(Anagrafiche.class, idAnagrafica);
			result.setAnagrafica(anag);
			result.setDataCreazione(today);
			result.setDataPagamento(today);
			result.setDataAccredito(today);
			result.setImporto(0D);
			result.setIdTipoPagamento(AppConstants.PAGAMENTO_MANUALE);
			result.setIdErrore(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(result);
	}

	@Override
	public Double sumPagamentiByIstanzaAbbonamento(Integer idIstanza)
			throws BusinessException, ValidationException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti ia = null;
		Double result = null;
		try {
			ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			result = pagDao.sumPagamentiByIstanza(ses, idIstanza);
			result += new PagamentiCreditiDao().sumCreditiByIstanza(ses, idIstanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if ((ia.getListino().getPrezzo().doubleValue() < AppConstants.SOGLIA) &&
				(result.doubleValue() < AppConstants.SOGLIA)) {
			throw new ValidationException("L'abbonamento "+ia.getAbbonamento().getCodiceAbbonamento()+" non necessita di pagamento");
		}
		return result;
	}

	@Override
	public Boolean isPagato(Integer idIstanza) throws BusinessException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti result = null;
		try {
			result = (IstanzeAbbonamenti) GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result.getPagato();
	}
	
	@Override
	public List<PagamentiCrediti> findCreditiByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<PagamentiCrediti> result = null;
		try {
			result = new PagamentiCreditiDao().findByAnagrafica(ses, idAnagrafica, false);
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
	public List<PagamentiCrediti> findCreditiByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<PagamentiCrediti> result = new ArrayList<PagamentiCrediti>();
		try {
			result = new PagamentiCreditiDao().findByAnagraficaSocieta(ses, 
					idAnagrafica, idSocieta, stornati, false);
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
	public List<Pagamenti> findPagamentiFatturabiliByAnagraficaSocieta(Integer idIa, String idSocieta)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Pagamenti> result = new ArrayList<Pagamenti>();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			Anagrafiche pagante = ia.getAbbonato();
			if (ia.getPagante() != null) pagante = ia.getPagante();
			List<Pagamenti> pagList = new PagamentiDao().findByAnagrafica(ses, pagante.getId(), false, false);
			for (Pagamenti pag:pagList) {
				if (pag.getIdSocieta().equals(idSocieta)) {
					result.add(pag);
				}
			}
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
	public List<Pagamenti> findPagamentiConErrore(
			Integer idPeriodico,
			int offset, int pageSize) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Pagamenti> result = null;
		try {
			result = pagDao.findPagamentiConErrore(ses,
					idPeriodico, offset, pageSize);
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
	public List<PagamentiCrediti> findCreditiByIstanza(
			Integer idIstanzaAbbonamento) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<PagamentiCrediti> result = null;
		try {
			result = credDao.findByIstanza(ses, idIstanzaAbbonamento);
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
	public Boolean deletePagamento(Integer idPagamento)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		boolean result = false;
		try {
			Pagamenti pag = GenericDao.findById(ses, Pagamenti.class, idPagamento);
			if (pag != null) {
				pagDao.delete(ses, pag);
				result = true;
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Anagrafiche registraAnticipoFattura(Integer idPagamento,
			Integer idAnagrafica, String idUtente) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Pagamenti pag = null;
		Anagrafiche anag = null;
		Date now = new Date();
		try {
			//Abbinamento pagante
			pag = GenericDao.findById(ses, Pagamenti.class, idPagamento);
			anag = GenericDao.findById(ses, Anagrafiche.class, idAnagrafica);
			pag.setIstanzaAbbonamento(null);
			pag.setAnagrafica(anag);
			pag.setIdErrore(null);
			pag.setDataModifica(now);
			pag.setIdUtente(idUtente);
			pagDao.update(ses, pag);
			//Fattura
			//Pagamento doesn't match a subscription but only a client:
			//Make a deposit invoice
			Fatture fattura = PagamentiMatchBusiness.processPayment(ses, now,
					pag.getId(), pag.getAnagrafica().getId(), pag.getIdSocieta(), false, idUtente);
			List<Fatture> newFattureList = new ArrayList<Fatture>();
			newFattureList.add(fattura);
			
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if ((anag == null) || (pag == null)) {
			throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
		}
		//saveOrUpdate(pag);
		return anag;
	}
	
	@Override
	public Double getImportoTotale(Integer idIstanza) throws BusinessException,
			ValidationException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		Double result = null;
		try {
			result = PagamentiMatchBusiness.getIstanzaTotalPrice(ses, idIstanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Double getStimaImportoTotale(Integer idListino, Integer copie, Set<Integer> idOpzSet) throws BusinessException,
			ValidationException {
		if (idListino == null) return null;
		Session ses = SessionFactory.getSession();
		Double result = null;
		try {
			result = pagDao.getStimaImportoTotale(ses, idListino, copie, idOpzSet);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Double getCreditoByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati, Boolean fatturati)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Double result = 0D;
		try {
			result = new PagamentiCreditiDao().getCreditoByAnagraficaSocieta(ses, idAnagrafica, idSocieta, stornati, fatturati);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	/* Se idPagamento è presente, produce una fattura e la abbina a idPagamento e ia
	 * Ad ia devono essere associati solo idPagamento, crediti e resti usati PER SALDARE
	 * Alla fattura deve essere associato idPagamento, abbuoni (in negativo) e nuovi resti (futuri crediti)
	 */
	@Override
	public Fatture processPayment(Date dataFattura, List<Integer> idPagList, List<Integer> idCredList,
			Integer idIa, List<Integer> idOpzList, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Fatture fatt = null;
		try {
			fatt = PagamentiMatchBusiness.processPayment(ses, dataFattura, idPagList, idCredList, idIa, idOpzList, idUtente);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return fatt;
	}
	
	@Override
	public Fatture processPayment(Date dataFattura, Integer idPagamento, Integer idPagante, String idSocieta, String idUtente) 
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Fatture fatt = null;
		try {
			fatt = PagamentiMatchBusiness.processPayment(ses, 
					dataFattura, idPagamento, idPagante, idSocieta, false, idUtente);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return fatt;
	}

	@Override
	public Boolean verifyPagatoAndUpdate(Integer idIa) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Boolean result = true;
		try {
			result = PagamentiMatchBusiness.verifyPagatoAndUpdate(ses, idIa);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Double getImportoMancante(Integer idIstanza)
			throws BusinessException, ValidationException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti ia = null;
		Double result = null;
		try {
			ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			if (IstanzeStatusUtil.isFatturatoOppureOmaggio(ia))
				throw new ValidationException("Istanza che non necessita di pagamento");
			List<Integer> idOpzList = new ArrayList<Integer>();
			if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					idOpzList.add(oia.getOpzione().getId());
				}
			}
			result = PagamentiMatchBusiness.getMissingAmount(ses, idIstanza, idOpzList);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	
	@Override
	public List<Fatture> findFattureByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Fatture> result = null;
		try {
			result = new FattureDao().findByAnagraficaRemovingMissingPrints(ses, idAnagrafica, true);
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
	public Fatture findFatturaById(Integer idFattura) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Fatture result = null;
		try {
			result = GenericDao.findById(ses, Fatture.class, idFattura);
			if (result != null) {
				boolean stampaExists = new FattureStampeDao()
						.isFatturaStampa(ses, result.getIdFatturaStampa());
				if (!stampaExists) result.setIdFatturaStampa(null);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result == null) throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
		return result;
	}

	@Override
	public List<Fatture> findFattureByIstanza(Integer idIstanzaAbbonamento)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Fatture> result = null;
		try {
			result = new FattureDao().findByIstanza(ses, idIstanzaAbbonamento);
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
	public List<FattureArticoli> findFattureArticoliByIdFattura(
			Integer idFattura) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<FattureArticoli> result = null;
		try {
			result = new FattureArticoliDao().findByFattura(ses, idFattura);
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
	public Fatture createRimborsoTotale(Integer idFattura) throws BusinessException {
		Fatture result = null;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			result = FattureBusiness.createRimborso(ses, idFattura, true, false, false, false);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage());
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Fatture createStornoTotale(Integer idFattura) throws BusinessException {
		Fatture result = null;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			result = FattureBusiness.createRimborso(ses, idFattura, false, true, false, false);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage());
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Fatture createRimborsoResto(Integer idFattura) throws BusinessException {
		Fatture result = null;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			result = FattureBusiness.createRimborso(ses, idFattura, false, false, true, false);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage());
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Fatture createStornoResto(Integer idFattura) throws BusinessException {
		Fatture result = null;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			result = FattureBusiness.createRimborso(ses, idFattura, false, false, false, true);
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage());
		} finally {
			ses.close();
		}
		return result;
	}
	
}
