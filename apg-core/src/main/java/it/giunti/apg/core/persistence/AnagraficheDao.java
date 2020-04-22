package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

import it.giunti.apg.core.business.CacheBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Nazioni;

public class AnagraficheDao implements BaseDao<Anagrafiche> {

	//private static final String QUICK_SEARCH_FIELDS[] = {"cognomeRagioneSociale",
	//	"nome", "indirizzoPrincipale.presso", "indirizzoPrincipale.indirizzo",
	//	"indirizzoPrincipale.cap", "indirizzoPrincipale.localita",
	//	"codiceCliente"};//rimossi "indirizzoPrincipale.provincia", "email",
	
	@Override
	public void update(Session ses, Anagrafiche instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, instance, true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		//Editing log
		LogEditingDao.writeEditingLog(ses, Anagrafiche.class, instance.getId(), instance.getUid(),
				instance.getIdUtente());
	}
	
	public void updateUnlogged(Session ses, Anagrafiche instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, instance, true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
	}
	
	@Override
	public Serializable save(Session ses, Anagrafiche transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, transientInstance, true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		//Editing log
		LogEditingDao.writeEditingLog(ses, Anagrafiche.class, id, transientInstance.getUid(),
				transientInstance.getIdUtente());
		return id;
	}

//	@Override
//	public void delete(Session ses, Anagrafiche instance) throws HibernateException {
//		try {
//			Integer idAnagrafiche = instance.getId();
//			GenericDao.deleteGeneric(ses, instance.getId(), instance);
//			//Aggiorna cache
//			try {
//				CacheBusiness.removeCache(ses, idAnagrafiche, true);
//			} catch (BusinessException e) {
//				throw new HibernateException(e.getMessage(), e);
//			}
//			LogDeletionDao.writeDeletionLog(ses, Anagrafiche.class, instance.getId(),
//					instance.getUid(), instance.getIdUtente());
//		} catch (HibernateException e) {
//			throw new HibernateException("Anagrafica " + instance.getUid() +
//					": "+e.getMessage(), e);
//		}
//	}
	
