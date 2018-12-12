package it.giunti.apg.automation.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.QueryFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Listini;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class CreateStatAbbonatiBusiness {

	public static final String CODICE_OMAGGI = "09";
	private static final int DELTA_MESI = 7;
	
	@SuppressWarnings("unchecked")
	public Integer countProssimaTiratura(Session ses, Date today, Integer idPeriodico,
			boolean soloNuovi, boolean soloMorosi,
			boolean soloPagati, boolean soloOmaggi)
			throws BusinessException {
		Integer result = 0;
		FascicoliDao fDao = new FascicoliDao();
		Fascicoli fasNext = fDao.findPrimoFascicoloNonSpedito(ses, idPeriodico, today, false);
		if (fasNext == null) throw new BusinessException("Impossibile trovare fascicoli non spediti dopo il "+
				ServerConstants.FORMAT_DAY.format(today));
		//ottiene le date di 7 mesi prima e dopo l'evasione del fascicolo
		Date dtFine = new Date(fasNext.getDataInizio().getTime()-AppConstants.MONTH*DELTA_MESI);
		
		//estrae i tipi abbonamento associati ad abbonamenti attivi
		//ovvero: i tipi degli ia con attivi al tempo del fascicolo e che scadano DELTA_MESI prima (x succ)
		String tipiAbbHql = "select distinct ia.listino from IstanzeAbbonamenti as ia " +
				"where ia.abbonamento.periodico.id = :d0 and " +
				"ia.fascicoloInizio.dataInizio <= :d1 and " +
				"ia.fascicoloFine.dataInizio >= :d2 ";
		Query tipiAbbQ = ses.createQuery(tipiAbbHql);
		tipiAbbQ.setInteger("d0", idPeriodico);
		tipiAbbQ.setDate("d1", fasNext.getDataInizio());
		tipiAbbQ.setDate("d2", dtFine);
		List<Listini> lstList = (List<Listini>) tipiAbbQ.list();
		List<Integer> copieList = new ArrayList<Integer>();
		//esegue una query per ciascun tipo abbonamento
		for (Listini lst:lstList) {
			String baseSelect = "select ia.copie from IstanzeAbbonamenti as ia ";
			QueryFactory qf = new QueryFactory(ses, baseSelect);
			qf.addWhere("ia.listino.id = :p0");
			qf.addParam("p0", lst.getId());
			qf.addWhere("ia.fascicoloInizio.dataInizio <= :d1");//data inizio <= data prox fascicolo
			qf.addParam("d1", fasNext.getDataInizio());
			qf.addWhere("(" +//regolare e pagato: spediti-totali<=gracing [es. 7-6<=1 ok]
						"((ia.fascicoliSpediti-ia.fascicoliTotali) < :p1 and " +
						"((ia.pagato = :b11 or ia.fatturaDifferita = :b12 or ia.listino.invioSenzaPagamento = :b13 or ia.listino.fatturaDifferita = :b14 or (ia.listino.prezzo < :d15)) and ia.dataDisdetta is null and ia.ultimaDellaSerie = :b16)) " +
					"or " +//pagato ma con disdetta o non "ultima della serie":
						"((ia.fascicoliSpediti < ia.fascicoliTotali) and " +
						"((ia.pagato = :b21 or ia.fatturaDifferita = :b22 or ia.listino.invioSenzaPagamento = :b23 or ia.listino.fatturaDifferita = :b24 or (ia.listino.prezzo < :d25)) and (ia.dataDisdetta is not null or ia.ultimaDellaSerie = :b26))) " +
					"or " +//gracing iniziale:
						"(ia.fascicoliSpediti < :p2) " +
					")");
			qf.addParam("b11", true);
			qf.addParam("b12", true);
			qf.addParam("b13", true);
			qf.addParam("b14", true);
			qf.addParam("d15", AppConstants.SOGLIA);
			qf.addParam("b16", Boolean.TRUE);
			qf.addParam("b21", true);
			qf.addParam("b22", true);
			qf.addParam("b23", true);
			qf.addParam("b24", true);
			qf.addParam("d25", AppConstants.SOGLIA);
			qf.addParam("b26", Boolean.FALSE);
			qf.addParam("p1", lst.getGracingFinale());
			qf.addParam("p2", lst.getGracingIniziale());
			qf.addWhere("ia.invioBloccato = :b4");
			qf.addParam("b4", false);
			//Nuovi
			if (soloNuovi) {
				//La creazione dell'istanza deve essere lo stesso giorno della
				//creazione dell'abbonamento
				qf.addWhere("ia.dataCreazione = ia.abbonamento.dataCreazione");
			}
			//Morosi
			if (soloMorosi) {
				qf.addWhere("ia.fatturaDifferita = :morB1"); //non fatturato
				qf.addWhere("ia.listino.fatturaDifferita = :morB2"); //non fatturato
				qf.addWhere("ia.listino.prezzo > :morD1"); //non omaggio
				qf.addWhere("(select count(p.id) from Pagamenti p where p.istanzaAbbonamento.id = ia.id) = 0");
				qf.addParam("morB1", false);
				qf.addParam("morB2", false);
				qf.addParam("morD1", AppConstants.SOGLIA);
				
			}
			//Quote pagate
			if (soloPagati) {
				qf.addWhere("(ia.fatturaDifferita = :pagB1 or " +
						"ia.listino.fatturaDifferita = :pagB2 or " +
						"(select count(p.id) from Pagamenti p where p.istanzaAbbonamento.id = ia.id) > 0 " +
						")");
				qf.addParam("pagB1", true);
				qf.addParam("pagB2", true);
			}
			//Omaggi
			if (soloOmaggi) {
				qf.addWhere("ia.listino.tipoAbbonamento.codice = :omaS1");
				qf.addParam("omaS1", CODICE_OMAGGI);
			}
			
			Query iaQ = qf.getQuery();
			List<Integer> cList = (List<Integer>) iaQ.list();
			copieList.addAll(cList);
		}
		for (Integer i:copieList) {
			result += i;
		}
		return result;
	}
	
	/**
	 * Cerca il numero attuale e conta le istanze per cui questo numero è
	 * compreso tra inizio e fine
	 * ma hanno una data disdetta o un blocco
	 * @param ses
	 * @param today
	 * @param idPeriodico
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer countDisdette(Session ses, Date today, Integer idPeriodico) {
		Fascicoli fasAttuale = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses, idPeriodico, today);
		Integer result = 0;
		if (fasAttuale != null) {
			//Nuovi
			String qs = "select count(ia.id) from IstanzeAbbonamenti as ia where " +
					"ia.abbonamento.periodico.id = :i1 and " +
					"ia.fascicoloInizio.dataInizio <= :dt1 and " +//il fascicolo iniziale è quello attuale o nel passato
					"ia.fascicoloFine.dataInizio >= :dt2 and " +//il fascicolo finale è quello attuale o nel futuro
					"(ia.dataDisdetta is not null or ia.invioBloccato = :b1)";
			Query q = ses.createQuery(qs);
			q.setInteger("i1", idPeriodico);
			q.setDate("dt1", fasAttuale.getDataInizio());
			q.setDate("dt2", fasAttuale.getDataInizio());
			q.setBoolean("b1", true);
			List<Long> list = (List<Long>) q.list();
			if(list != null) {
				if (list.size() > 0) {
					result += list.get(0).intValue();
				}
			}
		} else {
			result = -1;
		}
		return result;
	}

	/**
	 * Morosi dell'anno precedente
	 * @param ses
	 * @param today
	 * @param idPeriodico
	 * @return
	 */
	public Integer countMorosiAnnoPrec(Session ses, Date today, Integer idPeriodico) {
		Calendar ago = new GregorianCalendar();
		ago.setTime(today);
		ago.add(Calendar.YEAR, -1);
		return 0;//countMorosi(ses, ago.getTime(), idPeriodico);
	}
	
}
