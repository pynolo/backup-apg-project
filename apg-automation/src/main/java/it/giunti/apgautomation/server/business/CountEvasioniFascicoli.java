package it.giunti.apgautomation.server.business;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.EvasioniFascicoliDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Periodici;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountEvasioniFascicoli {

	private static final Logger LOG = LoggerFactory.getLogger(CountEvasioniFascicoli.class);
	
	private static final int PAGE_SIZE = 5000;
	private static final String SEP = ";";
	
	public static int countEvasioni(Periodici periodico, StringBuilder builder, String endOfLine)
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		Transaction trn = ses.beginTransaction();
		int count = 0;
		int offset = 0;
		int diffCount = 0;
		try {
			builder.append("Abbonamento"+SEP+"Inizio"+SEP+"Fine"+SEP+"Val.errato"+SEP+"Val.corretto"+endOfLine);
			LOG.info("Elaborazione delle istanze di "+periodico.getNome());
			do {
				List<IstanzeAbbonamenti> iaList = findIstanzeByPeriodico(ses,
						periodico.getId(), offset, PAGE_SIZE);
				count = iaList.size();
				offset += count;
				for (IstanzeAbbonamenti ia:iaList) {
					int newSpediti = efDao.countFascicoliSpediti(ses, ia.getId());
					if (ia.getFascicoliSpediti() != newSpediti) {
						diffCount++;
						appendLine(builder, ia, newSpediti, endOfLine);
						ia.setFascicoliSpediti(newSpediti);
						iaDao.update(ses, ia);
					}
				}
				ses.flush();
				ses.clear();
				LOG.info("Verificate "+offset+" istanze. Differenze "+diffCount);
			} while (count > 0);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info("Termine verifica "+periodico.getNome()+": "+offset+" istanze. "+diffCount+" differenze.");
		return diffCount;
	}
	
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findIstanzeByPeriodico(Session ses, Integer idPeriodico,
			Integer offset, Integer size) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
			"ia.abbonamento.periodico.id = :id1 and " +
			"ia.ultimaDellaSerie = :b1 " +
			"order by ia.id desc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE);
		q.setFirstResult(offset);
		q.setMaxResults(size);
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}
		
	public static void appendLine(StringBuilder builder, IstanzeAbbonamenti ia, int newSpediti,
			String endOfLine) throws IOException {
		String line = ia.getAbbonamento().getCodiceAbbonamento()+SEP+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+SEP+
				ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataInizio())+SEP+
				ia.getFascicoliSpediti()+SEP+
				newSpediti+endOfLine;
		builder.append(line);
	}

}