	@Override
	public void delete(Session ses, Anagrafiche instance) throws HibernateException {
		try {
			instance.setDeleted(true);//logical delete
			//instance.setUid(uid); mantenuto
			//instance.setUidMergeList(uidMergeList); mantenuto
			//instance.setMergedIntoUid(mergedIntoUid); mantenuto
			//instance.setIdUtente(idUtente); mantenuto
			
			instance.setCodiceDestinatario(null);
			instance.setCodiceFiscale(null);
			instance.setCodiceSap(null);
			instance.setConsensoMarketing(false);
			instance.setConsensoProfilazione(false);
			instance.setConsensoTos(false);
			instance.setCuf(null);
			instance.setDataAggiornamentoConsenso(null);
			instance.setDataCreazione(null);
			instance.setDataModifica(DateUtil.now());//mantenuto
			instance.setDataNascita(null);
			instance.setEmailPec(null);
			instance.setEmailPrimaria(null);
			instance.setGiuntiCardClub(null);
			instance.setIdAnagraficaDaAggiornare(null);
			instance.setIdTipoAnagrafica(null);
			instance.setNecessitaVerifica(false);
			instance.setNote(null);
			instance.setPa(false);
			instance.setPartitaIva(null);
			instance.setProfessione(null);
			instance.setSearchString(null);
			instance.setSesso(null);
			instance.setTelCasa(null);
			instance.setTelMobile(null);
			instance.setTitoloStudio(null);
			Indirizzi ip = instance.getIndirizzoPrincipale();
			//ip.setIdUtente(idUtente); mantenuto
			ip.setCap(null);
			ip.setCognomeRagioneSociale(null);
			ip.setDataModifica(DateUtil.now());// mantenuto
			ip.setIndirizzo(null);
			ip.setLocalita(null);
			ip.setNazione(null);
			ip.setNome(null);
			ip.setPresso(null);
			ip.setProvincia(null);
			ip.setTitolo(null);
			Indirizzi ib = instance.getIndirizzoFatturazione();
			//ib.setIdUtente(idUtente); mantenuto
			ib.setCap(null);
			ib.setCognomeRagioneSociale(null);
			ib.setDataModifica(DateUtil.now());// mantenuto
			ib.setIndirizzo(null);
			ib.setLocalita(null);
			ib.setNazione(null);
			ib.setNome(null);
			ib.setPresso(null);
			ib.setProvincia(null);
			ib.setTitolo(null);
			
			updateUnlogged(ses, instance);
			LogDeletionDao.writeDeletionLog(ses, Anagrafiche.class, instance.getId(),
					instance.getUid(), instance.getIdUtente());
		} catch (HibernateException e) {
			throw new HibernateException("Anagrafica " + instance.getUid() +
					": "+e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Anagrafiche> findByProperties(Session ses,
			String uidAnag, String ragSoc, String nome,
			String presso, String indirizzo,
			String cap, String loc, String prov,
			String email, String cfiva,
			Integer idPeriodico, String tipoAbb,
			Date dataValidita, String numeroFattura,
			Integer offset, Integer size) throws HibernateException {
		int conditions = 0;
		QueryFactory qf = new QueryFactory(ses, "from Anagrafiche a");
		if (uidAnag != null) {
			if (uidAnag.length() > 1) {
				uidAnag=uidAnag.replace('*', '%');
				if (!uidAnag.contains("%")) uidAnag += "%";
				qf.addWhere("a.uid like :p0");
				qf.addParam("p0", uidAnag);
				conditions++;
			}
		}
		if (ragSoc != null) {
			if (ragSoc.length() > 1) {
				ragSoc=ragSoc.replace('*', '%');
				if (!ragSoc.contains("%")) ragSoc += "%";
				qf.addWhere("a.indirizzoPrincipale.cognomeRagioneSociale like :p1");
				qf.addParam("p1", ragSoc);
				conditions++;
			}
		}
		if (nome != null) {
			if (nome.length() > 1) {
				nome=nome.replace('*', '%');
				qf.addWhere("(a.indirizzoPrincipale.nome like :p2_1) or (a.indirizzoPrincipale.cognomeRagioneSociale like :p2_2)");
				qf.addParam("p2_1", nome);
				qf.addParam("p2_2", "%"+nome);
				conditions++;
			}
		}
		if (presso != null) {
			if (presso.length() > 1) {
				presso=presso.replace('*', '%');
				qf.addWhere("a.indirizzoPrincipale.presso like :p3");
				qf.addParam("p3", presso);
				conditions++;
			}
		}
		if (indirizzo != null) {
			if (indirizzo.length() > 1) {
				indirizzo=indirizzo.replace('*', '%');
				qf.addWhere("a.indirizzoPrincipale.indirizzo like :p4");
				qf.addParam("p4", indirizzo);
				conditions++;
			}
		}
		if (cap != null) {
			if (cap.length() > 1) {
				cap=cap.replace('*', '%');
				qf.addWhere("a.indirizzoPrincipale.cap like :p5");
				qf.addParam("p5", cap);
				conditions++;
			}
		}
		if (loc != null) {
			if (loc.length() > 1) {
				loc=loc.replace('*', '%');
				qf.addWhere("a.indirizzoPrincipale.localita like :p6");
				qf.addParam("p6", loc);
				conditions++;
			}
		}
		if (prov != null) {
			if (prov.length() > 1) {
				prov=prov.replace('*', '%');
				qf.addWhere("a.indirizzoPrincipale.provincia like :p7");
				qf.addParam("p7", prov);
				conditions++;
			}
		}
		if (email != null) {
			if (email.length() > 1) {
				email=email.replace('*', '%');
				email="%"+email+"%";
				qf.addWhere("(a.emailPrimaria like :p8_1 or a.emailPec like :p8_2) ");
				qf.addParam("p8_1", email);
				qf.addParam("p8_2", email);
				conditions++;
			}
		}
		if (cfiva != null) {
			if (cfiva.length() > 1) {
				cfiva=cfiva.replace('*', '%');
				cfiva="%"+cfiva+"%";
				qf.addWhere("(a.codiceFiscale like :p8_5 or a.partitaIva like :p8_6) ");
				qf.addParam("p8_5", cfiva);
				qf.addParam("p8_6", cfiva);
				conditions++;
			}
		}
		if (idPeriodico != null) {
			if (idPeriodico != AppConstants.SELECT_EMPTY_VALUE) {
				qf.addWhere("a.id in (select ia.abbonato.id from IstanzeAbbonamenti ia where ia.abbonamento.periodico.id = :p9)");
				qf.addParam("p9", idPeriodico);
				conditions++;
			}
		}
		if (tipoAbb != null) {
			if (tipoAbb.length() > 0) {
				Integer idTipoAbb = null;
				try {
					idTipoAbb = Integer.parseInt(tipoAbb);
				} catch (NumberFormatException e) { }
				if (idTipoAbb != null) {
					qf.addWhere("a.id in (select ia.abbonato.id from IstanzeAbbonamenti ia where "+
							"ia.listino.tipoAbbonamento.id = :p10 and "+
							"ia.ultimaDellaSerie = :p11 "+
							")");
					qf.addParam("p10", idTipoAbb);
					qf.addParam("p11", Boolean.TRUE);
					conditions++;
				}
			}
		}
		if (dataValidita != null) {
			qf.addWhere("a.id in (select ia.abbonato.id from IstanzeAbbonamenti ia where "+
					"ia.fascicoloInizio.dataInizio <= :p12 and "+
					"ia.fascicoloFine.dataFine >= :p13 "+
					")");
			qf.addParam("p12", dataValidita);
			qf.addParam("p13", dataValidita);
			conditions++;
		}
		if (numeroFattura != null) {
			if (numeroFattura.length() > 1) {
				numeroFattura=numeroFattura.replace('*', '%');
				qf.addWhere("a.id in (select fat.idAnagrafica from Fatture fat where "+
						"fat.numeroFattura like :p14 "+
						")");
				qf.addParam("p14", numeroFattura);
				conditions++;
			}
		}
		
		if (conditions > 0) {
			qf.addWhere("a.deleted = :dlt ");
			qf.addParam("dlt", Boolean.FALSE);
			qf.addWhere("a.idAnagraficaDaAggiornare is null ");
			qf.addOrder("a.indirizzoPrincipale.cognomeRagioneSociale asc");
			qf.setPaging(offset, size);
			Query q = qf.getQuery();
			List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
			return anaList;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Anagrafiche findByUid(Session ses, String uid) 
			throws HibernateException {
		if (uid != null) uid = uid.toUpperCase();
		String qs = "from Anagrafiche anag where " +
				"anag.uid = :s1 and " +
				"anag.deleted = :dlt ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", uid);
		q.setParameter("dlt", Boolean.FALSE);
		List<Anagrafiche> anagList = q.list();
		Anagrafiche result = null;
		if (anagList != null) {
			if (anagList.size() > 0) {
				result = anagList.get(0);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Anagrafiche> findByMergedIntoUid(Session ses, String uid) 
			throws HibernateException {
		if (uid != null) uid = uid.toUpperCase();
		String qs = "from Anagrafiche anag where " +
				"anag.mergedIntoUid = :s1 and " +
				"anag.delete = :dlt ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", uid);
		q.setParameter("dlt", Boolean.FALSE);
		List<Anagrafiche> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Anagrafiche findByMergedUidCliente(Session ses, String uid) 
			throws HibernateException {
		if (uid != null) {
			if ((uid.length() > 5) && (uid.length() <= 10)) {
				String qs = "from Anagrafiche anag where " +
						"anag.uidMergeList like :s1";
				Query q = ses.createQuery(qs);
				q.setParameter("s1", "%"+uid+"%");
				List<Anagrafiche> anagList = q.list();
				Anagrafiche result = null;
				if (anagList != null) {
					if (anagList.size() > 0) {
						result = anagList.get(0);
					}
				}
				return result;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Anagrafiche findByIdAnagraficaDaAggiornare(Session ses, Integer id) 
			throws HibernateException {
		if (id != null) {
			String qs = "from Anagrafiche anag where " +
					"anag.idAnagraficaDaAggiornare = :id1 and " +
					"anag.delete = :dlt ";
			Query q = ses.createQuery(qs);
			q.setParameter("id1", id, IntegerType.INSTANCE);
			q.setParameter("dlt", Boolean.FALSE);
			List<Anagrafiche> anagList = q.list();
			Anagrafiche result = null;
			if (anagList != null) {
				if (anagList.size() > 0) {
					result = anagList.get(0);
				}
			}
			return result;
		}
		return null;
	}
	
	//@SuppressWarnings("unchecked")
	//public List<Anagrafiche> simpleSearchByCognomeNome(Session ses,
	//		String searchString, Integer size) throws HibernateException {
	//	String searchFields[] = {"indirizzoPrincipale.cognomeRagioneSociale",
	//		"indirizzoPrincipale.nome", "uid"};
	//	//Analisi searchString
	//	QueryFactory qf = new QueryFactory(ses, "from Anagrafiche a");
	//	List<String> sList = SearchBusiness.splitString(searchString);
	//	for (int i=0; i<sList.size(); i++) {
	//		if (sList.get(i).length() > 0){
	//			String orString = "";
	//			String param = sList.get(i).replace('*', '%');
	//			param = "%"+param+"%";
	//			for (int j=0; j<searchFields.length; j++) {
	//				orString += " (a."+searchFields[j] + " like :i"+i+"j"+j+" )";
	//				qf.addParam("i"+i+"j"+j, param);
	//				if (j != searchFields.length-1) {
	//					orString += " or";
	//				}
	//			}
	//			qf.addWhere(orString);
	//		}
	//	}
	//	qf.addWhere("a.idAnagraficaDaAggiornare is null ");
	//	qf.addOrder("a.indirizzoPrincipale.cognomeRagioneSociale asc");
	//	qf.setPaging(0, size);
	//	Query q = qf.getQuery();
	//	List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
	//	return anaList;
	//}
	
	public Anagrafiche createAnagrafiche(Session ses) throws HibernateException {
		Anagrafiche ana = new Anagrafiche();
		ana.setDeleted(false);
		ana.setConsensoTos(true);
		ana.setConsensoMarketing(false);
		ana.setConsensoProfilazione(false);
		ana.setDataAggiornamentoConsenso(DateUtil.now());
		Indirizzi indPri = new Indirizzi();
		Indirizzi indFat = new Indirizzi();
		Nazioni italia = (Nazioni)ses.get(Nazioni.class, "ITA");
		indPri.setProvincia(null);//AppConstants.DEFAULT_PROVINCIA_ITALIA);
		indPri.setNazione(italia);
		ana.setIndirizzoPrincipale(indPri);
		indFat.setProvincia(null);//AppConstants.DEFAULT_PROVINCIA_ITALIA);
		indFat.setNazione(italia);
		ana.setIndirizzoFatturazione(indFat);
		ana.setIdTipoAnagrafica(AppConstants.ANAG_PRIVATO);
		ana.setDataCreazione(DateUtil.now());
		return ana;
	}
	
	
	@SuppressWarnings("unchecked")
	public Integer countAnagraficheLikeRagSoc(Session ses, String ragSoc) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "select count(a.id) from Anagrafiche a");
		qf.addWhere("a.indirizzoPrincipale.cognomeRagioneSociale like :p1");
		qf.addParam("p1", ragSoc+"%");
		qf.addWhere("a.idAnagraficaDaAggiornare is null ");
		qf.addWhere("a.delete = :dlt ");
		qf.addParam("dlt", Boolean.FALSE);
		Query q = qf.getQuery();
		List<Object> list = (List<Object>) q.list();
		if (list != null) {
			if (list instanceof List) {
				if (list.size() > 0) {
					if (list.get(0) instanceof Integer) {
						Integer count = (Integer) list.get(0);
						return count;
					}
				}
			}
		}
		return null;
	}

	//@SuppressWarnings("unchecked")
	//public void fillAnagraficheWithLastInstances(Session ses, 
	//		Anagrafiche anag) throws HibernateException {
	//	QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
	//	qf.addWhere("ia.abbonato.id = :p1");
	//	qf.addParam("p1", anag.getId());
	//	qf.addOrder("ia.listino.tipoAbbonamento.periodico.id asc");
	//	Query q = qf.getQuery();
	//	List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
	//	Map<Integer, IstanzeAbbonamenti> periodiciInstanceMap = new HashMap<Integer, IstanzeAbbonamenti>();
	//	for (IstanzeAbbonamenti ia:abbList) {
	//		Integer idPer = ia.getListino().getTipoAbbonamento().getPeriodico().getId();
	//		IstanzeAbbonamenti last = periodiciInstanceMap.get(idPer);
	//		if (last == null) {
	//			periodiciInstanceMap.put(idPer, ia);
	//		} else {
	//			if (ValueUtil.fuzzyCompare(
	//					last.getFascicoloFine().getDataInizio(),
	//					ia.getFascicoloFine().getDataInizio()) <= 0) {
	//				periodiciInstanceMap.remove(idPer);
	//				periodiciInstanceMap.put(idPer, ia);
	//			}
	//		}
	//	}
	//	//Ottenuta la mappa la trasforma nella lista dei risultati
	//	ArrayList<IstanzeAbbonamenti> resultList = new ArrayList<IstanzeAbbonamenti>();
	//	for (IstanzeAbbonamenti ia:periodiciInstanceMap.values()) {
	//		resultList.add(SerializationUtil.makeSerializable(ia));
	//	}
	//	anag.setLastIstancesT(resultList);
	//}
	
	@SuppressWarnings("unchecked")
	public List<Anagrafiche> findOrderByLastModified(Session ses, Integer offset,
			Integer size) throws HibernateException {
		//Analisi searchString
		String qs = "from Anagrafiche a where "+
				"a.idAnagraficaDaAggiornare is null and "+
				"a.deleted = :dlt "+
				"order by a.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("dlt", Boolean.FALSE);
		q.setFirstResult(offset);
		q.setMaxResults(size);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList;
			}
		}
		return anaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Anagrafiche> findModifiedSinceDate(Session ses, Date startDate, Integer offset,
			Integer size) throws HibernateException {
		//Analisi searchString
		String qs = "from Anagrafiche a where "+
				"a.dataModifica >= :dt1 and "+
				"a.idAnagraficaDaAggiornare is null and "+
				"a.deleted = :dlt "+
				"order by a.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("dt1", startDate, DateType.INSTANCE);
		q.setParameter("dlt", Boolean.FALSE);
		q.setFirstResult(offset);
		q.setMaxResults(size);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList;
			}
		}
		return anaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Anagrafiche> findAnagraficheToVerify(Session ses, Integer offset,
			Integer size) throws HibernateException {
		//Analisi searchString
		String qs = "from Anagrafiche a where "+
				"a.necessitaVerifica = :b1 and "+
				"a.idAnagraficaDaAggiornare is null and "+
				"a.deleted = :dlt "+
				"order by a.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.TRUE);
		q.setParameter("dlt", Boolean.FALSE);
		q.setFirstResult(offset);
		q.setMaxResults(size);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList;
			}
		}
		return anaList;
	}
	
	@SuppressWarnings("unchecked")
	public Anagrafiche findByMergeReferral(Session ses, Integer idAnagrafiche)
			throws HibernateException {
		//Analisi searchString
		String qs = "from Anagrafiche a where "+
				"a.idAnagraficaDaAggiornare = :id1 and "+
				"a.deleted = :dlt "+
				"order by a.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnagrafiche);
		q.setParameter("dlt", Boolean.FALSE);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList.get(0);
			}
		}
		return null;
	}
}
