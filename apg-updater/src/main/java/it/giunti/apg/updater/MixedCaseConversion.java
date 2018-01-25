package it.giunti.apg.updater;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.NazioniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Nazioni;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MixedCaseConversion {

	private static int PAGE_SIZE = 1000;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static LocalitaDao locDao = new LocalitaDao();
	private static NazioniDao nazDao = new NazioniDao();
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String DATE_2018_STRING = "15/01/2018";
	private static Date DATE_2018 = null; 
	
	@SuppressWarnings("unchecked")
	public static void updateAnagraficheCase() 
			throws BusinessException, IOException {
		try {
			DATE_2018 = ServerConstants.FORMAT_DAY.parse(DATE_2018_STRING);
		} catch (ParseException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		List<Localita> lList = new ArrayList<Localita>();
		List<Nazioni> nList = new ArrayList<Nazioni>();
		Integer offset = 0;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			System.out.println("Totale anagrafiche: "+totalAnag);
			Date dtStart = new Date();
			//Update Anagrafiche
			hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					changeCaseAnagrafica(ses, a);
					changeDataCreazione(ses,a);
					anagDao.updateUnlogged(ses, a);
				}
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				System.out.println("Aggiornate "+offset+" anagrafiche ("+df.format(perc)+"%) "+
						"fine stimata "+stimaFine(dtStart, offset, totalAnag));
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			//Update Localita
			offset = 0;
			hql = "from Localita l order by l.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				lList = (List<Localita>) q.list();
				for (Localita l:lList) {
					updateCaseLocalita(ses, l);
				}
				offset += lList.size();
				System.out.println("Aggiornate "+offset+" località");
				ses.flush();
				ses.clear();
			} while (lList.size() == PAGE_SIZE);
			//Update Nazioni
			offset = 0;
			hql = "from Nazioni n order by n.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				nList = (List<Nazioni>) q.list();
				for (Nazioni n:nList) {
					updateCaseNazione(ses, n);
				}
				offset += nList.size();
				System.out.println("Aggiornate "+offset+" nazioni");
				ses.flush();
				ses.clear();
			} while (nList.size() == PAGE_SIZE);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static void changeCaseAnagrafica(Session ses, Anagrafiche a) {
		if (a.getEmailPrimaria() != null)
			a.setEmailPrimaria(a.getEmailPrimaria().toLowerCase());
		if (a.getEmailSecondaria() != null)
			a.setEmailSecondaria(a.getEmailSecondaria().toLowerCase());
		if (a.getNote() != null)
			a.setNote(a.getNote().toLowerCase());
		if (a.getIndirizzoPrincipale() != null)
			updateCaseIndirizzo(a.getIndirizzoPrincipale());
		if (a.getIndirizzoFatturazione() != null)
			updateCaseIndirizzo(a.getIndirizzoFatturazione());
		a.setSearchString(SearchBusiness.buildAnagraficheSearchString(a));
		//System.out.println(a.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+
		//		a.getIndirizzoPrincipale().getNome()+" "+
		//		a.getIndirizzoPrincipale().getPresso()+" "+
		//		a.getIndirizzoPrincipale().getIndirizzo()+" "+
		//		a.getIndirizzoPrincipale().getLocalita()+" "+
		//		a.getIndirizzoPrincipale().getTitolo());//TODO
	}
	
	@SuppressWarnings("unchecked")
	private static void changeDataCreazione(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_abbonato = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				changeDataCreazionePagante(ses, a);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void changeDataCreazionePagante(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_pagante = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				changeDataCreazionePromotore(ses, a);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void changeDataCreazionePromotore(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_promotore = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				a.setDataCreazione(a.getDataModifica());
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(a.getDataModifica());
			}
		}
	}
	
	private static void updateCaseIndirizzo(Indirizzi ind) {
		ind.setCognomeRagioneSociale(CharsetUtil.toMixedCase(ind.getCognomeRagioneSociale()));
		ind.setIndirizzo(CharsetUtil.toMixedCase(ind.getIndirizzo()));
		ind.setLocalita(CharsetUtil.toMixedCase(ind.getLocalita()));
		ind.setNome(CharsetUtil.toMixedCase(ind.getNome()));
		ind.setPresso(CharsetUtil.toMixedCase(ind.getPresso()));
		ind.setTitolo(CharsetUtil.toMixedCase(ind.getTitolo()));
		if (ind.getProvincia() != null)
			ind.setProvincia(ind.getProvincia().toUpperCase());
	}
	
	private static void updateCaseLocalita(Session ses, Localita loc) {
		loc.setNome(CharsetUtil.toMixedCase(loc.getNome()));
		locDao.update(ses, loc);
	}
	
	private static void updateCaseNazione(Session ses, Nazioni naz) {
		naz.setNomeNazione(CharsetUtil.toMixedCase(naz.getNomeNazione()));
		nazDao.update(ses, naz);
	}
	
	
	private static String stimaFine(Date dtInizio, Integer offset, Long total) {
		Date now = new Date();
		Long elapsed = now.getTime()-dtInizio.getTime();
		Double forecastDouble = elapsed.doubleValue()*total.doubleValue()/offset.doubleValue();
		Long forecastTime = forecastDouble.longValue() + dtInizio.getTime();
		Date forecastDt = new Date(forecastTime);
		return sdf.format(forecastDt);
	}
	
}
