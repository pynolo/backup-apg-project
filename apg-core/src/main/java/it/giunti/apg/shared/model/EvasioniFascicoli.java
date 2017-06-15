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
@Table(name = "evasioni_fascicoli")
public class EvasioniFascicoli extends BaseEntity implements IEvasioni {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -5215988434382233323L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;
    @Column(name = "data_ordine")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOrdine;
    @Column(name = "data_invio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInvio;
    @Column(name = "copie")
    private Integer copie;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "id_tipo_evasione", nullable = false)
    private String idTipoEvasione;
	@Column(name = "id_istanza_abbonamento")
	private Integer idIstanzaAbbonamento;
	@Column(name = "id_abbonamento")
	private Integer idAbbonamento;
	@Column(name = "id_anagrafica", nullable = false)
	private Integer idAnagrafica;
    @Transient
    private String idFascicoliT;
    @JoinColumn(name = "id_fascicolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Fascicoli fascicolo;
    @JoinColumn(name = "id_ordine_logistica", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private OrdiniLogistica ordiniLogistica;
    @Column(name = "data_conferma_evasione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConfermaEvasione;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
    public EvasioniFascicoli() {
    }

    public EvasioniFascicoli(Integer id) {
        this.id = id;
    }

    public EvasioniFascicoli(Integer id, Date dataCreazione) {
        this.id = id;
        this.dataCreazione = dataCreazione;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Date getDataOrdine() {
		return dataOrdine;
	}

	public void setDataOrdine(Date dataOrdine) {
		this.dataOrdine = dataOrdine;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}

	public Integer getCopie() {
		return copie;
	}

	public void setCopie(Integer copie) {
		this.copie = copie;
	}

	public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Date dataModifica) {
        this.dataModifica = dataModifica;
    }

    public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getIdTipoEvasione() {
		return idTipoEvasione;
	}

	public void setIdTipoEvasione(String idTipoEvasione) {
		this.idTipoEvasione = idTipoEvasione;
	}

	public Integer getIdIstanzaAbbonamento() {
		return idIstanzaAbbonamento;
	}

	public void setIdIstanzaAbbonamento(Integer idIstanzaAbbonamento) {
		this.idIstanzaAbbonamento = idIstanzaAbbonamento;
	}
	
	public Integer getIdAbbonamento() {
		return idAbbonamento;
	}

	public void setIdAbbonamento(Integer idAbbonamento) {
		this.idAbbonamento = idAbbonamento;
	}

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public Fascicoli getFascicolo() {
		return fascicolo;
	}

	public void setFascicolo(Fascicoli fascicolo) {
		this.fascicolo = fascicolo;
	}

	public OrdiniLogistica getOrdiniLogistica() {
		return ordiniLogistica;
	}

	public void setOrdiniLogistica(OrdiniLogistica ordiniLogistica) {
		this.ordiniLogistica = ordiniLogistica;
	}

	public Date getDataConfermaEvasione() {
		return dataConfermaEvasione;
	}

	public void setDataConfermaEvasione(Date dataConfermaEvasione) {
		this.dataConfermaEvasione = dataConfermaEvasione;
	}

	//public Date getDataAnnullamento() {
	//	return dataAnnullamento;
	//}
	//
	//public void setDataAnnullamento(Date dataAnnullamento) {
	//	this.dataAnnullamento = dataAnnullamento;
	//}

	public String getIdFascicoliT() {
		return idFascicoliT;
	}

	public void setIdFascicoliT(String idFascicoliT) {
		this.idFascicoliT = idFascicoliT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EvasioniFascicoli)) {
            return false;
        }
        EvasioniFascicoli other = (EvasioniFascicoli) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "EvasioniFascicoli[id=" + id + "] ";
        if (fascicolo != null) {
        	result += fascicolo.getPeriodico().getUid()+" "+fascicolo.getTitoloNumero();
            if (fascicolo.getOpzione() != null) result += " "+fascicolo.getOpzione().getNome();
        }
        return result;
    }

}
