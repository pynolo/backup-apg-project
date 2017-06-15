package it.giunti.apg.updater;


public class ReplaceAdesioniScuola {
//	
//	private static final Logger LOG = LoggerFactory.getLogger(ReplaceAdesioniScuola.class);
//	
//	private static AnagraficheDao anaDao = new AnagraficheDao();
//	private static KeywordDao adeDao = new KeywordDao();
//	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
//	private static IndirizziDao indDao = new IndirizziDao();
//	
//	private static Anagrafiche anaTvp;
//	private static Keyword adeMedie;
//	private static Keyword adeSup;
//	private static Utenti admin;
//	
//	public static void replaceAdesioni() 
//			throws BusinessException, IOException {
//		//Sessione singola istanza
//		String report = "";
//		Session ses = SessionFactory.getSession();
//		Transaction trn = ses.beginTransaction();
//		try {
//			anaTvp = anaDao.findByCodiceCliente(ses, "C07Q75");
//			adeMedie = adeDao.findByDescrPrefix(ses, "MEDIE", 0, Integer.MAX_VALUE);
//			adeSup = adeDao.findByDescrPrefix(ses, "SUP", 0, Integer.MAX_VALUE);
//			admin = new UtentiDao().findUtenteByUserName(ses, "admin");
//			List<IstanzeAbbonamenti> iaList = findIstanzeByNoteAdesione(ses, "adesione:SUP");
//			List<IstanzeAbbonamenti> iaList2 = findIstanzeByNoteAdesione(ses, "adesione:MEDIE");
//			iaList.addAll(iaList2);
//			int count = 0;
//			String abboCodes = "";
//			for (IstanzeAbbonamenti ia:iaList) {
//				boolean changed = false;
//				// Art e Dossier
//				if (ia.getAbbonamento().getCodiceAbbonamento().startsWith("Q")) {
//					if (ia.getListino().getTipoAbbonamento().getCodice().equalsIgnoreCase("PE")) {
//						changed = replaceQ(ses, ia, report);
//					}
//				}
//				// Psicologia Scuola
//				if (ia.getAbbonamento().getCodiceAbbonamento().startsWith("D")) {
//					if (ia.getListino().getTipoAbbonamento().getCodice().equalsIgnoreCase("TV")) {
//						changed = replaceD(ses, ia, report);
//					}
//				}
//				if (!changed) {
//					String msg = "Non modificato: "+ia.getAbbonamento().getCodiceAbbonamento()+" "+
//							ia.getListino().getTipoAbbonamento().getCodice()+" "+
//							ia.getNote();
//					LOG.error(msg);
//					report += msg+"/r/n";
//				} else {
//					abboCodes += ia.getAbbonamento().getCodiceAbbonamento()+" ";
//					count++;
//				}
//			}
//			trn.commit();
//			LOG.info("Modificate "+count+" istanze: "+abboCodes);
//			report += "Modificate "+count+" istanze: "+abboCodes+"\r\n";
//		} catch (HibernateException e) {
//			trn.rollback();
//			throw new BusinessException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//		LOG.error(report);
//	}
//	
//	//@SuppressWarnings("unchecked")
//	//private static List<Adesioni> findAdesioniAdottatari(Session ses)
//	//		throws HibernateException {
//	//	String hql = "from Adesioni a where a.codice like :s1 or a.codice like :s2";
//	//	Query q = ses.createQuery(hql);
//	//	q.setParameter("s1", "medie%", StringType.INSTANCE);
//	//	q.setParameter("s2", "sup%", StringType.INSTANCE);
//	//	return q.list();
//	//}
//	
//	//private static boolean replaceQ(Session ses, IstanzeAbbonamenti ia, String rapporto) throws HibernateException {
//	//	String adeSuffix = null;
//	//	Adesioni nextAde = null;
//	//	if (ia.getAdesione().getCodice().startsWith("MEDIE")) {
//	//		adeSuffix = ia.getAdesione().getCodice().substring(5);
//	//		nextAde = adeMedie;
//	//	}
//	//	if (ia.getAdesione().getCodice().startsWith("SUP")) {
//	//		adeSuffix = ia.getAdesione().getCodice().substring(3);
//	//		nextAde = adeSup;
//	//	}
//	//	if (adeSuffix != null) {
//	//		Anagrafiche promotore = findAnagraficaByCognome(ses, adeSuffix);
//	//		if (promotore == null) promotore = createAnagrafica(ses, adeSuffix, rapporto);
//	//		ia.setPromotore(promotore);
//	//		ia.setAdesione(nextAde);
//	//		iaDao.update(ses, ia);
//	//		return true;
//	//	}
//	//	return false;
//	//}
//	
//	private static boolean replaceQ(Session ses, IstanzeAbbonamenti ia, String rapporto) throws HibernateException {
//		String adeSuffix = null;
//		Keyword nextAde = null;
//		if (ia.getNote().contains("MEDIE")) {
//			adeSuffix = ia.getNote().substring(ia.getNote().indexOf("MEDIE")+5);
//			if (adeSuffix.contains(" ")) {
//				adeSuffix = adeSuffix.substring(0, adeSuffix.indexOf(" "));
//			}
//			nextAde = adeMedie;
//		}
//		if (ia.getNote().contains("SUP")) {
//			adeSuffix = ia.getNote().substring(ia.getNote().indexOf("SUP")+3);
//			if (adeSuffix.contains(" ")) {
//				adeSuffix = adeSuffix.substring(0, adeSuffix.indexOf(" "));
//			}
//			nextAde = adeSup;
//		}
//		LOG.debug(ia.getAbbonamento().getCodiceAbbonamento()+" "+adeSuffix+" "+ia.getNote());
//		if (adeSuffix != null) {
//			if (adeSuffix.length() > 1) {
//				Anagrafiche promotore = findAnagraficaByCognome(ses, adeSuffix);
//				if (promotore == null) promotore = createAnagrafica(ses, adeSuffix, rapporto);
//				ia.setPromotore(promotore);
//				ia.setAdesione(nextAde);
//				iaDao.update(ses, ia);
//			}
//			return true;
//		}
//		return false;
//	}
//	
//	private static boolean replaceD(Session ses, IstanzeAbbonamenti ia, String rapporto) throws HibernateException {
//		String adeSuffix = null;
//		Keyword nextAde = null;
//		if (ia.getNote().contains("MEDIE")) {
//			adeSuffix = ia.getNote().substring(ia.getNote().indexOf("MEDIE")+5);
//			if (adeSuffix.contains(" ")) {
//				adeSuffix = adeSuffix.substring(0, adeSuffix.indexOf(" "));
//			}
//			nextAde = adeMedie;
//		}
//		if (ia.getNote().contains("SUP")) {
//			adeSuffix = ia.getNote().substring(ia.getNote().indexOf("SUP")+3);
//			if (adeSuffix.contains(" ")) {
//				adeSuffix = adeSuffix.substring(0, adeSuffix.indexOf(" "));
//			}
//			nextAde = adeSup;
//		}
//		LOG.debug(ia.getAbbonamento().getCodiceAbbonamento()+" "+adeSuffix+" "+ia.getNote());
//		if (adeSuffix != null) {
//			if (adeSuffix.length() > 1) {
//				Anagrafiche promotore = findAnagraficaByCognome(ses, adeSuffix);
//				if (promotore == null) promotore = createAnagrafica(ses, adeSuffix, rapporto);
//				ia.setPagante(anaTvp);
//				ia.setPromotore(promotore);
//				ia.setAdesione(nextAde);
//				iaDao.update(ses, ia);
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static Anagrafiche findAnagraficaByCognome(Session ses, String cognome) 
//			throws HibernateException {
//		String qs = "from Anagrafiche anag where " +
//				"anag.cognomeRagioneSociale like :s1 and "+
//				"anag.nome like :s2";
//		Query q = ses.createQuery(qs);
//		q.setParameter("s1", cognome);
//		q.setParameter("s2", "");
//		List<Anagrafiche> anagList = q.list();
//		Anagrafiche result = null;
//		if (anagList != null) {
//			if (anagList.size() == 1) {
//				result = anagList.get(0);
//			}
//			if (anagList.size() > 1) {
//				throw new HibernateException("'"+cognome+"' has more than one occurrency in Anagrafiche");
//			} 
//		}
//		return result;
//	}
//	
//	public static Anagrafiche createAnagrafica(Session ses, String cognome, String rapporto)
//			throws HibernateException {
//		Anagrafiche ana = anaDao.createAnagrafiche(ses);
//		ana.getIndirizzoPrincipale().setUtente(admin);
//		ana.getIndirizzoFatturazione().setUtente(admin);
//		ana.setCognomeRagioneSociale(cognome);
//		ana.setNome("");
//		ana.setIdTipoAnagrafica(AppConstants.ANAG_AGENTE);
//		String codCli = new ContatoriDao().generateCodiceCliente(ses);
//		ana.setCodiceCliente(codCli);
//		ana.setUtente(admin);
//		indDao.save(ses, ana.getIndirizzoPrincipale());
//		indDao.save(ses, ana.getIndirizzoFatturazione());
//		anaDao.save(ses, ana);
//		String msg = "Nuovo agente '"+cognome+"': "+codCli;
//		rapporto += msg +"\r\n";
//		LOG.info(msg);
//		return ana;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<IstanzeAbbonamenti> findIstanzeByNoteAdesione(Session ses, String oldAdesione) 
//			throws HibernateException {
//		String hql = "from IstanzeAbbonamenti ia where "+
//			"(ia.abbonamento.codiceAbbonamento like :s01 or ia.abbonamento.codiceAbbonamento like :s02) and "+
//			"ia.note like :s1 and "+
//			"ia.adesione.id is null "+
//			"order by ia.id";
//		Query q = ses.createQuery(hql);
//		q.setParameter("s01", "Q%");
//		q.setParameter("s02", "D%");
//		q.setParameter("s1", "%"+oldAdesione+"%");
//		//q.setParameter("id1", idAdesioneMedie, IntegerType.INSTANCE);
//		//q.setParameter("id2", idAdesioneSup, IntegerType.INSTANCE);
//		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
//		return iaList;
//	}
}
