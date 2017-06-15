package it.giunti.apg.updater;

import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovePromotoreToPagante {
	
	private static final Logger LOG = LoggerFactory.getLogger(MovePromotoreToPagante.class);

	private static int PAGE_SIZE = 500;
	private static String[] tipiAbbonamento = {"DP", "09", "0R"};
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	public static void exec() 
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			for (String tipo:tipiAbbonamento) {
				LOG.info("");
				LOG.info("** TIPI "+tipo+" **");
				List<Abbonamenti> aList = findAbbonamentiOffertiPromotore(ses, tipo);
				for (Abbonamenti a:aList) {
					try {
						movePromotore(ses, a);
					} catch (BusinessException e) {
						LOG.error(e.getMessage());
					}
				}
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Abbonamenti> findAbbonamentiOffertiPromotore(Session ses, String tipoAbbonamento) 
			throws HibernateException {
		List<Abbonamenti> aList = new ArrayList<Abbonamenti>();
		String hql = "select ia.abbonamento from IstanzeAbbonamenti as ia where " +
				"ia.promotore != null and " +
				"ia.listino.tipoAbbonamento.codice = :s1 " +
				"group by ia.abbonamento " +
				"order by ia.abbonamento.codiceAbbonamento, ia.abbonamento.id ";
		List<Abbonamenti> list;
		do {
			Query q = ses.createQuery(hql);
			q.setFirstResult(aList.size());
			q.setMaxResults(PAGE_SIZE);
			q.setParameter("s1", tipoAbbonamento);
			list = (List<Abbonamenti>) q.list();
			aList.addAll(list);
		} while (list.size() == PAGE_SIZE);
		LOG.info("Trovati "+aList.size()+" abbonamenti "+tipoAbbonamento);
		return aList;
	}
	
	private static void movePromotore(Session ses, Abbonamenti a) 
			throws HibernateException, BusinessException {
		List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByAbbonamento(ses, a.getId());
		Anagrafiche promotore = null;
		Anagrafiche pagante = null;
		for (IstanzeAbbonamenti ia:iaList) {
			//Cerca un promotore
			if (ia.getPromotore() != null) {
				if ((promotore != null) && (!ia.getPromotore().equals(promotore))) {
					throw new BusinessException("> "+a.getCodiceAbbonamento()+
							" ignorato: nelle varie istanze ha promotori diversi");
				} else {
					promotore = ia.getPromotore();
				}
			}
			//Cerca un eventuale pagante
			if (ia.getPagante() != null) {
				if ((pagante != null) && (!ia.getPagante().equals(pagante))) {
					throw new BusinessException("> "+a.getCodiceAbbonamento()+
							" ignorato: nelle varie istanze ha paganti diversi");
				} else {
					pagante = ia.getPagante();
				}
			}
		}
		//Verifica se paganti sono proprio i promotori
		if ((pagante != null) && (promotore != null)) {
			if (!promotore.equals(pagante)) {
				throw new BusinessException("> "+a.getCodiceAbbonamento()+
						" ignorato: ha promotore e pagante diversi");
			}
		}
		//rimuove come promotore e salva come pagante
		for (IstanzeAbbonamenti ia:iaList) {
			ia.setPromotore(null);
			ia.setPagante(promotore);
			iaDao.update(ses, ia);
		}
		LOG.info("OK "+a.getCodiceAbbonamento());
	}
}
