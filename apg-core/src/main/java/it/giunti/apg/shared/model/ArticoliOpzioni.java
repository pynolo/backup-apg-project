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
@Table(name = "articoli_opzioni")
public class ArticoliOpzioni extends BaseEntity {
	private static final long serialVersionUID = 6085192089768249135L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
    @JoinColumn(name = "id_articolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Articoli6 articolo;//TODO remove
    @JoinColumn(name = "id_materiale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Materiali materiale;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Opzioni opzione;
    
    @Transient
    private Integer idArticoliT;
    
    public ArticoliOpzioni() {
    }

    public ArticoliOpzioni(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(Date dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}

	public Articoli6 getArticolo6() {
		return articolo;
	}

	public void setArticolo6(Articoli6 articolo) {
		this.articolo = articolo;
	}

	public Materiali getMateriale() {
		return materiale;
	}

	public void setMateriale(Materiali materiale) {
		this.materiale = materiale;
	}

	public Opzioni getOpzione() {
		return opzione;
	}

	public void setOpzione(Opzioni opzione) {
		this.opzione = opzione;
	}

	public Integer getIdArticoliT() {
		return idArticoliT;
	}

	public void setIdArticoliT(Integer idArticoliT) {
		this.idArticoliT = idArticoliT;
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
        if (!(object instanceof ArticoliOpzioni)) {
            return false;
        }
        ArticoliOpzioni other = (ArticoliOpzioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "ArticoliListini[id=" + id + "] ";
        return result;
    }

}
