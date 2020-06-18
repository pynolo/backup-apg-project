package it.giunti.apg.client;

import com.google.gwt.user.client.ui.SimplePanel;

import it.giunti.apg.client.frames.AbbonamentiModifiedFindFrame;
import it.giunti.apg.client.frames.AbbonamentiQuickSearchFrame;
import it.giunti.apg.client.frames.AbbonamentoFrame;
import it.giunti.apg.client.frames.AdesioniFindFrame;
import it.giunti.apg.client.frames.AnagraficaAbbonamentoQuickFrame;
import it.giunti.apg.client.frames.AnagraficaFrame;
import it.giunti.apg.client.frames.AnagraficheFindFrame;
import it.giunti.apg.client.frames.AnagraficheMergeFindFrame;
import it.giunti.apg.client.frames.AnagraficheMergeFrame;
import it.giunti.apg.client.frames.AnagraficheModifiedFindFrame;
import it.giunti.apg.client.frames.AnagraficheQuickSearchFrame;
import it.giunti.apg.client.frames.AvvisiFindFrame;
import it.giunti.apg.client.frames.AvvisiListFrame;
import it.giunti.apg.client.frames.ComunicazioneFrame;
import it.giunti.apg.client.frames.ComunicazioniFindFrame;
import it.giunti.apg.client.frames.DeliveryFileManagementFrame;
import it.giunti.apg.client.frames.ErrorFrame;
import it.giunti.apg.client.frames.FattureInvioFindFrame;
import it.giunti.apg.client.frames.InputPagamentiFrame;
import it.giunti.apg.client.frames.InstallFrame;
import it.giunti.apg.client.frames.JobFrame;
import it.giunti.apg.client.frames.MaterialiFindFrame;
import it.giunti.apg.client.frames.MaterialiProgrammazioneFindFrame;
import it.giunti.apg.client.frames.ModelliBollettiniFindFrame;
import it.giunti.apg.client.frames.ModelliBollettiniFrame;
import it.giunti.apg.client.frames.ModelliEmailFindFrame;
import it.giunti.apg.client.frames.ModelliEmailFrame;
import it.giunti.apg.client.frames.OpzioneFrame;
import it.giunti.apg.client.frames.OpzioniFindFrame;
import it.giunti.apg.client.frames.OrdiniLogisticaFindFrame;
import it.giunti.apg.client.frames.OutputMaterialiListiniFrame;
import it.giunti.apg.client.frames.OutputMaterialiOpzioniFrame;
import it.giunti.apg.client.frames.OutputEnqueuedEmailFrame;
import it.giunti.apg.client.frames.OutputMaterialiProgrammazioneFrame;
import it.giunti.apg.client.frames.PagamentiCorrezioniFrame;
import it.giunti.apg.client.frames.PagamentiCreditiFindFrame;
import it.giunti.apg.client.frames.PeriodiciFindFrame;
import it.giunti.apg.client.frames.QueryIstanzeFrame;
import it.giunti.apg.client.frames.RapportiFindFrame;
import it.giunti.apg.client.frames.RapportoFrame;
import it.giunti.apg.client.frames.RinnoviMassiviFrame;
import it.giunti.apg.client.frames.TipiAbbonamentoFindFrame;
import it.giunti.apg.client.frames.TipoAbbonamentoFrame;
import it.giunti.apg.client.frames.UtentiFindFrame;

public class UriManager {
	
	public static final String SEPARATOR_TOKEN = "!";// Â£
	public static final String SEPARATOR_PARAMS = "/"; // /
	public static final String SEPARATOR_VALUES = "=";
	
