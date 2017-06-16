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
@Table(name = "opzioni_listini")
public class OpzioniListini extends BaseEntity {
	private static final long serialVersionUID = 4622192359766219121L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Opzioni opzione;
    @JoinColumn(name = "id_listino", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Listini listino;
	//@JoinColumn(name = "id_utente", referencedColumnName = "id", nullable = false)
	//@ManyToOne(optional = false, fetch = FetchType.EAGER)
	//private Utenti utente;
    
    
    public OpzioniListini() {
    }

    public OpzioniListini(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Opzioni getOpzione() {
		return opzione;
	}

	public void setOpzione(Opzioni opzione) {
		this.opzione = opzione;
	}

	public Listini getListino() {
		return listino;
	}

	public void setListino(Listini listino) {
		this.listino = listino;
	}

	//public Utenti getUtente() {
	//	return utente;
	//}
	//
	//public void setUtente(Utenti utente) {
	//	this.utente = utente;
	//}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OpzioniListini)) {
            return false;
        }
        OpzioniListini other = (OpzioniListini) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "OpzioniListini[id=" + id + "] ";
        return result;
    }

}
