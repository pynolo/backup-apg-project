package it.giunti.apg.core.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.CacheCrmDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheCrm;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;


public class CacheBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(CacheBusiness.class);
	private static CacheCrmDao caDao = new CacheCrmDao();
	
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
		//FILTRO: per stesso periodico passa id maggiore se non bloccato
		Map<String, IstanzeAbbonamenti> perMap = new HashMap<String, IstanzeAbbonamenti>();
		for (IstanzeAbbonamenti ia:iaList) {
			String lettera = ia.getAbbonamento().getPeriodico().getUid();
			IstanzeAbbonamenti iaMap = perMap.get(lettera);
			if (iaMap == null) {
				perMap.put(lettera, ia);
			} else {
				if (iaMap.getInvioBloccato() && !ia.getInvioBloccato()) {
					//nella mappa bloccato, il nuovo no
					perMap.put(lettera, ia);
				} else {
					if (iaMap.getInvioBloccato() == ia.getInvioBloccato()) {
						//entrambi non bloccati o entrambi bloccati
						if (iaMap.getId() < ia.getId()) {
							//nella mappa ha id inferiore
							perMap.put(lettera, ia);
						}
					}
				}
			}
		}
		//Dopo il ciclo ho un solo abbonamento per periodico
		List<IstanzeAbbonamenti> mapList = new ArrayList<IstanzeAbbonamenti>(perMap.values());
		return mapList;
	}
	
	private static void copyIntoCache(CacheCrm cc, int idx, CrmData data) 
			throws BusinessException {
		try {
			//ownSubscriptionIdentifier
			Method ownSubscriptionIdentifierSetter = 
					cc.getClass().getMethod("setOwnSubscriptionIdentifier"+idx, String.class);
			ownSubscriptionIdentifierSetter.invoke(cc, data.getOwnSubscriptionIdentifier());
			//subscriptionCreationDate
			Method subscriptionCreationDateSetter = 
					cc.getClass().getMethod("setSubscriptionCreationDate"+idx, Date.class);
			subscriptionCreationDateSetter.invoke(cc, data.getSubscriptionCreationDate());
			//ownSubscriptionBegin
			Method ownSubscriptionBeginSetter = 
					cc.getClass().getMethod("setOwnSubscriptionBegin"+idx, Date.class);
			ownSubscriptionBeginSetter.invoke(cc, data.getOwnSubscriptionBegin());
			//ownSubscriptionEnd
			Method ownSubscriptionEndSetter = 
					cc.getClass().getMethod("setOwnSubscriptionEnd"+idx, Date.class);
			ownSubscriptionEndSetter.invoke(cc, data.getOwnSubscriptionEnd());
			//ownSubscriptionBlocked
			Method ownSubscriptionBlockedSetter = 
					cc.getClass().getMethod("setOwnSubscriptionBlocked"+idx, Boolean.class);
			ownSubscriptionBlockedSetter.invoke(cc, data.getOwnSubscriptionBlocked());
			//giftSubscriptionEnd
			Method giftSubscriptionEndSetter = 
					cc.getClass().getMethod("setGiftSubscriptionEnd"+idx, Date.class);
			giftSubscriptionEndSetter.invoke(cc, data.getGiftSubscriptionEnd());
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
			if (a == null) throw new BusinessException("Anagrafiche is null");
			CacheCrm originalCc = caDao.findByAnagrafica(ses, a.getId());
			if (originalCc == null) originalCc = new CacheCrm();
			CacheCrm cc = new CacheCrm();
			try {
				PropertyUtils.copyProperties(cc, originalCc);
			} catch (Exception e) {
				throw new BusinessException(e.getMessage(), e);
			}
			boolean isPayer = false;
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
								//PAGANTE
								if (ia.getPagante() == null) {
									isPayer = true;//Customer is paying & receiving
								} else {
									isGiftee = true;//Customer is receiving a gift
								}
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
								data.setOwnSubscriptionBlocked(ia.getInvioBloccato());
							} else {
								//PAGANTE
								isPayer = true;//Subscription is a gift paid by customer
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
					copyIntoCache(cc, idx, data);
				}
			}
			
			//Proprietà con valori generali aggregati
			String customerType = "";
			if (isPayer && isGiftee) {
				customerType = AppConstants.CACHE_CUSTOMER_TYPE_BOTH;
			} else {
				if (isPayer) customerType = AppConstants.CACHE_CUSTOMER_TYPE_PAYER;
				if (isGiftee) customerType = AppConstants.CACHE_CUSTOMER_TYPE_GIFTEE;
			}
			cc.setCustomerType(customerType);
			
			boolean equalBeans = BeanUtil.compareBeans(originalCc, cc);
			
			if (!equalBeans) {
				LOG.debug("saved id="+a.getId()+" uid="+a.getUid());
				//Save or update
				if (cc.getIdAnagrafica() == null) {
					//save
					cc.setModifiedDate(lastModified);
					cc.setIdAnagrafica(a.getId());
					caDao.save(ses, cc);
				} else {
					//update
					cc.setModifiedDate(lastModified);
					caDao.update(ses, cc);
				}
			} else {
				LOG.debug("ok id="+a.getId()+" uid="+a.getUid());
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
