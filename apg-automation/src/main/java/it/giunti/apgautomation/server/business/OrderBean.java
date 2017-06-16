package it.giunti.apgautomation.server.business;

import it.giunti.apg.server.persistence.OrdiniLogisticaDao;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.OrdiniLogistica;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public class OrderBean {
	
	private Anagrafiche anagrafica = null;
	private OrdiniLogistica ordineLogistica = null;
	private String committente = null;
	private List<OrderRowBean> rowList = new ArrayList<OrderRowBean>();
	
	public OrderBean(Session ses, Anagrafiche anag, String committente,
			Date dataInserimento, String orderPrefix) {
		this(ses, anag, committente,
				new ArrayList<OrderRowBean>(),
				dataInserimento, orderPrefix);
	}
	
	public OrderBean(Session ses, Anagrafiche anag, String committente,
			List<OrderRowBean> evaList,	Date dataInserimento, String orderPrefix) {
		this.anagrafica = anag;
		this.rowList = evaList;
		ordineLogistica = new OrdiniLogisticaDao().createPersistent(ses, anagrafica.getId(), dataInserimento, orderPrefix);
	}

	public List<OrderRowBean> getRowList() {
		return rowList;
	}

	public void setRowList(List<OrderRowBean> rowList) {
		this.rowList = rowList;
	}

	public Anagrafiche getAnagrafica() {
		return anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}

	public OrdiniLogistica getOrdineLogistica() {
		return ordineLogistica;
	}

	public void setOrdineLogistica(OrdiniLogistica ordineLogistica) {
		this.ordineLogistica = ordineLogistica;
	}

	public String getCommittente() {
		return committente;
	}

	public void setCommittente(String committente) {
		this.committente = committente;
	}

}
