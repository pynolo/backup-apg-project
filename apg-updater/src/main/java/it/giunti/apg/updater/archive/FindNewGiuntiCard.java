package it.giunti.apg.updater.archive;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.QueryFactory;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindNewGiuntiCard {

	private static final Logger LOG = LoggerFactory.getLogger(FindNewGiuntiCard.class);
	
	private static final String SEP = ";";
	private static final int DELTA_MESI = 7;
	private static final int PAGE_SIZE = 500;
	
	public static void findNew(Integer idPeriodico, boolean debug, String reportFileName)
			throws BusinessException, IOException {
		ReportWriter reportWriter = new ReportWriter(reportFileName);
		findNew(idPeriodico, debug, reportWriter);
		reportWriter.close();
	}
	
	private static void findNew(Integer idPeriodico, boolean debug, ReportWriter reportWriter)
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		AnagraficheDao anaDao = new AnagraficheDao();
		Transaction trn = ses.beginTransaction();
		try {
			Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
			LOG.info("Elaborazione delle istanze di "+p.getNome());
			List<IstanzeAbbonamenti> iaList = findIstanzeByPeriodico(ses, idPeriodico);
			LOG.info("Estratte "+iaList.size()+" istanze con anagrafiche senza GiuntiCard");
			for (IstanzeAbbonamenti ia:iaList) {
				reportWriter.print(ia);
				if (!debug) {
					ia.getAbbonato().setGiuntiCard(Boolean.TRUE);
					anaDao.update(ses, ia.getAbbonato());
				}
			}
			if (!debug) {
				trn.commit();
				LOG.warn("Modifiche scritte su DB");
			} else {
				trn.rollback();
				LOG.warn("Modalita' DEBUG: nessuna modifica e' stata scritta");
			}
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info("Termine estrazione.");
	}
	
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findIstanzeByPeriodico(Session ses, Integer idPeriodico) throws HibernateException {
		FascicoliDao fasDao = new FascicoliDao();
		Fascicoli fascicolo = fasDao.findPrimoFascicoloNonSpedito(ses, idPeriodico, new Date(), false);
		List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
		//ottiene le date di 7 mesi prima e dopo l'evasione del fascicolo
		Date dtFine = addMonth(fascicolo.getDataInizio(), (-1)*DELTA_MESI);
		
		//estrae i tipi abbonamento associati ad abbonamenti attivi (tipi solo cartacei!)
		//ovvero: i tipi degli ia con attivi al tempo del fascicolo e che scadano DELTA_MESI prima (x succ)
		QueryFactory qfLst = new QueryFactory(ses, "select distinct ia.listino from IstanzeAbbonamenti as ia");
		qfLst.addWhere("ia.abbonamento.periodico.id = :d0");
		qfLst.addParam("d0", idPeriodico);
		qfLst.addWhere("ia.fascicoloInizio.dataInizio <= :d1");
		qfLst.addParam("d1", fascicolo.getDataInizio());
		qfLst.addWhere("ia.fascicoloFine.dataFine >= :d2");
		qfLst.addParam("d2", dtFine);
		Query tipiAbbQ = qfLst.getQuery();
		List<Listini> lstList = (List<Listini>) tipiAbbQ.list();
		
		//esegue una query per ciascun tipo abbonamento
		for (Listini lst:lstList) {
			LOG.info("Estrazione '"+lst.getTipoAbbonamento().getNome()+"'");
			String baseSelect = "select distinct ia from IstanzeAbbonamenti as ia ";
			QueryFactory qf = new QueryFactory(ses, baseSelect);
			qf.addWhere("ia.abbonato.giuntiCard = :b00");
			qf.addParam("b00", Boolean.FALSE); //NON deve aver ricevuto la GiuntiCard
			qf.addWhere("ia.listino.id = :p0");
			qf.addParam("p0", lst.getId());
			qf.addWhere("ia.fascicoloInizio.dataInizio <= :d1");//data inizio <= data fascicolo
			qf.addParam("d1", fascicolo.getDataInizio());
			qf.addWhere("(" +//regolare e pagato: spediti-totali<=gracing [es. 7-6<=1 ok]
						"((ia.fascicoliSpediti-ia.fascicoliTotali) < :p1 and " +
						"((ia.pagato = :b11 or ia.inFatturazione = :b12 or ia.listino.invioSenzaPagamento = :b13 or ia.listino.fatturaDifferita = :b14 or (ia.listino.prezzo < :d15)) and ia.dataDisdetta is null and ia.ultimaDellaSerie = :b16)) " +
					"or " +//pagato ma con prenotazione disdetta o non "istanzaPiùRecente":
						"((ia.fascicoliSpediti < ia.fascicoliTotali) and " +
						"((ia.pagato = :b21 or ia.inFatturazione = :b22 or ia.listino.invioSenzaPagamento = :b23 or ia.listino.fatturaDifferita = :b24 or (ia.listino.prezzo < :d25)) and (ia.dataDisdetta is not null or ia.ultimaDellaSerie = :b26))) " +
					"or " +//gracing iniziale:
						"(ia.fascicoliSpediti < :p2) " +
					")");
			qf.addParam("b11", Boolean.TRUE);
			qf.addParam("b12", Boolean.TRUE);
			qf.addParam("b13", Boolean.TRUE);
			qf.addParam("b14", Boolean.TRUE);
			qf.addParam("d15", AppConstants.SOGLIA);
			qf.addParam("b16", Boolean.TRUE);
			qf.addParam("b21", Boolean.TRUE);
			qf.addParam("b22", Boolean.TRUE);
			qf.addParam("b23", Boolean.TRUE);
			qf.addParam("b24", Boolean.TRUE);
			qf.addParam("d25", AppConstants.SOGLIA);
			qf.addParam("b26", Boolean.FALSE);
			qf.addParam("p1", lst.getGracingFinale());
			qf.addParam("p2", lst.getGracingIniziale());
			qf.addWhere("ia.invioBloccato = :b4");
			qf.addParam("b4", false);
			qf.addOrder("ia.id asc");
			Query iaQ = qf.getQuery();
			
			//Estrazione paginata
			int offset = 0;
			int size = 0;
			do {
				if (offset != 0) LOG.info("Estratti "+result.size()+" destinatari");
				iaQ.setFirstResult(offset);
				iaQ.setMaxResults(PAGE_SIZE);
				List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) iaQ.list();
				size = iaList.size();
				offset += size;
				result.addAll(iaList);
				ses.flush();
				ses.clear();
			} while (size > 0);
		}
		return result;
	}
	
	private static Date addMonth(Date data, int mesi) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(data);
		cal.add(Calendar.MONTH, mesi);
		return cal.getTime();
	}
	
	
	// Inner classes
	
	
	public static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void print(IstanzeAbbonamenti ia) throws IOException {
			Anagrafiche anag = ia.getAbbonato();
			String line = ia.getAbbonamento().getCodiceAbbonamento()+SEP+
					ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+SEP+
					anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (anag.getIndirizzoPrincipale().getNome() != null) line += " "+anag.getIndirizzoPrincipale().getNome();
			line += SEP+
					anag.getIndirizzoPrincipale().getLocalita()+" "+anag.getIndirizzoPrincipale().getProvincia()+SEP+
					anag.getUid()+"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
		
	}
}
