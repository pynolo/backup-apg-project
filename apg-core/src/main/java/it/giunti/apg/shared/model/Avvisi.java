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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "avvisi")
public class Avvisi extends BaseEntity {
	private static final long serialVersionUID = 1254149319777303338L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	@Basic(optional = false)
	@Column(name = "importante", nullable = false)
	private boolean importante;
    @Basic(optional = false)
    @Column(name = "messaggio", nullable = false, length = 256)
    private String messaggio;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
    
    public Avvisi() {
    }

    public Avvisi(Integer id) {
        this.id = id;
    }

    public Avvisi(Integer id, boolean importante, String messaggio) {
        this.id = id;
        this.importante = importante;
        this.messaggio = messaggio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public boolean getImportante() {
		return importante;
	}

	public void setImportante(boolean importante) {
		this.importante = importante;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
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
        if (!(object instanceof Avvisi)) {
            return false;
        }
        Avvisi other = (Avvisi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Avvisi[id=" + id + "]";
    }

}
