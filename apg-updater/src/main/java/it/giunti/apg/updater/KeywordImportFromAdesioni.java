package it.giunti.apg.updater;


public class KeywordImportFromAdesioni {
//
//	private static final Logger LOG = LoggerFactory.getLogger(KeywordImportFromAdesioni.class);
//
//	private static int PAGE_SIZE = 500;
//	private static Map<Integer,Keyword> keywordMap = new HashMap<Integer, Keyword>();
//	
//	@SuppressWarnings("unchecked")
//	public static void migrateToKeywords() 
//			throws BusinessException, IOException {
//		Session ses = SessionFactory.getSession();
//		Transaction trn = ses.beginTransaction();
//		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
//		int offset = 0;
//		String hql = "from IstanzeAbbonamenti ia order by ia.id";
//		long start = new Date().getTime();
//		System.out.println("Start: "+ServerConstants.FORMAT_TIMESTAMP.format(new Date()));
//		try {
//			loadKeywordMap(ses);
//			do {
//				Query q = ses.createQuery(hql);
//				q.setFirstResult(offset);
//				q.setMaxResults(PAGE_SIZE);
//				iaList = (List<IstanzeAbbonamenti>) q.list();
//				for (IstanzeAbbonamenti ia:iaList) {
//					migrate(ses, ia);
//				}
//				offset += iaList.size();
//				Long now = new Date().getTime();
//				System.out.println("updated "+offset+" instances (in "+(now-start)+" millis)");
//				start = now;
//				ses.flush();
//				ses.clear();
//			} while (iaList.size() == PAGE_SIZE);
//			trn.commit();
//		} catch (HibernateException e) {
//			trn.rollback();
//			throw new BusinessException(e.getMessage(), e);
//		} catch (IOException e) {
//			trn.rollback();
//			throw new IOException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	private static void loadKeywordMap(Session ses) {
//		Query q = ses.createQuery("from Keyword k order by k.id");
//		List<Keyword> list = q.list();
//		for (Keyword kwd:list) {
//			keywordMap.put(kwd.getId(), kwd);
//		}
//	}
//	
//	private static void migrate(Session ses, IstanzeAbbonamenti ia) 
//			throws HibernateException, IOException {
//		Integer idAdesione = null;
//		if (ia.getIdAdesione() != null) {
//			idAdesione = ia.getIdAdesione();
//			KeywordIstanzeAbbonamenti kia = new KeywordIstanzeAbbonamenti();
//			Keyword kwd = keywordMap.get(idAdesione);
//			if (kwd != null) { 
//				kia.setKeyword(kwd);
//				kia.setIstanza(ia);
//				GenericDao.saveGeneric(ses, kia);
//			} else {
//				LOG.error("Not found: adesione "+idAdesione);
//			}
//		}
//	}

}
