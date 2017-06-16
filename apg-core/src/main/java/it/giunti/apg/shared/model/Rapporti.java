/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "rapporti")
public class Rapporti extends BaseEntity {
	private static final long serialVersionUID = 9074149319777303438L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "titolo", nullable = false, length = 256)
    private String titolo;
    @Lob
    @Basic(optional = false)
    @Column(name = "testo", nullable = false)
    private String testo;
    @Basic(optional = false)
	@Column(name = "terminato", nullable = false)
    private boolean terminato;
    @Basic(optional = false)
	@Column(name = "errore", nullable = false)
    private boolean errore;
    @Basic(optional = false)
	@Column(name = "data_modifica", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
    
    public Rapporti() {
    }

    public Rapporti(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public boolean getTerminato() {
		return terminato;
	}

	public void setTerminato(boolean terminato) {
		this.terminato = terminato;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public boolean getErrore() {
		return errore;
	}

	public void setErrore(boolean errore) {
		this.errore = errore;
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

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rapporti)) {
            return false;
        }
        Rapporti other = (Rapporti) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rapporti[id=" + id + "]";
    }

}
