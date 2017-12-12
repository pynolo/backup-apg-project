package it.giunti.apg.updater;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveIstanzaEnd {
	
	private static final Logger LOG = LoggerFactory.getLogger(MoveIstanzaEnd.class);
	//private static final int X1801L = 709;
	//private static final int X1905L = 713;
	private static final int X1752L = 610;
	private static final int X1753L = 611;
	private static final int X1856L = 708;
	private static final int art60821M = 191; //Articolo
	
	public static void moveD() throws BusinessException, IOException {
		File outputFile = File.createTempFile("updateIstanzeD_", ".txt");
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		LOG.warn("Output file: "+outputFile.getAbsolutePath());
		
		//Sessione singola istanza
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Fascicoli fasIniz52 = GenericDao.findById(ses, Fascicoli.class, X1752L);
			Fascicoli fasIniz53 = GenericDao.findById(ses, Fascicoli.class, X1753L);
			Fascicoli newFasFine = GenericDao.findById(ses, Fascicoli.class, X1856L);
			Articoli articolo = GenericDao.findById(ses, Articoli.class, art60821M);
			moveByFascicoloInizio(ses, writer, fasIniz52, newFasFine, articolo);
			moveByFascicoloInizio(ses, writer, fasIniz53, newFasFine, articolo);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		writer.close();
		
		LOG.warn("Output file: "+outputFile.getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	private static void moveByFascicoloInizio(Session ses, PrintWriter writer,
			Fascicoli fasInizio, Fascicoli newFasFine, Articoli articolo) {
		int offset = 0;
		String hql = "from IstanzeAbbonamenti ia where "+
				"ia.invioBloccato = :b1 and "+
				"ia.fascicoloInizio.id = :id1 "+//or ia.fascicoloFine.id = :id2 ) "+
				"order by ia.id";
		int pageSize = 250;
		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
		List<IstanzeAbbonamenti> list = null;
		do {
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE);
			q.setParameter("id1", fasInizio.getId());
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			list = (List<IstanzeAbbonamenti>) q.list();
			offset += list.size();
			iaList.addAll(list);
			ses.flush();
			ses.clear();
		} while (list.size() > 0);
		for (IstanzeAbbonamenti ia:iaList) {
			moveIstanza(ses, writer, ia, newFasFine);
			addArticolo(ses, writer, ia, articolo);
		}
		LOG.info("Aggiornati "+offset+" abbonamenti");
	}
	
	private static void moveIstanza(Session ses, PrintWriter writer,
			IstanzeAbbonamenti ia, Fascicoli newFasFine) throws HibernateException {
		String oldInizio = ia.getFascicoloInizio().getTitoloNumero()+ " "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio());
		String oldFine = ia.getFascicoloFine().getTitoloNumero()+ " "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine());
		//Sposta effettivamente
		ia.setFascicoloFine(newFasFine);
		//Conta il totale fascicoli
		List<Fascicoli> fList = new FascicoliDao().findFascicoliBetweenDates(ses, ia.getAbbonamento().getPeriodico().getId(),
				ia.getFascicoloInizio().getDataInizio(),
				ia.getFascicoloFine().getDataInizio());
		int totFascicoli = 0;
		for (Fascicoli fas:fList) {
			totFascicoli += fas.getFascicoliAccorpati();
		}
		ia.setFascicoliTotali(totFascicoli);
		new IstanzeAbbonamentiDao().update(ses, ia);
		String newInizio = ia.getFascicoloInizio().getTitoloNumero()+ " "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio());
		String newFine = ia.getFascicoloFine().getTitoloNumero()+ " "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine());
		String msg = ia.getAbbonamento().getCodiceAbbonamento()+" ["+
				ia.getId()+"] vecchio: "+oldInizio+" - "+oldFine+
				" nuovo: "+newInizio+" - "+newFine +" ("+
				ia.getFascicoliTotali()+" fasc.)";
		writer.write(msg+"\r\n");
		LOG.debug(msg);
	}
	
	private static void addArticolo(Session ses, PrintWriter writer,
			IstanzeAbbonamenti ia, Articoli articolo) throws HibernateException {
		EvasioniArticoli ea = new EvasioniArticoli();
		ea.setArticolo(articolo);
		ea.setCopie(ia.getCopie());
		ea.setDataCreazione(DateUtil.now());
		ea.setDataModifica(DateUtil.now());
		ea.setDataInvio(DateUtil.now());//Indicazione di Marzia al 12/12/17
		ea.setPrenotazioneIstanzaFutura(false);
		ea.setIdAbbonamento(ia.getAbbonamento().getId());
		ea.setIdAnagrafica(ia.getAbbonato().getId());
		ea.setIdArticoloListino(null);
		ea.setIdArticoloOpzione(null);
		ea.setIdIstanzaAbbonamento(ia.getId());
		ea.setIdTipoDestinatario(AppConstants.DEST_BENEFICIARIO);
		ea.setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
				
		new EvasioniArticoliDao().save(ses, ea);
		//String msg = ia.getAbbonamento().getCodiceAbbonamento()+" ["+
		//		ia.getId()+"] added "+
		//		ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+" a "+
		//		ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataInizio())+" ("+
		//		ia.getFascicoliTotali()+" fasc.)";
		//writer.write(msg+"\r\n");
		//LOG.debug(msg);
	}
}
