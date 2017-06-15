/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

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

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "opzioni_istanze_abbonamenti")
public class OpzioniIstanzeAbbonamenti extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -833830603325650494L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "id_fattura")
    private Integer idFattura;
	
    @JoinColumn(name = "id_istanza", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    private IstanzeAbbonamenti istanza;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Opzioni opzione;
    
    public OpzioniIstanzeAbbonamenti() {
    }

    public OpzioniIstanzeAbbonamenti(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public IstanzeAbbonamenti getIstanza() {
		return istanza;
	}

	public void setIstanza(IstanzeAbbonamenti istanza) {
		this.istanza = istanza;
	}

	public Integer getIdFattura() {
		return idFattura;
	}

	public void setIdFattura(Integer idFattura) {
		this.idFattura = idFattura;
	}

	public Opzioni getOpzione() {
		return opzione;
	}

	public void setOpzione(Opzioni opzione) {
		this.opzione = opzione;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OpzioniIstanzeAbbonamenti)) {
            return false;
        }
        OpzioniIstanzeAbbonamenti other = (OpzioniIstanzeAbbonamenti) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    	String s = "OpzioniIstanzeAbbonamenti[id=" + id + "] ";
        return s;
    }

}
