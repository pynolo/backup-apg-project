package it.giunti.apg.shared.model;

import java.util.Date;

public interface IEvasioni {

	public Integer getId();
    public void setId(Integer id);

    public Date getDataCreazione();
    public void setDataCreazione(Date dataCreazione);

    public Date getDataOrdine();
	public void setDataOrdine(Date dataOrdine);

	public Date getDataInvio();
	public void setDataInvio(Date dataInvio);
	
	public Integer getCopie();
	public void setCopie(Integer copie);
	
	public Integer getIdIstanzaAbbonamento();
	public void setIdIstanzaAbbonamento(Integer idIstanzaAbbonamento);
	
	public Integer getIdAbbonamento();
	public void setIdAbbonamento(Integer idAbbonamento);

	public Integer getIdAnagrafica();
	public void setIdAnagrafica(Integer idAnagrafica);
	
	public OrdiniLogistica getOrdiniLogistica();
	public void setOrdiniLogistica(OrdiniLogistica ordiniLogistica);
	
    public Date getDataModifica();
    public void setDataModifica(Date dataModifica);
    
    public String getNote();
    public void setNote(String note);
    
    public Date getDataConfermaEvasione();
    public void setDataConfermaEvasione(Date dataModifica);
    
	//public String getIdTipoArticolo();
	//public void setIdTipoArticolo(String idTipoArticolo);
	//
	//public String getIdSocieta();
	//public void setIdSocieta(String idSocieta);
}
