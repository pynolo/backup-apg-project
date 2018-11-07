package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AnagraficheSearchBox extends FormPanel {

	private static final String CERCA = "cerca con *";
	private static final String EMPTY_LABEL = "[nessuno]";
	private static final int DUMMY_ID = -1;
	
	private FlowPanel holder;
	private String captionHtml;
	private boolean editable;
	private boolean editStatus;
	private Anagrafiche value; //non è mai null, ma eventualmente contiene una dummyAnagrafica
	private ListBox anagList = null;
	private TextBox anagBox = null;
	
	public AnagraficheSearchBox(String captionHtml, Anagrafiche value, boolean editable) {
		this(captionHtml, value, editable, false);
	}
	
	public AnagraficheSearchBox(String captionHtml, Anagrafiche value,
			boolean editable, boolean editStatus) {
		this.captionHtml = captionHtml;
		this.setStyleName("inline");
		if (value != null) {
			this.value=value;
		} else {
			this.value=createDummyAnagrafica();
		}
		this.editable=editable;
		this.editStatus=editStatus;
		drawPanel();
	}
	
	/**
	 * Disegna il link all'anagrafica se non siamo in editStatus, ma se
	 * siamo in editStatus visualizza il mini motore di ricerca
	 */
	private void drawPanel() {
		this.clear();
		final FormPanel fThis = this;
		holder = new FlowPanel();
		holder.setStyleName("inline");
		holder.add(new InlineHTML("<b>"+captionHtml+"</b>&nbsp;&nbsp;"));
		if (editStatus) {
			//piccolo motore di ricerca anagrafiche
			anagList = new ListBox();
			//anagList.setVisibleItemCount(1);
			refreshAnagList();
			holder.add(anagList);
			anagBox = new TextBox();
			anagBox.setWidth("7em");
			inactivateBox(anagBox);
			anagBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent arg0) {
					activateBox(anagBox);
				}
			});
			anagBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent arg0) {
					if (anagBox.getValue().equals("")) {
						inactivateBox(anagBox);
					}
				}
			});
			holder.add(anagBox);
			Button cercaButton = new Button(ClientConstants.ICON_MAGNIFIER+"&nbsp;Cerca");
			cercaButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					fThis.submit();
				}
			});
			holder.add(cercaButton);
			this.addSubmitHandler(new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					refreshAnagList();
				}
			});
		} else {
			//not editStatus
			if (value.getId().intValue()==DUMMY_ID) {
				if (editable) {
					holder.add(new InlineHTML(buildDescription(value, true)));
				} else {
					//se è vuoto e non editabile allora viene nascosto
					this.setVisible(false);
					this.removeFromParent();
				}
			} else {
				String linkText = buildDescription(value, true);
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, value.getId());
				Hyperlink nameLink = params.getHyperlink(
						linkText, UriManager.ANAGRAFICHE_MERGE);
				holder.add(nameLink);
			}
			if (editable) {
				holder.add(new InlineHTML("&nbsp;&nbsp;"));
				InlineHTML editImg = new InlineHTML(ClientConstants.ICON_EDIT);
				editImg.setTitle("Modifica");
				editImg.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						editStatus=true;
						drawPanel();
					}
				});
				holder.add(editImg);
			}
		}
		this.add(holder);
	}
	
	private void inactivateBox(TextBox box) {
		box.setStyleName("inactive-box");
		box.setValue(CERCA);
	}
	
	private void activateBox(TextBox box) {
		box.setStyleName("active-box");
		if (CERCA.equals(box.getValue())) {
			box.setValue("");
		}
	}
	
	private void refreshAnagList() {
		String searchString = "";
		if (anagBox != null) {
			searchString = anagBox.getValue();
		}
		if (!searchString.equals("") && !searchString.equals(CERCA)) {
			loadAnagraficaByCognomeNome(searchString);
		} else {
			List<Anagrafiche> list = new ArrayList<Anagrafiche>();
			list.add(value);
			drawAnagraficheList(list, value);
		}
	}

	/**
	 * Entrambi i parametri devono essere già pronti
	 * @param anagrafiche
	 * @param selectedId
	 */
	private void drawAnagraficheList(List<Anagrafiche> anagrafiche, Anagrafiche selectedValue) {
		anagList.clear();
		//elemento vuoto
		anagList.addItem(EMPTY_LABEL, DUMMY_ID+"");//gestisce anche anagrafiche null
		for (int i=0; i<anagrafiche.size(); i++) {
			if (anagrafiche.get(i).getId().intValue() != DUMMY_ID) { 
				anagList.addItem(buildDescription(anagrafiche.get(i), false),
						anagrafiche.get(i).getId().toString());
				if (anagrafiche.get(i).getId().equals(selectedValue.getId())) {
					anagList.setSelectedIndex(i+1);
				}
			}
		}
	}
	
	private String buildDescription(Anagrafiche anag, boolean asHtml) {
		String result = "";
		if (asHtml) result += "<b>";
		result += buildNomeFromAnagrafica(anag)+" ";
		if (asHtml) result += "</b>";
		if (anag.getUid() != null) result += " ["+anag.getUid()+"] ";
		result += buildIndirizzoFromAnagrafica(anag);
		return result;
	}
	
	private String buildNomeFromAnagrafica(Anagrafiche anag) {
		if (anag == null) return EMPTY_LABEL;
		if (anag.getId().intValue()==DUMMY_ID) return EMPTY_LABEL;
		String result = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (anag.getIndirizzoPrincipale().getNome() != null) {
			result += " "+anag.getIndirizzoPrincipale().getNome();
		}
		return result;
	}
	
	private String buildIndirizzoFromAnagrafica(Anagrafiche anag) {
		String result = "";
		if (anag.getIndirizzoPrincipale().getIndirizzo() != null) {
			if (anag.getIndirizzoPrincipale().getIndirizzo().length() > 0) {
				result += anag.getIndirizzoPrincipale().getIndirizzo();
			}
		}
		if (anag.getIndirizzoPrincipale().getLocalita() != null) {
			if (anag.getIndirizzoPrincipale().getLocalita().length() > 0) {
				result += ", "+anag.getIndirizzoPrincipale().getLocalita();
			}
		}
		if (anag.getIndirizzoPrincipale().getProvincia() != null) {
			if (anag.getIndirizzoPrincipale().getProvincia().length() > 0) {
				result += "("+anag.getIndirizzoPrincipale().getProvincia()+")";
			}
		}
		return result;
	}
	
	public String getIdValue() {
		String result;
		if (editStatus) {
			result = anagList.getValue(anagList.getSelectedIndex());
		} else {
			result = value.getId().toString();
		}
		if (result.equals(DUMMY_ID+"")) result = null;
		return result;
	}
	
	private Anagrafiche createDummyAnagrafica() {
		Anagrafiche dummy = new Anagrafiche();
		dummy.setDataCreazione(DateUtil.now());
		Indirizzi ind = new Indirizzi();
		ind.setCognomeRagioneSociale(EMPTY_LABEL);
		dummy.setId(DUMMY_ID);
		dummy.setIndirizzoPrincipale(ind);
		ind.setCap("");
		ind.setProvincia(null);
		ind.setIndirizzo("");
		ind.setLocalita("");
		return dummy;
	}
	
	private void loadAnagraficaByCognomeNome(String searchString) {
		AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
		AsyncCallback<List<Anagrafiche>> callback = new AsyncCallback<List<Anagrafiche>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Anagrafiche> result) {
				drawAnagraficheList(result, value);
				WaitSingleton.get().stop();
			}
		};
		try {
			WaitSingleton.get().start();
			anagraficheService.quickSearchAnagrafiche(searchString, 0, 50, callback);
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
}
