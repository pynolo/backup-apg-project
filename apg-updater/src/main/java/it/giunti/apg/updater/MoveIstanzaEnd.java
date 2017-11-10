package it.giunti.apg.updater;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
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
	private static final int X1801L = 709;
	private static final int X1905L = 713;
	
	@SuppressWarnings("unchecked")
	public static void moveD() throws BusinessException, IOException {
		File outputFile = File.createTempFile("updateIstanzeD_", ".txt");
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		LOG.warn("Output file: "+outputFile.getAbsolutePath());
		int offset = 0;
		
		//Sessione singola istanza
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Fascicoli fasCambio = GenericDao.findById(ses, Fascicoli.class, X1801L);
			Fascicoli fasFinale = GenericDao.findById(ses, Fascicoli.class, X1905L);
			String hql = "from IstanzeAbbonamenti ia where "+
					"ia.invioBloccato = :b1 and "+
					"ia.fascicoloFine.periodico = :id1 and "+
					"ia.fascicoloFine.dataInizio >= :dt1 "+//or ia.fascicoloFine.id = :id2 ) "+
					"order by ia.id";
			int pageSize = 250;
			List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
			do {
				Query q = ses.createQuery(hql);
				q.setParameter("b1", Boolean.FALSE);
				q.setParameter("id1", fasCambio.getPeriodico());
				q.setParameter("dt1", fasCambio.getDataInizio());
				//q.setParameter("id2", fasCambio.getId());
				q.setFirstResult(offset);
				q.setMaxResults(pageSize);
				iaList = (List<IstanzeAbbonamenti>) q.list();
				offset += iaList.size();
				for (IstanzeAbbonamenti ia:iaList) {
					moveIstanza(ses, writer, ia, fasFinale);
				}
				ses.flush();
				ses.clear();
			} while (iaList.size() > 0);
			
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		writer.close();
		
		LOG.warn("Output file: "+outputFile.getAbsolutePath());
		LOG.info("Aggiornati "+offset+" abbonamenti");
	}
	
	private static void moveIstanza(Session ses, PrintWriter writer,
			IstanzeAbbonamenti ia, Fascicoli newFasFine) throws HibernateException {
		//Controlla che non finisca dopo!
		if (ia.getFascicoloFine().getDataInizio().after(newFasFine.getDataInizio())) {
			String msg = ia.getAbbonamento().getCodiceAbbonamento()+" ["+
					ia.getId()+"] finisce col numero "+ia.getFascicoloFine().getTitoloNumero()+" del "+
					ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataInizio());
			writer.write(msg+"\r\n");
			LOG.debug(msg);
			throw new HibernateException(msg);
		}
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
		String msg = ia.getAbbonamento().getCodiceAbbonamento()+" ["+
				ia.getId()+"] da "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+" a "+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataInizio())+" ("+
				ia.getFascicoliTotali()+" fasc.)";
		writer.write(msg+"\r\n");
		LOG.debug(msg);
	}
	
}
