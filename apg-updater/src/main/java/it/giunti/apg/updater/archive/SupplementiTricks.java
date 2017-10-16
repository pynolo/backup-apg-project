package it.giunti.apg.updater.archive;


public class SupplementiTricks {
	
	public static int ID_VS_SETT2012 = 150;
	//public static int ID_VS_SETT2013 = 550;
	public static int ID_SI_SETT2012 = 281;
	//public static int ID_SI_SETT2013 = 551;
	public static int ID_VS_AREAWEB2012 = 16;
	public static int ID_VS_AREAWEB2013 = 26;
	public static int ID_SI_AREAWEB2012 = 20;
	public static int ID_SI_AREAWEB2013 = 31;
			
//	
//	public static void addSupplementoFromPrevInstance(String letteraPeriodico,
//			Integer idFasPrevious, Integer idOldSupp, Integer idNewSupp) throws BusinessException {
//		Session ses = SessionFactory.getSession();
//		Transaction trn = ses.beginTransaction();
//		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
//		try {
//			Supplementi newSupp = GenericDao.findById(ses, Supplementi.class, idNewSupp);
//			List<IstanzeAbbonamenti> iaList;
//			int offset = 0;
//			int count = 0;
//			do {
//				iaList = findIstanzeByFasInizioSupplemento(ses, letteraPeriodico, idFasPrevious, idOldSupp, offset, 250);
//				offset += iaList.size();
//				String logString = "";
//				for (IstanzeAbbonamenti ia:iaList) {
//					IstanzeAbbonamenti lastIa = iaDao.findUltimaIstanzaByAbbonamento(ses,
//							ia.getAbbonamento().getId());
//					if (lastIa.getId() != ia.getId()) {
//						if (!lastIa.getSupplementiSet().contains(newSupp)) {
//							lastIa.getSupplementiSet().add(newSupp);
//							count++;
//							logString += ia.getAbbonamento().getCodiceAbbonamento()+"; ";
//						}
//						iaDao.update(ses, lastIa);
//					}
//				}
//				System.out.println(offset+": "+logString);
//				ses.flush();
//				ses.clear();
//			} while (iaList.size() > 0);
//			trn.commit();
//			System.out.println("Aggiunti "+count+" supplementi su "+offset+" abbonamenti candidati");
//		} catch (HibernateException e) {
//			trn.rollback();
//			throw new BusinessException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//	}
//	
//	public static void removeSupplementoFromPrevInstance(String letteraPeriodico,
//			Integer idSuppOld, Integer idSuppErr) throws BusinessException {
//		Session ses = SessionFactory.getSession();
//		Transaction trn = ses.beginTransaction();
//		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
//		try {
//			Supplementi suppErr = GenericDao.findById(ses, Supplementi.class, idSuppErr);
//			List<Integer> idList;
//			int offset = 0;
//			int count = 0;
//			do {
//				idList = findSupplementoErrato(ses, idSuppOld, idSuppErr, offset, 250);
//				offset += idList.size();
//				String logString = "";
//				for (Integer id:idList) {
//					IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, id);
//					if (ia != null) {
//						ia.getSupplementiSet().remove(suppErr);
//						iaDao.update(ses, ia);
//						logString += ia.getAbbonamento().getCodiceAbbonamento()+"; ";
//						count++;
//					}
//				}
//				System.out.println(offset+": "+logString);
//				ses.flush();
//				ses.clear();
//			} while (idList.size() > 0);
//			trn.commit();
//			System.out.println("Rimossi "+count+" supplementi da "+offset+" abbonamenti candidati");
//		} catch (HibernateException e) {
//			trn.rollback();
//			throw new BusinessException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	private static List<IstanzeAbbonamenti> findIstanzeByFasInizioSupplemento(Session ses,
//			String letteraPeriodico, Integer idFas, Integer idSup,
//			int offset, int pageSize) {
//		String qs = "select ia from IstanzeAbbonamenti ia " +
//				"join ia.supplementiSet as s with s.id = :id1 "+
//				"where "+
//				"ia.abbonamento.codiceAbbonamento like :s1 and "+
//				"ia.invioBloccato = :b1 and "+
//				"ia.fascicoloInizio.id = :id2 "+
//				"order by ia.id asc";
//		Query q = ses.createQuery(qs);
//		q.setParameter("id1", idSup, IntegerType.INSTANCE);
//		q.setParameter("s1", letteraPeriodico+"%", StringType.INSTANCE);
//		q.setParameter("b1", Boolean.FALSE);
//		q.setParameter("id2", idFas, IntegerType.INSTANCE);
//		q.setFirstResult(offset);
//		q.setMaxResults(pageSize);
//		List<IstanzeAbbonamenti> iaList = q.list();
//		return iaList;
//	}
//
//	@SuppressWarnings("unchecked")
//	private static List<Integer> findSupplementoErrato(Session ses,
//			Integer idSuppOld, Integer idSuppErr,
//			int offset, int pageSize) {
////		String sql = "select sia.id_istanza_abbonamento from supplementi_istanze_abbonamenti as sia where " +
////				"sia.id_supplemento = :id1 and " +
////				"sia.id_istanza_abbonamento in (" +
////						"select sia2.id_istanza_abbonamento from supplementi_istanze_abbonamenti sia2 where " +
////						"sia2.id_supplemento = :id2)" +
////				"order by sia.id_istanza_abbonamento asc";
//		String sql = "select si1.id_istanza_abbonamento from supplementi_istanze_abbonamenti as si1, " +
//					"supplementi_istanze_abbonamenti as si2 where " +
//				"si1.id_istanza_abbonamento = si2.id_istanza_abbonamento and " +
//				"si1.id_supplemento = :id1 and " +
//				"si2.id_supplemento = :id2 " +
//				"order by si1.id_istanza_abbonamento asc";
//		Query q = ses.createSQLQuery(sql);
//		q.setParameter("id1", idSuppOld, IntegerType.INSTANCE);
//		q.setParameter("id2", idSuppErr, IntegerType.INSTANCE);
////		q.setFirstResult(offset);
////		q.setMaxResults(pageSize);
//		List<Integer> idList = q.list();
//		return idList;
//	}
	
}
