package it.giunti.apg.server.services;

import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.core.ServerUtil;
import it.giunti.apg.core.business.AnagraficheBusiness;
import it.giunti.apg.core.business.ContatoriBusiness;
import it.giunti.apg.core.business.MergeBusiness;
import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.OrdiniLogisticaDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.OrdiniLogistica;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AnagraficheServiceImpl extends RemoteServiceServlet implements AnagraficheService  {
	private static final long serialVersionUID = -4900729561676102348L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AnagraficheServiceImpl.class);
	
	
	public List<Anagrafiche> findByProperties(String codAnag, String ragSoc,
			String nome, String presso, String indirizzo,
			String cap, String loc, String prov,
			String email, String cfiva,
			Integer idPeriodico, String tipoAbb,
			Integer offset, Integer size) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		AnagraficheDao dao = new AnagraficheDao();
		List<Anagrafiche> listAna = null;
		try {
			listAna = dao.findByProperties(
					ses, codAnag, ragSoc, nome, presso, indirizzo,
					cap, loc, prov, email, cfiva,
					idPeriodico, tipoAbb, offset, size);
			for(Anagrafiche anag:listAna) {
				dao.fillAnagraficheWithLastInstances(ses, anag);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (listAna != null) {
			if (listAna.size() > 0) {
				return listAna;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	public List<Anagrafiche> quickSearchAnagrafiche(String searchString, Integer offset, Integer size) throws BusinessException {
		List<Anagrafiche> listAna = SearchBusiness.quickSearchAnagrafiche(searchString, offset, size);
		return listAna;
	}
	
	public List<Anagrafiche> simpleSearchByCognomeNome(String searchString, Integer size) throws BusinessException {
		Session ses = SessionFactory.getSession();
		AnagraficheDao dao = new AnagraficheDao();
		List<Anagrafiche> listAna = null;
		try {
			listAna = dao.simpleSearchByCognomeNome(ses, searchString, size);
			for(Anagrafiche anag:listAna) {
				dao.fillAnagraficheWithLastInstances(ses, anag);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return listAna;
	}
	
	public Anagrafiche findById(Integer id) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Anagrafiche result = null;
		try {
			result = GenericDao.findById(ses, Anagrafiche.class, id);
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
	public String findDescriptionById(Integer id) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		String result = "";
		try {
			Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, id);
			if (anag != null) {
				result += anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
				if (anag.getIndirizzoPrincipale().getNome() != null) {
					if (anag.getIndirizzoPrincipale().getNome().length() > 0) result += " "+anag.getIndirizzoPrincipale().getNome();
				}
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result.length() > 0) {
			return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Anagrafiche createAnagrafica()
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Anagrafiche result = null;
		try {
			result = new AnagraficheDao().createAnagrafiche(ses);
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
	
	public Boolean deleteAnagrafica(Integer idAnagrafica) throws BusinessException {
		//Salvataggio
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, idAnagrafica);
			//Controlli su abbonamenti esistenti
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			List<IstanzeAbbonamenti> iaList1 = iaDao.findIstanzeProprieByAnagrafica(ses, idAnagrafica, false, 0, Integer.MAX_VALUE);
			List<IstanzeAbbonamenti> iaList2 = iaDao.findIstanzeRegalateByAnagrafica(ses, idAnagrafica, false, 0, Integer.MAX_VALUE);
			List<IstanzeAbbonamenti> iaList3 = iaDao.findIstanzePromosseByAnagrafica(ses, idAnagrafica, false, 0, Integer.MAX_VALUE);
			//Controlli sui crediti
			List<Pagamenti> pagList = new PagamentiDao().findByAnagrafica(ses, anag.getId(), null, null);
			List<PagamentiCrediti> credList = new PagamentiCreditiDao().findByAnagrafica(ses, anag.getId(), null); 
			List<OrdiniLogistica> olList = new OrdiniLogisticaDao().findOrdiniByAnagrafica(ses, false, idAnagrafica, 0, Integer.MAX_VALUE);
			if ((iaList1.size() == 0) && (iaList2.size() == 0) &&
					(iaList3.size() == 0) && (pagList.size() == 0) && (credList.size() == 0)) {
				//Elimina ordini logistica
				for (OrdiniLogistica ol:olList) {
					GenericDao.deleteGeneric(ses, ol.getId(), ol);
				}
				//A questo punto non ci sono entità collegate
				GenericDao.deleteGeneric(ses, idAnagrafica, anag);
			} else {
				throw new BusinessException("Esistono ancora entita' collegate all'anagrafica: impossibile eliminare!");
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
	
	public Integer saveOrUpdate(Anagrafiche item) throws BusinessException, ValidationException {
		//Assegna codice cliente se necessario (è una transazione a parte!)
		if (item.getUid() == null) {
			String uid = ContatoriBusiness.generateUidCliente();
			item.setUid(uid);
		}
		//Save or update
		Integer id = null;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			id = AnagraficheBusiness.saveOrUpdate(ses, item, true);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return id;
	}

	@Override
	public Localita findCapByCapString(String capString) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Localita result = null;
		try {
			result = new LocalitaDao().findCapByCapNumber(ses, capString);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			result.setNome(ValueUtil.capitalizeFirstLetter(result.getNome()));
		}
		return result;
	}

	@Override
	public Localita findCapByLocalitaProv(String localita, String optionalProv) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Localita result = null;
		try {
			result = new LocalitaDao().findCapByLocalitaProv(ses, localita, optionalProv);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (EmptyResultException e) {
			throw new EmptyResultException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			result.setNome(ValueUtil.capitalizeFirstLetter(result.getNome()));
		}
		return result;
	}

	@Override
	public Localita findCapByLocalitaCapString(String localita, String cap) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Localita result = null;
		try {
			result = new LocalitaDao().findCapByLocalitaCapString(ses, localita, cap);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			result.setNome(ValueUtil.capitalizeFirstLetter(result.getNome()));
		}
		return result;
	}

	@Override
	public List<Anagrafiche> findAnagraficheByLastModified(int offset,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		AnagraficheDao dao = new AnagraficheDao();
		List<Anagrafiche> listAna = null;
		try {
			listAna = dao.findAnagraficheByLastModified(ses, offset, pageSize);
			for(Anagrafiche anag:listAna) {
				dao.fillAnagraficheWithLastInstances(ses, anag);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return listAna;
	}

	@Override
	public List<Localita> findLocalitaCapSuggestions(String localitaPrefix, String provinciaPrefix, String capPrefix) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Localita> result = null;
		try {
			result = new LocalitaDao().findLocalitaCapSuggestions(ses, localitaPrefix, provinciaPrefix, capPrefix);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public List<Localita> findLocalitaSuggestions(String localitaPrefix) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Localita> result = null;
		try {
			result = new LocalitaDao().findLocalitaSuggestions(ses, localitaPrefix);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Boolean verifyLocalitaItalia(String localitaName, String localitaProv, String localitaCap) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Boolean result = null;
		try {
			result = new LocalitaDao().verifyLocalitaItalia(ses, localitaName, localitaProv, localitaCap);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public List<Anagrafiche> findAnagraficheToVerify(int offset,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		AnagraficheDao dao = new AnagraficheDao();
		List<Anagrafiche> listAna = null;
		try {
			listAna = dao.findAnagraficheToVerify(ses, offset, pageSize);
			for(Anagrafiche anag:listAna) {
				dao.fillAnagraficheWithLastInstances(ses, anag);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return listAna;
	}
	
	@Override
	public List<Anagrafiche> findMergeArray(Integer idAnagrafica) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Anagrafiche> result = new ArrayList<Anagrafiche>();
		try {
			Anagrafiche anag1 = null;
			Anagrafiche anag2 = null;
			Anagrafiche anag3;
			Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, idAnagrafica);
			if (anag == null) throw new EmptyResultException("No Anagrafiche with id="+idAnagrafica);
			if (anag.getIdAnagraficaDaAggiornare() != null) {
				//idAnagrafica is referring the new anagrafica to be verified
				anag2 = anag;
				anag1 = GenericDao.findById(ses, Anagrafiche.class, anag2.getIdAnagraficaDaAggiornare());
				if (anag1 == null) throw new EmptyResultException("No Anagrafiche with id="+anag2.getIdAnagraficaDaAggiornare());
				anag3 = MergeBusiness.mergeTransient(anag1, anag2);
			} else {
				//idAnagrafica is referring the old anagrafica
				anag1 = anag;
				anag2 = new AnagraficheDao().findAnagraficheByMergeReferral(ses, anag1.getId());
				if (anag2 != null) {
					anag3 = MergeBusiness.mergeTransient(anag1, anag2);
				} else {
					//throw new EmptyResultException("Anagrafiche with id="+anag1.getId()+" is not referred");
					//idAnagrafica is referring a new single anagrafica => anag2=anag3=null
					anag3 = null;
				}
			}
			//Result
			result.add(0, anag1);
			result.add(1, anag2);
			result.add(2, anag3);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public List<Anagrafiche> findMergeArray(Integer idAnagrafica1, Integer idAnagrafica2)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Anagrafiche> result = new ArrayList<Anagrafiche>();
		try {
			Anagrafiche anag1 = GenericDao.findById(ses, Anagrafiche.class, idAnagrafica1);
			if (anag1 == null) throw new EmptyResultException("No Anagrafiche with id="+idAnagrafica1);
			Anagrafiche anag2 = GenericDao.findById(ses, Anagrafiche.class, idAnagrafica2);
			if (anag2 == null) throw new EmptyResultException("No Anagrafiche with id="+idAnagrafica2);
			//Merge attempt
			Anagrafiche anag3 = MergeBusiness.mergeTransient(anag1, anag2);
			//Result
			result.add(0, anag1);
			result.add(1, anag2);
			result.add(2, anag3);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Anagrafiche merge(Anagrafiche anag1, Anagrafiche anag2, Anagrafiche anag3) 
			throws BusinessException, EmptyResultException {
		Anagrafiche result = null;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Integer id1 = anag1.getId();
			Integer id2 = anag2.getId();
			Integer idIndP = anag1.getIndirizzoPrincipale().getId();
			Integer idIndF = anag1.getIndirizzoFatturazione().getId();
			String uid = anag1.getUid();
			try {
				PropertyUtils.copyProperties(anag1, anag3);// copy 3 over 1
				PropertyUtils.copyProperties(anag1.getIndirizzoPrincipale(), anag3.getIndirizzoPrincipale());
				PropertyUtils.copyProperties(anag1.getIndirizzoFatturazione(), anag3.getIndirizzoFatturazione());
				} catch (Exception e) {e.printStackTrace();}
			anag1.setId(id1);
			anag1.getIndirizzoPrincipale().setId(idIndP);
			anag1.getIndirizzoFatturazione().setId(idIndF);
			anag1.setUid(uid);
			anag1.setIdAnagraficaDaAggiornare(null);
			anag1.setNecessitaVerifica(false);
			ServerUtil.pojoToUppercase(anag1.getIndirizzoPrincipale());
			new IndirizziDao().update(ses, anag1.getIndirizzoPrincipale());
			ServerUtil.pojoToUppercase(anag1.getIndirizzoFatturazione());
			new IndirizziDao().update(ses, anag1.getIndirizzoFatturazione());
			ServerUtil.pojoToUppercase(anag1);
			anag1.setSearchString(SearchBusiness.buildAnagraficheSearchString(anag1));
			new AnagraficheDao().update(ses, anag1);
			result = anag1;
			//Merge abbonamenti, fatture, spedizioni
			MergeBusiness.moveAbbonamenti(ses, id1, id2);
			MergeBusiness.moveEvasioniFisiche(ses, id1, id2);
			MergeBusiness.moveFatture(ses, id1, id2);
			MergeBusiness.movePagamenti(ses, id1, id2);
			MergeBusiness.moveCrediti(ses, id1, id2);
			MergeBusiness.moveOrdiniLogistica(ses, id1, id2);
			ses.flush();
			//Rimuove anag2 !
			Indirizzi indP = anag2.getIndirizzoPrincipale();
			Indirizzi indF = anag2.getIndirizzoFatturazione();
			new AnagraficheDao().delete(ses, anag2);
			//Elimina i vecchi indirizzi
			IndirizziDao indDao = new IndirizziDao();
			indDao.delete(ses, indP);
			indDao.delete(ses, indF);
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
	public Integer countAnagraficaLikeRagSoc(String ragSoc) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer count = null;
		try {
			count = new AnagraficheDao().countAnagraficheLikeRagSoc(ses, ragSoc);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return count;
	}

}
