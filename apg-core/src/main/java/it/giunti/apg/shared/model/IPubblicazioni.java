package it.giunti.apg.shared.model;

import java.util.Date;

public interface IPubblicazioni {

    public Integer getId();
    public void setId(Integer id);
    
    public String getTitoloNumero();
	public void setTitoloNumero(String titoloNumero);
	
	public String getCodiceMeccanografico();
	public void setCodiceMeccanografico(String codiceMeccanografico);
	
    public Date getDataInizio();
    public void setDataInizio(Date dataInizio);

    public Date getDataFine();
    public void setDataFine(Date dataFine);
    
	//public String getIdTipoArticolo();
	//public void setIdTipoArticolo(String idTipoArticolo);
	
    public boolean getInAttesa();
	public void setInAttesa(boolean inAttesa);
	
	public String getIdTipoAnagraficaSap();
	public void setIdTipoAnagraficaSap(String idTipoAnagraficaSap);
}
