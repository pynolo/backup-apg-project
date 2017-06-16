/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "periodici")
public class Periodici extends BaseEntity {
	private static final long serialVersionUID = -5077373156430943605L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "uid", nullable = false, length = 4)
    private String uid;
    @Column(name = "nome", length = 64)
    private String nome;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
    @Column(name = "data_fine")
    @Temporal(TemporalType.DATE)
    private Date dataFine;
    @Basic(optional = false)
    @Column(name = "numero_cc", length = 64, nullable = false)
    private String numeroCc;
    @Column(name = "indirizzo", length = 256)
    private String indirizzo;
    @Basic(optional = false)
    @Column(name = "numeri_annuali", nullable = false)
    private Integer numeriAnnuali;
    @Column(name = "iban", length = 32)
    private String iban;
    @Column(name = "tag", length = 256)
    private String tag;
    @Basic(optional = false)
    @Column(name = "id_societa", nullable = false, length = 4)
    private String idSocieta;
    @Basic(optional = false)
    @Column(name = "id_tipo_periodico", nullable = false, length = 4)
    private String idTipoPeriodico;

    public Periodici() {
    }

    public Periodici(Integer id) {
        this.id = id;
    }

    public Periodici(Integer id, Date dataInizio, String numeroCc) {
        this.id = id;
        this.dataInizio = dataInizio;
        this.numeroCc = numeroCc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

    public String getNumeroCc() {
        return numeroCc;
    }

    public void setNumeroCc(String numeroCc) {
        this.numeroCc = numeroCc;
    }
    
    public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public Integer getNumeriAnnuali() {
		return numeriAnnuali;
	}

	public void setNumeriAnnuali(Integer numeriAnnuali) {
		this.numeriAnnuali = numeriAnnuali;
	}

    public String getIdTipoPeriodico() {
		return idTipoPeriodico;
	}

	public void setIdTipoPeriodico(String idTipoPeriodico) {
		this.idTipoPeriodico = idTipoPeriodico;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getIdSocieta() {
		return idSocieta;
	}

	public void setIdSocieta(String idSocieta) {
		this.idSocieta = idSocieta;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Periodici)) {
            return false;
        }
        Periodici other = (Periodici) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Periodici[id=" + id + "] "+uid;
    }

}
