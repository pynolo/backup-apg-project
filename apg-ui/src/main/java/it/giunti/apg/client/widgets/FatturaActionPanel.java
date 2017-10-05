package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Fatture;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class FatturaActionPanel extends HorizontalPanel {
	
	private final PagamentiServiceAsync pagService = GWT.create(PagamentiService.class);
	
	
	private static Date firstJanuary = getCurrent1stJanuary();
	private static Date firstJune = getCurrent1stJune();
	private static Date today = DateUtil.now();
	private static boolean prevYearBlocked = firstJune.before(today);//quest'anno è passato giugno
	private boolean isOperator = false;
	private boolean isEditor = false;
	private IRefreshable parent = null;
	
	public FatturaActionPanel(boolean isOperator, boolean isEditor, IRefreshable parent) {
		this.isOperator = isOperator;
		this.isEditor = isEditor;
		this.parent = parent;
	}
	
	public FatturaActionPanel(Fatture fattura, boolean isOperator, boolean isEditor, IRefreshable parent) {
		this.isOperator = isOperator;
		this.isEditor = isEditor;
		this.parent = parent;
		draw(fattura);
	}
	
	
	public void draw(Fatture fattura) {
		final AsyncCallback<Fatture> callback = new AsyncCallback<Fatture>() {
			@Override
			public void onFailure(Throwable caught) {
				if (parent !=null) parent.refresh();
			}
			@Override
			public void onSuccess(Fatture result) {
				if (parent !=null) parent.refresh();
			}
		};
		boolean isNotaCred = fattura.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_NOTA_CREDITO);
		boolean archived = false;
		if (firstJanuary.after(fattura.getDataFattura())) {
			//fattura dell'anno precedente
			if (prevYearBlocked) archived = true;
		}
		final Fatture fFattura = fattura;
		if (!archived &&
				fattura.getDataCreazione().getTime() < (today.getTime()-9*AppConstants.HOUR)) {
			if (isOperator) {
				Anchor rigeneraAnchor = new Anchor(ClientConstants.ICON_RIGENERA+"Rigenera ", true);
			
				rigeneraAnchor.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						boolean confirm1 = Window.confirm("Attenzione: la rigenerazione di una fattura e' un'operazione "+
								"sofisticata che implica del lavoro aggiuntivo per l'amministrazione. Si e' sicuri "+
								"di voler continuare? "+
								"N.B. E' possibile rigenerare le fatture dell'anno precedente solo entro fine maggio dell'anno corrente.");
						if (confirm1) {
							String servletUrl = AppConstants.URL_APG_AUTOMATION_REBUILD_FATTURA + 
									"?" + AppConstants.PARAM_NAME + "=" + fFattura.getNumeroFattura();
							Window.open(servletUrl, "", "");
						}
					}
				});
				this.add(rigeneraAnchor);
			}
		}
		if (!isNotaCred) {
			boolean rimborsato = (fattura.getIdNotaCreditoRimborso() != null) ||
					(fattura.getIdNotaCreditoStorno() != null) ||
					(fattura.getIdNotaCreditoRimborsoResto() != null) ||
					(fattura.getIdNotaCreditoStornoResto() != null);
			if (!rimborsato) {
				if (isEditor) {
					//Rimborso
					if (!archived) {
						Anchor rimborsoAnchor = new Anchor(ClientConstants.ICON_FATTURA_RIMBORSO+"Rimborso ", true);
						rimborsoAnchor.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent arg0) {
								boolean confirm1 = Window.confirm("Attenzione: la creazione di una nota di credito e' una operazione "+
										"irreversibile. APG crea il documento ma non effettua l'effettivo rimborso. Al termine non "+
										"risultera' credito residuo.");
								if (confirm1) {
									pagService.createRimborsoTotale(fFattura.getId(), callback);
									//String servletUrl = AppConstants.URL_APG_AUTOMATION_CREATE_RIMBORSO + 
									//		"?" + AppConstants.PARAM_NAME + "=" + fFattura.getNumeroFattura()+
									//		"&action=rimborsoTotale";
									//Window.open(servletUrl, "", "");
								}
							}
						});
						this.add(rimborsoAnchor);
					}
					//Storno totale
					Anchor stornoAnchor = new Anchor(ClientConstants.ICON_FATTURA_RIMBORSO+"Storno ", true);
					stornoAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent arg0) {
							boolean confirm1 = Window.confirm("Attenzione: questa azione crea una nota di credito "+
									"per l'intero importo della fattura. "+
									"L'abbonamento non risultera' piu' pagato. "+
									"L'importo sara' disponibile come credito.");
							if (confirm1) {
								pagService.createStornoTotale(fFattura.getId(), callback);
								//String servletUrl = AppConstants.URL_APG_AUTOMATION_CREATE_RIMBORSO + 
								//		"?" + AppConstants.PARAM_NAME + "=" + fFattura.getNumeroFattura()+
								//		"&action=stornoTotale";
								//Window.open(servletUrl, "", "");
							}
						}
					});
					this.add(stornoAnchor);
					//Rimborso del resto
					if (fattura.getImportoResto() != null && 
							fattura.getIdNotaCreditoRimborsoResto() == null && 
							fattura.getIdNotaCreditoStornoResto() == null) {
						if (fattura.getImportoResto() > 0D) {
							Anchor restoAnchor = new Anchor(ClientConstants.ICON_FATTURA_RIMBORSO+"Rimborso resto ", true);
							restoAnchor.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent arg0) {
									boolean confirm1 = Window.confirm("Attenzione: questa azione crea una nota di credito "+
											"per l'anticipo precedentemente fatturato. "+
											"APG crea il documento ma non effettua l'effettivo rimborso. Al termine non "+
											"risultera' credito residuo.");
									if (confirm1) {
										pagService.createRimborsoResto(fFattura.getId(), callback);
										//String servletUrl = AppConstants.URL_APG_AUTOMATION_CREATE_RIMBORSO + 
										//		"?" + AppConstants.PARAM_NAME + "=" + fFattura.getNumeroFattura()+
										//		"&action=rimborsoResto";
										//Window.open(servletUrl, "", "");
									}
								}
							});
							this.add(restoAnchor);
						}
					}
					//Storno del resto
					if (fattura.getImportoResto() != null && 
							fattura.getIdNotaCreditoRimborsoResto() == null && 
							fattura.getIdNotaCreditoStornoResto() == null) {
						if (fattura.getImportoResto() > 0D) {
							Anchor restoAnchor = new Anchor(ClientConstants.ICON_FATTURA_RIMBORSO+"Storno resto ", true);
							restoAnchor.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent arg0) {
									boolean confirm1 = Window.confirm("Attenzione: questa azione crea una nota di credito "+
											"per l'anticipo precedentemente fatturato. "+
											"L'importo sara' disponibile come credito.");
									if (confirm1) {
										pagService.createStornoResto(fFattura.getId(), callback);
										//String servletUrl = AppConstants.URL_APG_AUTOMATION_CREATE_RIMBORSO + 
										//		"?" + AppConstants.PARAM_NAME + "=" + fFattura.getNumeroFattura()+
										//		"&action=stornoResto";
										//Window.open(servletUrl, "", "");
									}
								}
							});
							this.add(restoAnchor);
						}
					}
				}
			} else {
				//Gia' rimborsato
				if (fattura.getIdNotaCreditoRimborso() != null) {
					this.add(new InlineHTML("Rimborsata: "));
					FatturaStampaLink link = new FatturaStampaLink(fattura.getIdNotaCreditoRimborso(), parent);
					this.add(link);
				}
				if (fattura.getIdNotaCreditoStorno() != null) {
					this.add(new InlineHTML("Stornata: "));
					FatturaStampaLink link = new FatturaStampaLink(fattura.getIdNotaCreditoStorno(), parent);
					this.add(link);
				}
				if (fattura.getIdNotaCreditoRimborsoResto() != null) {
					this.add(new InlineHTML("Resto rimborsato: "));
					FatturaStampaLink link = new FatturaStampaLink(fattura.getIdNotaCreditoRimborsoResto(), parent);
					this.add(link);
				}
				if (fattura.getIdNotaCreditoStornoResto() != null) {
					this.add(new InlineHTML("Resto stornato: "));
					FatturaStampaLink link = new FatturaStampaLink(fattura.getIdNotaCreditoStornoResto(), parent);
					this.add(link);
				}
			}
		} else {
			//E' nota di credito
			this.add(new InlineHTML("<i>Nota di credito</i>"));
			
		}
	}
	
	@SuppressWarnings("deprecation")
	private static Date getCurrent1stJanuary() {
		Date dt = DateUtil.now();
		dt.setMonth(0);//in gwt is not deprecated
		dt.setDate(1);
		return dt;
	}
	
	@SuppressWarnings("deprecation")
	private static Date getCurrent1stJune() {
		Date dt = DateUtil.now();
		dt.setMonth(5);//in gwt is not deprecated
		dt.setDate(1);
		return dt;
	}
}