	public static final String INDEX = "index";
	public static final String ERRORE = "error";
	public static final String QUICK_SEARCH_ABBONAMENTI = "quickSearchAbbo";
	public static final String QUICK_SEARCH_ANAGRAFICHE = "quickSearchAnag";
	public static final String QUICK_DATA_ENTRY = "quickdataentry";
	public static final String ANAGRAFICA = "anagrafica";
	public static final String ANAGRAFICHE_FIND = "anagraficaFind";
	public static final String ANAGRAFICHE_MODIFIED_FIND = "anagModFind";
	public static final String ANAGRAFICHE_MERGE_FIND = "anagraficheMergeFind";
	public static final String ANAGRAFICHE_MERGE = "anagraficheMerge";
	//public static final String FEEDBACK_ANAGRAFICHE = "feedbackAnagraficheFind";
	//public static final String FEEDBACK_ANAGRAFICA = "feedbackAnagrafica";
	public static final String ABBONAMENTO = "abbonamento";
	public static final String ABBONAMENTI_MODIFIED_FIND = "abboModFind";
	public static final String PAGAMENTI_CREDITI_FIND = "pagamentiCreditiFind";
	public static final String PAGAMENTI_CORREZIONE = "pagamentiCorrezione";
	public static final String TIPI_ABBONAMENTO_FIND = "tipiAbbonamentoFind";
	public static final String LISTINO = "listino";
	public static final String INPUT_PAGAMENTI = "inputPagamenti";
	public static final String OUTPUT_FASCICOLI = "outputFascicoli";
	public static final String OUTPUT_ARTICOLI_LISTINI = "outputArtLsn";
	public static final String OUTPUT_ARTICOLI_OPZIONI = "outputArtOpz";
	public static final String OUTPUT_COMUNICAZIONI = "outputComunic";
	public static final String QUERY_ISTANZE = "queryIstanze";
	public static final String COMUNICAZIONI_FIND = "comunicFind";
	public static final String COMUNICAZIONE = "comunic";
	public static final String MODELLI_BOLLETTINI_FIND = "modBolFind";
	public static final String MODELLI_BOLLETTINI = "modBol";
	public static final String MODELLI_EMAIL_FIND = "modEmailFind";
	public static final String MODELLI_EMAIL = "modEmail";
	public static final String PERIODICI_FIND = "periodiciFind";
	public static final String MATERIALI_FIND = "matFind";
	public static final String MATERIALI_PROGRAMMAZIONE_FIND = "matProgFind";
	public static final String FATTURE_INVIO_FIND = "fattureInvioFind";
	public static final String ADESIONI_FIND = "adesioniFind";
	public static final String AVVISI_FIND = "avvisiFind";
	public static final String AVVISI_LIST = "avvisiList";
	public static final String OPZIONI_FIND = "opzioniFind";
	public static final String OPZIONE = "opzione";
	public static final String UTENTI_FIND = "utentiFind";
	public static final String RAPPORTI_FIND = "rapportiFind";
	public static final String RAPPORTO = "rapporto";
	public static final String JOB_FIND = "jobFind";
	public static final String INSTALL_FIND = "installFind";
	public static final String RINNOVI_MASSIVI = "rinnoviMassivi";
	public static final String ORDINI_FIND = "ordiniFind";
	public static final String DELIVERY_FILE_MANAGEMENT = "deliveryManagement";
	
	//Reloads the current page from the server reload(true) and not from cache reload(false)
	public static native void hardReload() /*-{
	  $wnd.location.reload(true);
	}-*/;
	
