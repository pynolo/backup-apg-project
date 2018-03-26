package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.CacheAnagraficheDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheAnagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(CacheBusiness.class);
	private static CacheAnagraficheDao caDao = new CacheAnagraficheDao();
	
	public static void saveOrUpdateCache(Session ses, Anagrafiche a) 
			throws BusinessException {
		CacheCreator cc = new CacheCreator(ses, a);
		cc.run();
	}
	
	public static void saveOrUpdateCacheThreadless(Session ses, Anagrafiche a) 
			throws BusinessException {
		CacheCreator cc = new CacheCreator(ses, a);
		cc.threadlessRun();
	}
	
	public static void removeCache(Session ses, Integer idAnagrafica) 
			throws BusinessException {
		Anagrafiche anaTemp = new Anagrafiche();
		anaTemp.setId(idAnagrafica);
		anaTemp.setDataModifica(new Date());
		saveOrUpdateCache(ses, anaTemp);
	}
	
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findIstanze(Session ses, Integer idAnagrafica) 
			throws HibernateException {
		String hql = "from IstanzeAbbonamenti ia where "
				+ "(ia.abbonato.id = :id1 or ia.pagante.id = :id2) and "
				+ "(ia.fascicoloFine.dataFine > :dt1 or ia.ultimaDellaSerie = :b1) "
				+ "order by ia.id";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("id2", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("dt1", new Date(), DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> iaList = q.list();
		return iaList;
	}
	
	private static void copyIntoCache(CacheAnagrafiche ca, int idx, CrmData data) 
			throws BusinessException {
		try {
			//ownSubscriptionIdentifier
			Method ownSubscriptionIdentifierSetter = 
					ca.getClass().getMethod("setOwnSubscriptionIdentifier"+idx, String.class);
			ownSubscriptionIdentifierSetter.invoke(ca, data.getOwnSubscriptionIdentifier());
			//subscriptionCreationDate
			Method subscriptionCreationDateSetter = 
					ca.getClass().getMethod("setSubscriptionCreationDate"+idx, Date.class);
			subscriptionCreationDateSetter.invoke(ca, data.getSubscriptionCreationDate());
			//ownSubscriptionBegin
			Method ownSubscriptionBeginSetter = 
					ca.getClass().getMethod("setOwnSubscriptionBegin"+idx, Date.class);
			ownSubscriptionBeginSetter.invoke(ca, data.getOwnSubscriptionBegin());
			//ownSubscriptionEnd
			Method ownSubscriptionEndSetter = 
					ca.getClass().getMethod("setOwnSubscriptionEnd"+idx, Date.class);
			ownSubscriptionEndSetter.invoke(ca, data.getOwnSubscriptionEnd());
			//ownSubscriptionBlocked
			Method ownSubscriptionBlockedSetter = 
					ca.getClass().getMethod("setOwnSubscriptionBlocked"+idx, Boolean.class);
			ownSubscriptionBlockedSetter.invoke(ca, data.getOwnSubscriptionBlocked());
			//giftSubscriptionEnd
			Method giftSubscriptionEndSetter = 
					ca.getClass().getMethod("setGiftSubscriptionEnd"+idx, Date.class);
			giftSubscriptionEndSetter.invoke(ca, data.getGiftSubscriptionEnd());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException |
				IllegalArgumentException | InvocationTargetException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	
	

	//Inner classes
	
	private static class CacheCreator implements Runnable {
		
		private Session ses;
		private Anagrafiche a;
		
		public CacheCreator(Session ses, Anagrafiche a) {
			this.ses = ses;
			this.a = a;
		}
		
		public void run() {
			try {
				saveOrUpdate();
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		public void threadlessRun() {
			try {
				saveOrUpdate();
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		private void saveOrUpdate() throws BusinessException {
			CacheAnagrafiche ca = caDao.findByAnagrafica(ses, a.getId());
			if (ca == null) ca = new CacheAnagrafiche();
			boolean isRecipient = false;
			boolean isGiftee = false;
			List<IstanzeAbbonamenti> iaList = findIstanze(ses, a.getId());
			Date lastModified = a.getDataModifica();
			for (int idx=0; idx<AppConstants.CACHE_PERIODICI_ORDER.length; idx++) {
				String uidPeriodico = AppConstants.CACHE_PERIODICI_ORDER[idx];
				if (uidPeriodico != null) {
					//linearizzazione periodico
					CrmData data = new CrmData();
					data.setOwnSubscriptionBlocked(false);
					for (IstanzeAbbonamenti ia:iaList) {
						if (ia.getFascicoloInizio().getPeriodico().getUid().equals(uidPeriodico)) {
							if (ia.getAbbonato().getId().equals(a.getId())) {
								//BENEFICIARIO
								isRecipient = true;
								//codice abbonamento
								data.setOwnSubscriptionIdentifier(ia.getAbbonamento().getCodiceAbbonamento());
								//creazione originale
								data.setSubscriptionCreationDate(ia.getAbbonamento().getDataCreazione());
								//data inizio proprio abbonamento
								if (data.getOwnSubscriptionBegin() == null) {
									data.setOwnSubscriptionBegin(ia.getFascicoloInizio().getDataInizio());
								} else {
									if (ia.getFascicoloInizio().getDataInizio().before(data.getOwnSubscriptionBegin())) {
										data.setOwnSubscriptionBegin(ia.getFascicoloInizio().getDataInizio());
									}
								}
								//data fine proprio abbonamento
								if (data.getOwnSubscriptionEnd() == null) {
									data.setOwnSubscriptionEnd(ia.getFascicoloFine().getDataFine());
								} else {
									if (ia.getFascicoloFine().getDataFine().after(data.getOwnSubscriptionEnd())) {
										data.setOwnSubscriptionEnd(ia.getFascicoloFine().getDataFine());
									}
								}
								//blocco proprio abbonamento
								if (ia.getInvioBloccato()) data.setOwnSubscriptionBlocked(true);
							} else {
								//PAGANTE
								isGiftee = true;
								//data fine regalo
								if (data.getGiftSubscriptionEnd() == null) {
									data.setGiftSubscriptionEnd(ia.getFascicoloFine().getDataFine());
								} else {
									if (ia.getFascicoloFine().getDataFine().after(data.getGiftSubscriptionEnd())) {
										data.setGiftSubscriptionEnd(ia.getFascicoloFine().getDataFine());
									}
								}
							}
							if (lastModified.before(ia.getDataModifica())) {
								lastModified = ia.getDataModifica();
							}
						}
					}
					//Copy into cache
					copyIntoCache(ca, idx, data);
				}
			}
			
			//Proprietà con valori generali aggregati
			ca.setModifiedDate(lastModified);
			String customerType = "";
			if (isRecipient && isGiftee) {
				customerType = AppConstants.CACHE_CUSTOMER_TYPE_BOTH;
			} else {
				if (isRecipient) customerType = AppConstants.CACHE_CUSTOMER_TYPE_RECIPIENT;
				if (isGiftee) customerType = AppConstants.CACHE_CUSTOMER_TYPE_GIFTEE;
			}
			ca.setCustomerType(customerType);
			
			//Save or update
			if (ca.getIdAnagrafica() == null) {
				//save
				ca.setIdAnagrafica(a.getId());
				caDao.save(ses, ca);
			} else {
				//update
				caDao.update(ses, ca);
			}
		}
	
	}
	
	
	private static class CrmData {
		private String ownSubscriptionIdentifier;
		private boolean ownSubscriptionBlocked;
		private Date ownSubscriptionBegin;
		private Date ownSubscriptionEnd;
		private Date giftSubscriptionEnd;
		private Date subscriptionCreationDate;
		
		public String getOwnSubscriptionIdentifier() {
			return ownSubscriptionIdentifier;
		}
		public void setOwnSubscriptionIdentifier(String ownSubscriptionIdentifier) {
			this.ownSubscriptionIdentifier = ownSubscriptionIdentifier;
		}
		public boolean getOwnSubscriptionBlocked() {
			return ownSubscriptionBlocked;
		}
		public void setOwnSubscriptionBlocked(boolean ownSubscriptionBlocked) {
			this.ownSubscriptionBlocked = ownSubscriptionBlocked;
		}
		public Date getOwnSubscriptionBegin() {
			return ownSubscriptionBegin;
		}
		public void setOwnSubscriptionBegin(Date ownSubscriptionBegin) {
			this.ownSubscriptionBegin = ownSubscriptionBegin;
		}
		public Date getOwnSubscriptionEnd() {
			return ownSubscriptionEnd;
		}
		public void setOwnSubscriptionEnd(Date ownSubscriptionEnd) {
			this.ownSubscriptionEnd = ownSubscriptionEnd;
		}
		public Date getGiftSubscriptionEnd() {
			return giftSubscriptionEnd;
		}
		public void setGiftSubscriptionEnd(Date giftSubscriptionEnd) {
			this.giftSubscriptionEnd = giftSubscriptionEnd;
		}
		public Date getSubscriptionCreationDate() {
			return subscriptionCreationDate;
		}
		public void setSubscriptionCreationDate(Date subscriptionCreationDate) {
			this.subscriptionCreationDate = subscriptionCreationDate;
		}
	}
}
