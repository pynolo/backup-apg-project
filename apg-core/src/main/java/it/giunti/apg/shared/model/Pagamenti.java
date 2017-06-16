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
@Table(name = "pagamenti")
public class Pagamenti extends BaseEntity {
	private static final long serialVersionUID = -322801846855157622L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;
    @Basic(optional = false)
    @Column(name = "importo", nullable = false, precision = 9, scale = 2)
    private Double importo;
    @Basic(optional = false)
    @Column(name = "data_accredito", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataAccredito;
    @Basic(optional = false)
    @Column(name = "data_pagamento", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "id_errore")
    private String idErrore;
    @Column(name = "codice_abbonamento_bollettino", length = 16)
    private String codiceAbbonamentoBollettino;
    @Basic(optional = false)
    @Column(name = "codice_abbonamento_match", length = 16, nullable = false)
    private String codiceAbbonamentoMatch;
    @Column(name = "stringa_bollettino", length = 32)
    private String stringaBollettino;
    @Basic(optional = false)
    @Column(name = "id_tipo_pagamento", nullable = false)
    private String idTipoPagamento;
    @Column(name = "id_fattura")
    private Integer idFattura;
    @Basic(optional = false)
    @Column(name = "id_societa", nullable = false, length = 4)
    private String idSocieta;
    @Column(name = "trn", length = 128)
    private String trn;
    
    @JoinColumn(name = "id_istanza_abbonamento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private IstanzeAbbonamenti istanzaAbbonamento;
    @JoinColumn(name = "id_anagrafica", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Anagrafiche anagrafica;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;

    public Pagamenti() {
    }

    public Pagamenti(Integer id) {
        this.id = id;
    }

    public Pagamenti(Integer id, Date dataCreazione, Double importo, Date dataPagamento) {
        this.id = id;
        this.dataCreazione = dataCreazione;
        this.importo = importo;
        this.dataPagamento = dataPagamento;
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

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataAccredito() {
		return dataAccredito;
	}

	public void setDataAccredito(Date dataAccredito) {
		this.dataAccredito = dataAccredito;
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

    public String getIdErrore() {
        return idErrore;
    }

    public void setIdErrore(String idErrore) {
        this.idErrore = idErrore;
    }

    public String getCodiceAbbonamentoBollettino() {
		return codiceAbbonamentoBollettino;
	}

	public void setCodiceAbbonamentoBollettino(String codiceAbbonamentoBollettino) {
		this.codiceAbbonamentoBollettino = codiceAbbonamentoBollettino;
	}

	public String getStringaBollettino() {
        return stringaBollettino;
    }

    public void setStringaBollettino(String stringaBollettino) {
        this.stringaBollettino = stringaBollettino;
    }

	public String getIdTipoPagamento() {
		return idTipoPagamento;
	}

	public void setIdTipoPagamento(String idTipoPagamento) {
		this.idTipoPagamento = idTipoPagamento;
	}

	public Integer getIdFattura() {
		return idFattura;
	}

	public void setIdFattura(Integer idFattura) {
		this.idFattura = idFattura;
	}

	public String getIdSocieta() {
		return idSocieta;
	}

	public void setIdSocieta(String idSocieta) {
		this.idSocieta = idSocieta;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public IstanzeAbbonamenti getIstanzaAbbonamento() {
		return istanzaAbbonamento;
	}

	public void setIstanzaAbbonamento(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}

	public Anagrafiche getAnagrafica() {
		return anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}

	public String getCodiceAbbonamentoMatch() {
		return codiceAbbonamentoMatch;
	}

	public void setCodiceAbbonamentoMatch(String codiceAbbonamentoMatch) {
		this.codiceAbbonamentoMatch = codiceAbbonamentoMatch;
	}
	
	public String getTrn() {
		return trn;
	}

	public void setTrn(String trn) {
		this.trn = trn;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pagamenti)) {
            return false;
        }
        Pagamenti other = (Pagamenti) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Pagamenti[id=" + id + "]";
    }

}
