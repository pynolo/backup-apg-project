package it.giunti.apg.updater;

import it.giunti.apg.server.OpzioniUtil;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.business.FileFormatCommon;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachMissingOpzioniObbligatorie {
	
	private static final Logger LOG = LoggerFactory.getLogger(AttachMissingOpzioniObbligatorie.class);
	
	@SuppressWarnings("unchecked")
	public static void attachMissingOpzioniObbligatorie(String letteraPeriodico) throws BusinessException,
			IOException {
		int count = 1;
		Date today = new Date();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			String hql1 = "select distinct ol.listino from OpzioniListini ol where "+
					"ol.listino.tipoAbbonamento.periodico.lettera like :s2 ";
			Query q1 = ses.createQuery(hql1);
			q1.setParameter("s2", letteraPeriodico, StringType.INSTANCE);
			List<Listini> lList = q1.list();
			
			List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
			for (Listini l:lList) {
				boolean ok = false;
				if (l.getDataFine() == null) {
					ok = true;
				} else {
					if (l.getDataFine().after(today)) ok = true;
				}
				if (ok) {
					String hql2 = "from IstanzeAbbonamenti ia where "+
							"ia.abbonamento.codiceAbbonamento like :s1 and "+
							//"ia.fascicoloInizio.dataNominale <= :dt1 and "+
							//"ia.fascicoloFine.dataNominale >= :dt2 and "+
							"ia.listino.id = :id1 ";
					Query q2 = ses.createQuery(hql2);
					q2.setParameter("s1", letteraPeriodico+"%", StringType.INSTANCE);
					//q2.setParameter("dt1", today, DateType.INSTANCE);
					//q2.setParameter("dt2", today, DateType.INSTANCE);
					q2.setParameter("id1", l.getId(), IntegerType.INSTANCE);
					List<IstanzeAbbonamenti> list = q2.list();
					iaList.addAll(list);
				}
			}
			
			//Ciclo su tutte le righe
			for (IstanzeAbbonamenti ia:iaList) {
				if (ia.getOpzioniIstanzeAbbonamentiSet() == null) {
					ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
				}
				if (ia.getOpzioniIstanzeAbbonamentiSet().size() == 0) {
					OpzioniUtil.addOpzioniObbligatorie(ses, ia, false);
					LOG.info(FileFormatCommon.formatInteger(6, count)+") "+
							ia.getAbbonamento().getCodiceAbbonamento()+" "+
							ia.getListino().getTipoAbbonamento().getCodice()+" ["+
							ServerConstants.FORMAT_DAY.format(ia.getDataCreazione())+"] assegnate "+
							ia.getOpzioniIstanzeAbbonamentiSet().size()+" opzioni");
					count++;
				} else {
					LOG.info("        "+
							ia.getAbbonamento().getCodiceAbbonamento()+" "+
							ia.getListino().getTipoAbbonamento().getCodice()+" ["+
							ServerConstants.FORMAT_DAY.format(ia.getDataCreazione())+"] ha gia' "+
							ia.getOpzioniIstanzeAbbonamentiSet().size()+" opzioni");
				}
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info("Aggiunte opzioni a "+count+" istanze");
	}
	
}