	public static void loadContent(String fullToken) {
		if (fullToken != null) {
			String token = tokenFromUri(fullToken);
			UriParameters params = paramsFromUri(fullToken);
			SimplePanel contentPanel = UiSingleton.get().getContentPanel();
			contentPanel.clear();
			if (INDEX.equals(token)) {
				contentPanel.add(new AvvisiListFrame(params));
			}
			if (ERRORE.equals(token)) {
				contentPanel.add(new ErrorFrame(params));
			}
			if (QUICK_SEARCH_ABBONAMENTI.equals(token)) {
				contentPanel.add(new AbbonamentiQuickSearchFrame(params));
			}
			if (QUICK_SEARCH_ANAGRAFICHE.equals(token)) {
				contentPanel.add(new AnagraficheQuickSearchFrame(params));
			}
			if (QUICK_DATA_ENTRY.equals(token)) {
				contentPanel.add(new AnagraficaAbbonamentoQuickFrame());
			}
			if (ANAGRAFICA.equals(token)) {
				contentPanel.add(new AnagraficaFrame(params));
			}
			if (ANAGRAFICHE_FIND.equals(token)) {
				contentPanel.add(new AnagraficheFindFrame(params));
			}
			if (ANAGRAFICHE_MODIFIED_FIND.equals(token)) {
				contentPanel.add(new AnagraficheModifiedFindFrame(params));
			}
			if (ANAGRAFICHE_MERGE_FIND.equals(token)) {
				contentPanel.add(new AnagraficheMergeFindFrame(params));
			}
			if (ANAGRAFICHE_MERGE.equals(token)) {
				contentPanel.add(new AnagraficheMergeFrame(params));
			}
//			if (FEEDBACK_ANAGRAFICHE.equals(token)) {
//				contentPanel.add(new FeedbackAnagraficheFindFrame(params));
//			}
//			if (FEEDBACK_ANAGRAFICA.equals(token)) {
//				contentPanel.add(new FeedbackAnagraficaFrame(params));
//			}
			if (ABBONAMENTO.equals(token)) {
				contentPanel.add(new AbbonamentoFrame(params));
			}
			if (ABBONAMENTI_MODIFIED_FIND.equals(token)) {
				contentPanel.add(new AbbonamentiModifiedFindFrame(params));
			}
			if (PAGAMENTI_CREDITI_FIND.equals(token)) {
				contentPanel.add(new PagamentiCreditiFindFrame(params));
			}
			if (PAGAMENTI_CORREZIONE.equals(token)) {
				contentPanel.add(new PagamentiCorrezioniFrame(params));
			}
			if (TIPI_ABBONAMENTO_FIND.equals(token)) {
				contentPanel.add(new TipiAbbonamentoFindFrame(params));
			}
			if (LISTINO.equals(token)) {
				contentPanel.add(new TipoAbbonamentoFrame(params));
			}
			if (INPUT_PAGAMENTI.equals(token)) {
				contentPanel.add(new InputPagamentiFrame(params));
			}
			if (OUTPUT_FASCICOLI.equals(token)) {
				contentPanel.add(new OutputMaterialiProgrammazioneFrame(params));
			}
			if (OUTPUT_ARTICOLI_LISTINI.equals(token)) {
				contentPanel.add(new OutputMaterialiListiniFrame(params));
			}
			if (OUTPUT_ARTICOLI_OPZIONI.equals(token)) {
				contentPanel.add(new OutputMaterialiOpzioniFrame(params));
			}
			if (OUTPUT_COMUNICAZIONI.equals(token)) {
				contentPanel.add(new OutputEnqueuedEmailFrame(params));
			}
			if (QUERY_ISTANZE.equals(token)) {
				contentPanel.add(new QueryIstanzeFrame(params));
			}
			if (COMUNICAZIONI_FIND.equals(token)) {
				contentPanel.add(new ComunicazioniFindFrame(params));
			}
			if (COMUNICAZIONE.equals(token)) {
				contentPanel.add(new ComunicazioneFrame(params));
			}
			if (MODELLI_BOLLETTINI_FIND.equals(token)) {
				contentPanel.add(new ModelliBollettiniFindFrame(params));
			}
			if (MODELLI_BOLLETTINI.equals(token)) {
				contentPanel.add(new ModelliBollettiniFrame(params));
			}
			if (MODELLI_EMAIL_FIND.equals(token)) {
				contentPanel.add(new ModelliEmailFindFrame(params));
			}
			if (MODELLI_EMAIL.equals(token)) {
				contentPanel.add(new ModelliEmailFrame(params));
			}
			if (MATERIALI_FIND.equals(token)) {
				contentPanel.add(new MaterialiFindFrame(params));
			}
			if (MATERIALI_PROGRAMMAZIONE_FIND.equals(token)) {
				contentPanel.add(new MaterialiProgrammazioneFindFrame(params));
			}
			if (FATTURE_INVIO_FIND.equals(token)) {
				contentPanel.add(new FattureInvioFindFrame(params));
			}
			if (ADESIONI_FIND.equals(token)) {
				contentPanel.add(new AdesioniFindFrame(params));
			}
			if (PERIODICI_FIND.equals(token)) {
				contentPanel.add(new PeriodiciFindFrame(params));
			}
			if (OPZIONI_FIND.equals(token)) {
				contentPanel.add(new OpzioniFindFrame(params));
			}
			if (OPZIONE.equals(token)) {
				contentPanel.add(new OpzioneFrame(params));
			}
			if (AVVISI_FIND.equals(token)) {
				contentPanel.add(new AvvisiFindFrame(params));
			}
			if (AVVISI_LIST.equals(token)) {
				contentPanel.add(new AvvisiListFrame(params));
			}
			if (UTENTI_FIND.equals(token)) {
				contentPanel.add(new UtentiFindFrame(params));
			}
			if (RAPPORTI_FIND.equals(token)) {
				contentPanel.add(new RapportiFindFrame(params));
			}
			if (RAPPORTO.equals(token)) {
				contentPanel.add(new RapportoFrame(params));
			}
			if (JOB_FIND.equals(token)) {
				contentPanel.add(new JobFrame(params));
			}
			if (INSTALL_FIND.equals(token)) {
				contentPanel.add(new InstallFrame(params));
			}
			if (RINNOVI_MASSIVI.equals(token)) {
				contentPanel.add(new RinnoviMassiviFrame(params));
			}
			if (ORDINI_FIND.equals(token)) {
				contentPanel.add(new OrdiniLogisticaFindFrame(params));
			}
			if (DELIVERY_FILE_MANAGEMENT.equals(token)) {
				contentPanel.add(new DeliveryFileManagementFrame(params));
			}
		}
	}
	
	private static String tokenFromUri(String fullToken) {
		String result = null;
		String[] pieces = fullToken.split(SEPARATOR_TOKEN);
		if (pieces.length >= 1) {
			result = pieces[0];
		}
		return result;
	}
	
	private static UriParameters paramsFromUri(String fullToken) {
		//Extract only the part after SEPARATOR_TOKEN
		String parameters = null;
		String[] tokenPieces = fullToken.split(SEPARATOR_TOKEN);
		if (tokenPieces.length >= 2) {
			parameters = tokenPieces[1];
		} else {
			return null;
		}
		
		//Extract parameters and put them in a map
		UriParameters result = new UriParameters();
		String[] pieces = parameters.split(SEPARATOR_PARAMS);
		if (pieces.length >= 1) {
			for (String piece : pieces) {
				String[] couple = piece.split(SEPARATOR_VALUES);
				if (couple.length >= 2) {
					result.add(couple[0], couple[1]);
				}
			}
		}
		return result;
	}
}
