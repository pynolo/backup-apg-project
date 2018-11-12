package it.giunti.apg.updater;

import it.giunti.apg.updater.archive.CleanupWrongCodFisc;

public class Main {
	//private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//UpdateAnagraficaCodFisc.updateAnagraficaFormCsv(args[0]);
			CleanupWrongCodFisc.run();
			
			//CinVerification.update();
			//EncryptGdprPasswords.execute();
			//InsertAnagraficaAndArticolo.parseFileAnagrafiche(args[0], args[1]);// args: "file" "lettera"
			//UpdateAnagraficaCreazione.update();
			//CreateCacheCrm.update();
			//UpdateAnagraficaType.update();
			//MixedCaseConversion.updateAnagraficheCase();
			//MoveIstanzaEnd.moveD();
			//InsertAnagraficaAndArticolo.parseFileAnagrafiche(args[0], args[1]);// args: "file" "lettera"
			//UpdateAnagraficaProfessioni.updateAnagraficaFormCsv(args[0]);
			//UpdateEndIstanze.parseUpdate(args[0]);
			//UpdateAnagraficaSearchString.updateAnagraficaCodice();
			//ReplaceAdesioniScuola.replaceAdesioni();
			//not needed slf4j: new Main().initLogger();
			//MigrateToNewFatture.migrate();
			//ChangeNazione.change();
			//UpdateAnagraficaCodFisc.updateAnagraficaFormCsv(args[0]);
			//MoveAdesioniToTable.importFile(args[0]);
			//FattureRapportoDaListaPending.extract(args);
			//FattureEstrazioneDaListaArchidoc.extract(args);
			//ForceInvio.force(args);
			//AttachMissingOpzioniObbligatorie.attachMissingOpzioniObbligatorie(args[0]);
			//RemoveArticoliDuplicati.update();
			//UpdateFascicoliTotali.parseUpdate("/home/paolo/B_245_id179.csv", false, true);
			//UpdateAnagraficaEmail.updateAnagraficaFormCsv("/home/paolo/SI_email.csv");
			//UpdateAnagraficaEmail.updateAnagraficaFormCsv("/home/paolo/VS_email.csv");
			//UpdateAddArticolo.parseFileAddArticolo("/home/paolo/C4087C.txt", "C4087C");
			//UpdateAddArticolo.parseFileAddArticolo("/home/paolo/C4088C.txt", "C4088C");
			//UpdateAddArticolo.parseFileAddArticolo("/home/paolo/57186F.txt", "57186F");
			//UpdateAddArticolo.parseFileAddArticolo("/home/paolo/60469W.txt", "60469W");
			//UpdateStartIstanze.update();
			//AnagraficheMerger.matchDoniReceiver();
			//InsertAbbonamentoByAnagrafica.addAbbonamenti(args[0]);
			//AnagraficheMerger.mergeAnagrafiche("mergeAnagrafiche", false);
			//FindNewGiuntiCard.findNew(1, true, "estrazioneGCard_A_"); //Vita scolastica
			//FindNewGiuntiCard.findNew(7, true, "estrazioneGCard_M_"); //Scuola dell'infanzia
			//CountEvasioniFascicoli.countEvasioniFascicoli(1, "ricalcoloFascicoli_A_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(2, "ricalcoloFascicoli_B_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(4, "ricalcoloFascicoli_D_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(7, "ricalcoloFascicoli_M_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(8, "ricalcoloFascicoli_Q_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(9, "ricalcoloFascicoli_W_");
			//CountEvasioniFascicoli.countEvasioniFascicoli(10, "ricalcoloFascicoli_S_");	
			//UpdateAnagraficaCodice.updateAnagraficaCodice();
			//UpdateAnagraficaSesso.updateAnagraficaFormDollarCsv(args[0]);
			//UpdateAnagraficaEmail.updateAnagraficaFormDollarCsv(args[0]);
			//AttachMissingFascicoli.attachMissingFascicoli(true, "A");
			//ReattachOldGracing.reattachOldGracing();
			//UpdateUltimaDellaSerie.updateAbbonamenti();
			//SupplementiTricks.addSupplementoFromPrevInstance("A", SupplementiTricks.ID_VS_SETT2012,
			//		SupplementiTricks.ID_VS_AREAWEB2012, SupplementiTricks.ID_VS_AREAWEB2013);
			//SupplementiTricks.addSupplementoFromPrevInstance("M", SupplementiTricks.ID_SI_SETT2012,
			//		SupplementiTricks.ID_SI_AREAWEB2012, SupplementiTricks.ID_SI_AREAWEB2013);
			//SupplementiTricks.removeSupplementoFromPrevInstance("A",
			//		SupplementiTricks.ID_VS_AREAWEB2012, SupplementiTricks.ID_VS_AREAWEB2013);
			//SupplementiTricks.removeSupplementoFromPrevInstance("M",
			//		SupplementiTricks.ID_SI_AREAWEB2012, SupplementiTricks.ID_SI_AREAWEB2013);
			//MovePromotoreToPagante.exec();
			//UpdateSplitEmail.updateAbbonamenti();
		} catch (Exception e) {
			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

//	private void initLogger(){
//		try {
//			InputStream confIs = this.getClass().getResourceAsStream(ServerConstants.LOGGER_CONFIG_FILE);
////			try {
////				confIs = new FileInputStream("."+ServerConstants.LOGGER_CONFIG_FILE);
////			} catch (FileNotFoundException e) {
////				e.printStackTrace();
////			}
//			if(confIs!=null){
//				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(confIs);
//				DOMConfigurator.configure(doc.getDocumentElement());
//				//Instanzio il logger
//				Logger LOG = LoggerFactory.getLogger(StartLoggerServlet.class);
//				LOG.info("Logger instantiated from ."+ServerConstants.LOGGER_CONFIG_FILE);
//			} else {
//				throw new RuntimeErrorException(null, ServerConstants.LOGGER_CONFIG_FILE+" NOT FOUND");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
