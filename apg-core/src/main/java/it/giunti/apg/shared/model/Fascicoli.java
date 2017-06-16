/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "fascicoli")
public class Fascicoli extends BaseEntity implements IPubblicazioni {
	private static final long serialVersionUID = -985003739772877977L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "titolo_numero", nullable = false, length = 64)
    private String titoloNumero;
    @Basic(optional = false)
    @Column(name = "codice_meccanografico", nullable = false, length = 32)
    private String codiceMeccanografico;
    @Column(name = "data_cop", length = 64)
    private String dataCop;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
    @Basic(optional = false)
    @Column(name = "data_fine", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataFine;
    @Basic(optional = false)
    @Column(name = "data_pubblicazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPubblicazione;
    @Basic(optional = false)
    @Column(name = "fascicoli_accorpati", nullable=false)
    private Integer fascicoliAccorpati;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
	@Basic(optional = false)
	@Column(name = "in_attesa", nullable = false)
	private boolean inAttesa;//etichettaSeparata;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Periodici periodico;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Opzioni opzione;
    @Basic(optional = false)
	@Column(name = "comunicazioni_inviate", nullable = false)
	private boolean comunicazioniInviate;
	//@Basic(optional = false)
	//@Column(name = "id_tipo_articolo", nullable = false, length = 4)
	//private String idTipoArticolo;
	@Basic(optional = false)
	@Column(name = "id_tipo_anagrafica_sap", nullable = false, length = 4)
	private String idTipoAnagraficaSap;
	
	@Transient
    private String idPeriodicoT;
    @Transient
    private String idOpzioneT;
    
	public Fascicoli() {
    }

    public Fascicoli(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitoloNumero() {
		return titoloNumero;
	}

	public void setTitoloNumero(String titoloNumero) {
		this.titoloNumero = titoloNumero;
	}

	public String getCodiceMeccanografico() {
        return codiceMeccanografico;
    }

    public void setCodiceMeccanografico(String codiceMeccanografico) {
        this.codiceMeccanografico = codiceMeccanografico;
    }

    public String getDataCop() {
        return dataCop;
    }

    public void setDataCop(String dataCop) {
        this.dataCop = dataCop;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataNominale) {
        this.dataInizio = dataNominale;
    }

    public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataNominaleFine) {
		this.dataFine = dataNominaleFine;
	}

	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getFascicoliAccorpati() {
		return fascicoliAccorpati;
	}

	public void setFascicoliAccorpati(Integer fascicoliAccorpati) {
		this.fascicoliAccorpati = fascicoliAccorpati;
	}

	public Date getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(Date dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}

	public boolean getInAttesa() {
		return inAttesa;
	}

	public void setInAttesa(boolean inAttesa) {
		this.inAttesa = inAttesa;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
	}
	
	public Opzioni getOpzione() {
		return opzione;
	}

	public void setOpzione(Opzioni opzione) {
		this.opzione = opzione;
	}
    
    public boolean getComunicazioniInviate() {
		return comunicazioniInviate;
	}

	public void setComunicazioniInviate(boolean comunicazioniInviate) {
		this.comunicazioniInviate = comunicazioniInviate;
	}

	public String getIdTipoAnagraficaSap() {
		return idTipoAnagraficaSap;
	}

	public void setIdTipoAnagraficaSap(String idTipoAnagraficaSap) {
		this.idTipoAnagraficaSap = idTipoAnagraficaSap;
	}

	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}
	
	public String getIdOpzioneT() {
		return idOpzioneT;
	}

	public void setIdOpzioneT(String idOpzioneT) {
		this.idOpzioneT = idOpzioneT;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Fascicoli)) {
            return false;
        }
        Fascicoli other = (Fascicoli) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "Fascicoli[id=" + id + "] "+periodico.getUid()+" "+titoloNumero;
        if (opzione != null) result += " "+opzione.getNome();
        return result;
    }

}
