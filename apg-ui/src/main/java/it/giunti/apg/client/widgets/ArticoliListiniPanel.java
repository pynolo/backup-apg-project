package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ArticoliListini;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticoliListiniPanel extends TitlePanel {
	
	private List<ArticoliListini> articoliListiniList = new ArrayList<ArticoliListini>();
	
	public ArticoliListiniPanel(Set<ArticoliListini> articoliSet, String title) {
		super(title);
		if (articoliSet != null) {
			if (articoliSet.size() > 0) articoliListiniList.addAll(articoliSet);
		}
		drawArticoli();
	}
	
	public ArticoliListiniPanel(Integer idListino, String title) {
		super(title);
		loadArticoli(idListino);
	}
	
	public void changeListino(Set<ArticoliListini> articoliSet) {
		articoliListiniList.clear();
		articoliListiniList.addAll(articoliSet);
		drawArticoli();
	}
	public void changeListino(Integer idListino) {
		loadArticoli(idListino);
	}
	
	private void drawArticoli() {
		this.clear();
		boolean isEmpty = true;
		if (articoliListiniList != null) {
			if (articoliListiniList.size()>0) {
				VerticalPanel itemsPanel = new VerticalPanel();
				this.add(itemsPanel);
				for (ArticoliListini artLst:articoliListiniList) {
					//Disegna
					String labelHtml = "<b>"+artLst.getArticolo().getCodiceMeccanografico()+"</b> "+
							artLst.getArticolo().getTitoloNumero()+
							" <i>(per il "+AppConstants.DEST_DESC.get(artLst.getIdTipoDestinatario())+")</i> ";
					if ((artLst.getGiornoLimitePagamento() != null) &&
							(artLst.getMeseLimitePagamento() != null)) {
						labelHtml += " limite pagamento <b>"+artLst.getGiornoLimitePagamento()+" "+
								ClientConstants.MESI[artLst.getMeseLimitePagamento()]+"</b>";
					}
					itemsPanel.add(new InlineHTML(labelHtml));
				}
				if (articoliListiniList.size()>0) {
					isEmpty=false;
				}
			}
		}
		this.setVisible(!isEmpty);
	}
	
	private void loadArticoli(Integer idListino) {
		ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
		AsyncCallback<List<ArticoliListini>> callback = new AsyncCallback<List<ArticoliListini>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<ArticoliListini> result) {
				articoliListiniList=result;
				drawArticoli();
				WaitSingleton.get().stop();
			}
		};
		try {
			WaitSingleton.get().start();
			articoliService.findArticoliListini(idListino, callback);
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
}
