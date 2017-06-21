package it.giunti.apg.server.servlet;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class RinnovoMassivoUtil {
	
	private static FascicoliDao fasDao = new FascicoliDao();
	
	@SuppressWarnings("unchecked")
	public static List<IstanzeAbbonamenti> findIstanzeByFascicoloListinoPagato(Session ses, Listini lst,
			Integer idFascicoloInizio, boolean soloRegolari, int pageSize) throws BusinessException {
		try {
			String hql = "from IstanzeAbbonamenti ia where " +
					"ia.fascicoloInizio.id = :id1 and " +
					"ia.ultimaDellaSerie = :b1 and " +//TRUE
					"ia.listino.id = :id2 and ";
			if (soloRegolari) hql += "(ia.pagato = :b2 or ia.inFatturazione = :b3) and ";
			hql += "ia.invioBloccato = :b4 and " +
					"ia.dataDisdetta is null and " +
					"(" + //non devono proprio esistere istanze successive (bloccate o meno!)
						"select count (ia2.id) from IstanzeAbbonamenti ia2 where " +
						"ia2.abbonamento.id = ia.abbonamento.id and " +
						"ia2.fascicoloFine.dataInizio > ia.fascicoloFine.dataInizio" +
					") = :i1 " +
					"order by ia.id asc ";
			Query q = ses.createQuery(hql);
			q.setInteger("id1", idFascicoloInizio);
			q.setBoolean("b1", true);
			q.setInteger("id2", lst.getId());
			if (soloRegolari) {
				q.setBoolean("b2", Boolean.TRUE);
				q.setBoolean("b3", Boolean.TRUE);
			}
			q.setBoolean("b4", false);
			q.setInteger("i1", 0);
			q.setMaxResults(pageSize);
			List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
			return iaList;
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static long countIstanzeByFascicoloListinoPagato(Session ses, Listini tal,
			Integer idFascicoloInizio, boolean soloRegolari) throws BusinessException {
		try {
			String hql = "select count(ia.id) from IstanzeAbbonamenti ia where " +
					"ia.fascicoloInizio.id = :id1 and " +
					"ia.ultimaDellaSerie = :b1 and " +
					"ia.listino.id = :id2 and ";
			if (soloRegolari) hql += "(ia.pagato = :b2 or ia.inFatturazione = :b3) and ";
			hql += "ia.invioBloccato = :b4 and " +
					"ia.dataDisdetta is null and " +
					"(" + //non devono proprio esistere istanze successive (bloccate o meno!)
						//la condizione sulla ultimaDellaSerie non è sufficiente perché i bloccati non sono mai ultimaDellaSerie=true
						"select count (ia2.id) from IstanzeAbbonamenti ia2 where " +
						"ia2.abbonamento.id = ia.abbonamento.id and " +
						"ia2.fascicoloFine.dataInizio > ia.fascicoloFine.dataInizio" +
					") = :i1 ";
			Query q = ses.createQuery(hql);
			q.setInteger("id1", idFascicoloInizio);
			q.setBoolean("b1", true);
			q.setInteger("id2", tal.getId());
			if (soloRegolari) {
				q.setBoolean("b2", Boolean.TRUE);
				q.setBoolean("b3", Boolean.TRUE);
			}
			q.setBoolean("b4", false);
			q.setInteger("i1", 0);
			List<Object> list = (List<Object>) q.list();
			long count = (Long) list.get(0);
			return count;
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	
	public static IstanzeAbbonamenti renewToTransientNoOpzioni(Session ses, IstanzeAbbonamenti oldIa,
			Listini lst, Date renewalDate, String idUtente) 
			throws BusinessException {
		try {
			IstanzeAbbonamenti ia = new IstanzeAbbonamenti();
			ia.setAbbonamento(oldIa.getAbbonamento());
			ia.setAbbonato(oldIa.getAbbonato());
			ia.setPagante(oldIa.getPagante());
			ia.setPromotore(null);
			ia.setListino(lst);
			ia.setCopie(oldIa.getCopie());
			ia.setFascicoliSpediti(0);
			ia.setFascicoliTotali(lst.getNumFascicoli());
			ia.setDataCreazione(renewalDate);
			ia.setDataSyncMailing(ServerConstants.DATE_FAR_PAST);
			ia.setDataCambioTipo(renewalDate);
			ia.setDataModifica(renewalDate);
			ia.setPagato(false);
			ia.setInvioBloccato(false);
			ia.setIdUtente(idUtente);
			//Inizio e fine
			Fascicoli fasInizio = fasDao.findFascicoliAfterFascicolo(ses, oldIa.getFascicoloFine(), 1);
			Fascicoli fasFine = fasDao.findFascicoliAfterFascicolo(ses, fasInizio, lst.getNumFascicoli()-1);
			ia.setFascicoloInizio(fasInizio);
			ia.setFascicoloFine(fasFine);
			return ia;
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
}
