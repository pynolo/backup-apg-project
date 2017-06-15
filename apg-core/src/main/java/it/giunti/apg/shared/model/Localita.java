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
@Table(name = "localita")
public class Localita extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -6487384277676767661L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "cap", nullable = false, length = 8)
    private String cap;
    @Basic(optional = false)
    @Column(name = "nome", nullable = false, length = 64)
    private String nome;
    @Basic(optional = false)
    @Column(name = "id_provincia", nullable = false, length = 4)
    private String idProvincia;
    @Basic(optional = false)
    @Column(name = "modifica_propagata", nullable = false)
    private boolean modificaPropagata;
    @Basic(optional = false)
    @Column(name = "data_modifica", nullable = false, length = 64)
	@Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    
    public Localita() {
    }

    public Localita(Integer id) {
        this.id = id;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(String idProvincia) {
		this.idProvincia = idProvincia;
	}

	public boolean getModificaPropagata() {
		return modificaPropagata;
	}

	public void setModificaPropagata(boolean modificaPropagata) {
		this.modificaPropagata = modificaPropagata;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Localita)) {
            return false;
        }
        Localita other = (Localita) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cap[id=" + id + "]";
    }

}
