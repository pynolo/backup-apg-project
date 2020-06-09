package it.giunti.apg.client.widgets;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.widgets.select.TipiAbbSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.RinnoviMassivi;

public class RinnoviMassiviRowPanel extends HorizontalPanel {
	
	private Date today = DateUtil.now();
	private long startDt = today.getTime() - AppConstants.MONTH * 26;
	//private long finishDt = today.getTime() + AppConstants.MONTH * 36;
	
	private RinnoviMassivi rinnovoMassivo = null;
	private RinnoviMassiviPanel parent = null;
	private Integer idPeriodico = null;
	
	private CheckBox regolaAttivaCheck = null;
	//private PeriodiciSelect periodiciList = null;
	private TipiAbbSelect tipoAbbList = null;
	private DateOnlyBox startDate = null;
	private CheckBox soloRegolariCheck = null;
	private CheckBox soloConPaganteCheck = null;
	private CheckBox soloSenzaPaganteCheck = null;
	private TipiAbbSelect tipoAbbRinnovoList = null;
	
	public RinnoviMassiviRowPanel(RinnoviMassivi rinnovoMassivo, Integer idPeriodicoDefault, RinnoviMassiviPanel parent) {
		super();
		this.parent=parent;
		this.idPeriodico=idPeriodicoDefault;
		if (rinnovoMassivo != null) {
			this.rinnovoMassivo=rinnovoMassivo;
		} else {
			this.rinnovoMassivo=loadDefaultEntity(idPeriodicoDefault);
		}
		idPeriodico = this.rinnovoMassivo.getIdPeriodico();
		draw();
	}
	
	private void draw() {
		//this.setStyleName("box");
		//Attiva
		FlowPanel attivaPanel = new FlowPanel();
		attivaPanel.setStyleName("align-center");
		attivaPanel.add(new InlineHTML("Attiva"));
		regolaAttivaCheck = new CheckBox();
		regolaAttivaCheck.setValue(rinnovoMassivo.getRegolaAttiva());
		attivaPanel.add(regolaAttivaCheck);
		this.add(attivaPanel);
		//Utenti user = AuthSingleton.get().getUtente();
		
		//Periodico
		//periodiciList = new PeriodiciSelect(rinnovoMassivo.getIdPeriodico(),
		//		today, false, false, user);
		//periodiciList.addChangeHandler(new ChangeHandler() {
		//	@Override
		//	public void onChange(ChangeEvent event) {
		//		onPeriodicoChange();
		//	}
		//});
		//this.add(periodiciList);
		
		//TipoAbbonamento iniziale
		tipoAbbList = new TipiAbbSelect(rinnovoMassivo.getIdTipoAbbonamento(),
				rinnovoMassivo.getIdPeriodico(),
				today, false, false);
		this.add(tipoAbbList);
		//Data inizio
		startDate = new DateOnlyBox();
		startDate.setValue(new Date(startDt));
		this.add(startDate);
		//Solo regolari
		FlowPanel pagatiPanel = new FlowPanel();
		pagatiPanel.setStyleName("align-center");
		pagatiPanel.add(new InlineHTML("Pagati"));
		soloRegolariCheck = new CheckBox();
		soloRegolariCheck.setValue(rinnovoMassivo.getSoloRegolari());
		pagatiPanel.add(soloRegolariCheck);
		this.add(pagatiPanel);
		//Solo NON regalo
		FlowPanel nonRegaloPanel = new FlowPanel();
		nonRegaloPanel.setStyleName("align-center");
		nonRegaloPanel.add(new InlineHTML("Personali"));
		soloSenzaPaganteCheck = new CheckBox();
		soloSenzaPaganteCheck.setValue(rinnovoMassivo.getSoloSenzaPagante());
		nonRegaloPanel.add(soloSenzaPaganteCheck);
		this.add(nonRegaloPanel);
		//Solo regalo
		FlowPanel regaloPanel = new FlowPanel();
		regaloPanel.setStyleName("align-center");
		regaloPanel.add(new InlineHTML("Regalati"));
		soloConPaganteCheck = new CheckBox();
		soloConPaganteCheck.setValue(rinnovoMassivo.getSoloConPagante());
		regaloPanel.add(soloConPaganteCheck);
		this.add(regaloPanel);
		//Immagine
		this.add(new InlineHTML(ClientConstants.ICON_ARROW));
		//TipoAbbonamento finale
		tipoAbbRinnovoList = new TipiAbbSelect(rinnovoMassivo.getIdTipoAbbonamentoRinnovo(),
				rinnovoMassivo.getIdPeriodico(),
				today, false, false);
		this.add(tipoAbbRinnovoList);
		
		//Elimina
		InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
		trashImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				confirmAndDelete(rinnovoMassivo.getId());
			}
		});
		this.add(trashImg);
	}
		
	//private void onPeriodicoChange() {
	//	if (periodiciList != null) {
	//		if(periodiciList.getItemCount() > 0) {
	//			int idPeriodico = periodiciList.getSelectedValueInt();
	//			tipoAbbList.reload(-1, idPeriodico, today, false);
	//			fasList.reload(-1, idPeriodico, startDt, finishDt, false, false, true, false, false);
	//			tipoAbbRinnovoList.reload(-1, idPeriodico, today, false);
	//			CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
	//		}
	//	}
	//}
		
	private RinnoviMassivi loadDefaultEntity(Integer idPeriodico) {
		RinnoviMassivi rinnovoMassivo = new RinnoviMassivi();
		rinnovoMassivo.setRegolaAttiva(true);
		if (idPeriodico == null) idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico= UiSingleton.get().getDefaultIdPeriodico(null);
		rinnovoMassivo.setIdPeriodico(idPeriodico);
		rinnovoMassivo.setIdTipoAbbonamento(-1);
		rinnovoMassivo.setDataInizio(DateUtil.now());
		rinnovoMassivo.setSoloRegolari(false);
		rinnovoMassivo.setSoloConPagante(false);
		rinnovoMassivo.setSoloSenzaPagante(false);
		rinnovoMassivo.setIdTipoAbbonamentoRinnovo(-1);
		return rinnovoMassivo;
	}
	
	public RinnoviMassivi getRinnovoMassivo() {
		rinnovoMassivo.setRegolaAttiva(regolaAttivaCheck.getValue());
		rinnovoMassivo.setIdPeriodico(idPeriodico);
		rinnovoMassivo.setIdTipoAbbonamento(tipoAbbList.getSelectedValueInt());
		rinnovoMassivo.setDataInizio(startDate.getValue());
		rinnovoMassivo.setSoloRegolari(soloRegolariCheck.getValue());
		rinnovoMassivo.setSoloConPagante(soloConPaganteCheck.getValue());
		rinnovoMassivo.setSoloSenzaPagante(soloSenzaPaganteCheck.getValue());
		rinnovoMassivo.setIdTipoAbbonamentoRinnovo(tipoAbbRinnovoList.getSelectedValueInt());
		return rinnovoMassivo;
	}
	
	private void confirmAndDelete(Integer id) {
		boolean confirm = Window.confirm("Vuoi veramente cancellare l'impostazione dall'elenco?");
		if (confirm) {
			parent.deleteRow(this);
		}
	}
	
}
