package it.giunti.apg.updater;


public class UpdateEndIstanze {
	
//private static final Logger LOG = LoggerFactory.getLogger(UpdateFascicoliTotali.class);
//	
//	private static final String SEPARATOR_REGEX = "\\;";
//	private static final int ID_ADESIONE = 149;//Sondaggio 2015
//	
//	private static AbbonamentiDao aDao = new AbbonamentiDao();
//	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
//	
//	public static void parseUpdate(String csvFilePath) 
//			throws BusinessException, IOException {
//		File csvFile = new File(csvFilePath);
//		FileInputStream fstream = new FileInputStream(csvFile);
//		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
//		int count = 0;
//		
//		File outputFile = File.createTempFile("updateFascicoliFine_", ".txt");
//		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
//		LOG.warn("Output file: "+outputFile.getAbsolutePath());
//		
//		try {
//			//Sessione singola istanza
//			Session ses = SessionFactory.getSession();
//			Transaction trn = ses.beginTransaction();
//			try {
//				Keyword adesione = GenericDao.findById(ses, Keyword.class, ID_ADESIONE);
//				//Ciclo su tutte le righe
//				LOG.info("Prolungamento delle istanze nel file "+csvFile.getAbsolutePath());
//				String line = br.readLine();
//				while (line != null) {				
//					String s[] = line.split(SEPARATOR_REGEX);
//					String codice = s[0].trim();
//					String numS = s[1].trim();
//					Integer num = new Integer(numS);
//					String email = null;
//					if (s.length > 2) email = s[2].trim();
//					updateIstanza(ses, count, codice, num, email, adesione, writer);
//					count++;
//					if (count%100 == 0) {
//						ses.flush();
//						LOG.info(count+" righe");
//					}
//					line = br.readLine();
//				}
//				trn.commit();
//			} catch (HibernateException e) {
//				trn.rollback();
//				throw new BusinessException(e.getMessage(), e);
//			} finally {
//				ses.close();
//			}
//		} catch (IOException e) {
//			throw new IOException(e.getMessage(), e);
//		} finally {
//			br.close();
//			fstream.close();
//		}
//		
//		writer.close();
//		
//		LOG.warn("Output file: "+outputFile.getAbsolutePath());
//		LOG.info("Aggiornati "+count+" abbonamenti");
//	}
//	
//	private static void updateIstanza(Session ses, int count,
//			String codAbb, Integer num, String email, Keyword adesione,
//			PrintWriter writer) throws HibernateException {
//		Abbonamenti abb = aDao.findAbbonamentiByCodice(ses, codAbb);
//		Utenti admin = new UtentiDao().findUtenteByUserName(ses, "admin");
//		Date today = new Date();
//		EvasioniFascicoliDao eaDao = new EvasioniFascicoliDao();
//		if (abb != null) {
//			//Elenco in ordine cronologico inverso
//			List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByAbbonamento(ses, abb.getId());
//			if (iaList.size()>0) {
//				//Istanza
//				IstanzeAbbonamenti ia = iaList.get(0);
//				//Listini lsn = ia.getListino();
//				int totali = ia.getFascicoliTotali()+num;
//				ia.setFascicoliTotali(totali);
//				Fascicoli fasInizio = ia.getFascicoloInizio();
//				Fascicoli fasFine = new FascicoliDao()
//						.findFascicoliAfterFascicolo(ses, fasInizio, totali-1);
//				ia.setFascicoloFine(fasFine);
//				ia.setAdesione(adesione);
//				GenericDao.updateGeneric(ses, ia.getId(), ia);
//				List<EvasioniFascicoli> eaList = eaDao.enqueueMissingArretrati(ses, ia, today, admin);
//				//Anagrafica
//				Anagrafiche ana = ia.getAbbonato();
//				if (email != null) {
//					if (email.length() > 1) {
//						if (ana.getEmailPrimaria() != null) {
//							if (ana.getEmailPrimaria().length() > 1) {
//								ana.setEmailSecondaria(ana.getEmailPrimaria());
//							}
//						}
//						ana.setEmailPrimaria(email.toLowerCase());
//						GenericDao.updateGeneric(ses, ana.getId(), ana);
//					}
//				}
//				String log = ia.getAbbonamento().getCodiceAbbonamento()+";"+
//						ana.getCognomeRagioneSociale()+";"+
//						ana.getNome()+";"+
//						ana.getEmailPrimaria()+";"+
//						ia.getFascicoloFine().getNumeroFascicolo()+";"+
//						ia.getFascicoloFine().getDataCop().replaceAll("\\s","\\-")+" "+
//						ServerConstants.FORMAT_YEAR.format(ia.getFascicoloFine().getDataNominale())+";"+
//						eaList.size();
//				LOG.info("["+count+"]"+log);
//				writer.println(log);
//				if (eaList.size() > 0) {
//					LOG.info("*arretrati: "+eaList.size()+" "+ia.getAbbonamento().getCodiceAbbonamento());
//				}
//			} else {
//				throw new HibernateException("Abbonamento "+codAbb+" senza istanze");
//			}
//			return;
//		} else {
//			throw new HibernateException("Abbonamento "+codAbb+" non trovato");
//		}
//	}
}
