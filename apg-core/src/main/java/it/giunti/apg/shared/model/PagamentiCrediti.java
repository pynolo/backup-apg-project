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

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "pagamenti_crediti")
public class PagamentiCrediti extends BaseEntity {
	private static final long serialVersionUID = -322801846855157621L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
    @Column(name = "id_anagrafica", nullable = false)
    private Integer idAnagrafica;
    @Column(name = "id_istanza_abbonamento")
    private Integer idIstanzaAbbonamento;
    @Basic(optional = false)
    @Column(name = "id_societa", nullable = false, length = 4)
    private String idSocieta;
    @Basic(optional = false)
    @Column(name = "importo", nullable = false, precision = 9, scale = 2)
    private Double importo;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "note", length = 256)
    private String note;
	@Basic(optional = false)
	@Column(name = "stornato_da_origine", nullable = false)
	private boolean stornatoDaOrigine;

    @Basic(optional = false)
    @JoinColumn(name = "id_fattura_origine", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Fatture fatturaOrigine;
    @JoinColumn(name = "id_fattura_impiego", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Fatture fatturaImpiego;
    
    public PagamentiCrediti() {
    }

    public PagamentiCrediti(Integer id) {
        this.id = id;
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

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
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

	
	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public Fatture getFatturaOrigine() {
		return fatturaOrigine;
	}

	public void setFatturaOrigine(Fatture fatturaOrigine) {
		this.fatturaOrigine = fatturaOrigine;
	}

	public Fatture getFatturaImpiego() {
		return fatturaImpiego;
	}

	public void setFatturaImpiego(Fatture fatturaImpiego) {
		this.fatturaImpiego = fatturaImpiego;
	}

	public String getIdSocieta() {
		return idSocieta;
	}

	public void setIdSocieta(String idSocieta) {
		this.idSocieta = idSocieta;
	}

	public Integer getIdIstanzaAbbonamento() {
		return idIstanzaAbbonamento;
	}

	public void setIdIstanzaAbbonamento(Integer idIstanzaAbbonamento) {
		this.idIstanzaAbbonamento = idIstanzaAbbonamento;
	}

	public boolean getStornatoDaOrigine() {
		return stornatoDaOrigine;
	}

	public void setStornatoDaOrigine(boolean stornatoDaOrigine) {
		this.stornatoDaOrigine = stornatoDaOrigine;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PagamentiCrediti)) {
            return false;
        }
        PagamentiCrediti other = (PagamentiCrediti) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PagamentiCrediti[id=" + id + "]";
    }

}
