package it.giunti.apg.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiListini;

public class MaterialiListiniPanel extends TitlePanel {
	
	private List<MaterialiListini> articoliListiniList = new ArrayList<MaterialiListini>();
	
	public MaterialiListiniPanel(Set<MaterialiListini> articoliSet, String title) {
		super(title);
		if (articoliSet != null) {
			if (articoliSet.size() > 0) articoliListiniList.addAll(articoliSet);
		}
		drawArticoli();
	}
	
	public MaterialiListiniPanel(Integer idListino, String title) {
		super(title);
		loadArticoli(idListino);
	}
	
	public void changeListino(Set<MaterialiListini> articoliSet) {
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
				for (MaterialiListini artLst:articoliListiniList) {
					//Disegna
					String labelHtml = "<b>"+artLst.getMateriale().getCodiceMeccanografico()+"</b> "+
							artLst.getMateriale().getTitolo()+
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
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<List<MaterialiListini>> callback = new AsyncCallback<List<MaterialiListini>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<MaterialiListini> result) {
				articoliListiniList=result;
				drawArticoli();
				WaitSingleton.get().stop();
			}
		};
		try {
			WaitSingleton.get().start();
			matService.findMaterialiListini(idListino, callback);
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
}
