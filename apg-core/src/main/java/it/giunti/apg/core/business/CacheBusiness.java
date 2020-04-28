package it.giunti.apg.core.business;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheCrm;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;


public class CacheBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(CacheBusiness.class);
	private static CacheCrmDao caDao = new CacheCrmDao();
	
	public static void saveOrUpdateCache(Session ses, Anagrafiche a, boolean markAsModifiedToday) 
			throws BusinessException {
		CacheCreator cc = new CacheCreator(ses, a, markAsModifiedToday);
		cc.run();
	}
	
	public static void saveOrUpdateCacheThreadless(Session ses, Anagrafiche a, boolean markAsModifiedToday) 
			throws BusinessException {
		CacheCreator cc = new CacheCreator(ses, a, markAsModifiedToday);
		cc.threadlessRun();
	}
	
	public static void removeCache(Session ses, Integer idAnagrafica, boolean markAsModifiedToday) 
			throws BusinessException {
		//Sostituisce una anagrafica vuota a quella da rimuovere
		Anagrafiche anaTemp = new Anagrafiche();
		anaTemp.setId(idAnagrafica);
		anaTemp.setDataModifica(new Date());
		saveOrUpdateCache(ses, anaTemp, markAsModifiedToday);
	}
	
	/** Return an array with a single OWN instance and a single GIFT instance for EACH MAGAZINE **/
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findAndFilterIstanze(Session ses, Integer idAnagrafica) 
			throws HibernateException {
		List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
		//Own istance
		String hql = "from IstanzeAbbonamenti ia where "
				+ "ia.abbonato.id = :id1 and "
				+ "(ia.fascicoloFine.dataFine > :dt1 or ia.ultimaDellaSerie = :b1) "
				+ "order by ia.id";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("dt1", new Date(), DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> ownList = q.list();
		ownList = filterIa(ownList);
		result.addAll(ownList);
		//Gift istance
		hql = "from IstanzeAbbonamenti ia where "
				+ "ia.pagante.id = :id2 and "
				+ "(ia.fascicoloFine.dataFine > :dt1 or ia.ultimaDellaSerie = :b1) "
				+ "order by ia.id";
		q = ses.createQuery(hql);
		q.setParameter("id2", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("dt1", new Date(), DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> giftList = q.list();
		giftList = filterIa(giftList);
		result.addAll(giftList);
		return result;
	}
	
	private static List<IstanzeAbbonamenti> filterIa(List<IstanzeAbbonamenti> iaList) {
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
	
	/** 
	 * run() and threadlessRun() methods trigger the creation or update of the CacheCrm row
	 * based on the data of an Anagrafiche object
	 * @author USER
	 *
	 */
	private static class CacheCreator implements Runnable {
		
		private Session ses;
		private Anagrafiche a;
		private boolean markAsModifiedToday;
		
		public CacheCreator(Session ses, Anagrafiche a, boolean markAsModifiedToday) {
			this.ses = ses;
			this.a = a;
			this.markAsModifiedToday = markAsModifiedToday;
		}
		
		public void run() {
			try {
				saveOrUpdate(markAsModifiedToday);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		public void threadlessRun() {
			try {
				saveOrUpdate(markAsModifiedToday);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		private void saveOrUpdate(boolean markAsModifiedToday) throws BusinessException {
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
			List<IstanzeAbbonamenti> iaList = findAndFilterIstanze(ses, a.getId());
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
			// TODO
			////Proprietà con valori generali aggregati
			//String customerType = "";
			//if (isPayer && isGiftee) {
			//	customerType = AppConstants.CACHE_CUSTOMER_TYPE_BOTH;
			//} else {
			//	if (isPayer) customerType = AppConstants.CACHE_CUSTOMER_TYPE_PAYER;
			//	if (isGiftee) customerType = AppConstants.CACHE_CUSTOMER_TYPE_GIFTEE;
			//}
			//cc.setCustomerType(customerType);

			boolean equalBeans = compareCacheIgnoringBegin(originalCc, cc);
			
			if (!equalBeans) {
				//LOG.debug("saved id="+a.getId()+" uid="+a.getUid());
				//Choose modified date
				if (markAsModifiedToday) {
					cc.setModifiedDate(DateUtil.now());
				} else {
					cc.setModifiedDate(lastModified);
				}
				//Save or update
				if (cc.getIdAnagrafica() == null) {
					//save
					cc.setIdAnagrafica(a.getId());
					caDao.save(ses, cc);
				} else {
					//update
					caDao.update(ses, cc);
				}
			}
		}
	
	}
	
	public static boolean compareCacheIgnoringBegin(CacheCrm bean1, CacheCrm bean2) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(CacheCrm.class);
		} catch (IntrospectionException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}

		// We loop over all the properties, get the read method and invoke it on
		// both beans. Both values are compared by recursively calling the current
		// compareBeans function :
		for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			Method getter = prop.getReadMethod();
			if (getter != null) {
				if (!getter.getName().contains("Begin")) {//Ignore "begin" values
					Object value1 = null;
					Object value2 = null;
					try {
						value1 = getter.invoke(bean1);
						value2 = getter.invoke(bean2);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						/*LOG.error(e.getMessage(), e);
						return false;*/
					}
					// compare the values as beans
					if (!BeanUtil.compareBeans(value1, value2)) {
						//LOG.debug(getter.getName()+": "+value1+" != "+value2);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Alcuni casi da prevedere:
	 * - distinguere abbonamento da pagare (serve data inizio)
	 * - interrompere comunicazione subito, se blocco
	 * - interrompere comunicazione a fine abb, se disdetta
	 * - proprio abbonamento, distinguere pagati e chi riceve un regalo
	 */
	private static class CrmData {//TODO
		private String ownSubscriptionIdentifier; //Identificativo proprio abbonamento (COD ABBO)
		private String ownSubscriptionMedia; // "d" - solo digitale
												// "p" - solo cartaceo
												// "dp" - digitale e cartaceo 
		private String ownSubscriptionStatus; // "regolare" - proprio abbonamento in regola (fatturati, pagati...)
												// "moroso" - proprio abbonamento da pagare
												// "beneficiario" - l'abbonamento è regalato
												// "omaggio" - omaggio
		private Date ownSubscriptionCreationDt; //Data storica creazione proprio abbonamento
		private Date ownSubscriptionBeginDt; // * Data inizio proprio abbonamento
		private Date ownSubscriptionEndDt; //Data fine proprio abbonamento
		private Date ownSubscriptionCancellationDt; // * Data disdetta proprio abbonamento
		private boolean ownSubscriptionBlocked; // * Se il proprio abbonamento è bloccato
		//* rimuovere se estrazione massiva
		
		//private boolean giftSubscriptionMedia; // da rimuovere: "d" - solo digitale, "p" - solo cartaceo, "dp" - digitale e cartaceo 
		private Date giftSubscriptionEnd; //Data fine abbonamento regalato
		
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
