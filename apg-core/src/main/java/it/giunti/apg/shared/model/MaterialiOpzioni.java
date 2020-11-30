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
@Table(name = "materiali_opzioni")
public class MaterialiOpzioni extends BaseEntity {
	private static final long serialVersionUID = 6085192089768249135L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
    @JoinColumn(name = "id_articolo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Articoli6 articolo6;//TODO remove
    @JoinColumn(name = "id_materiale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Materiali materiale;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Opzioni opzione;
    
	@Transient
    private String materialeCmT;
    
    public MaterialiOpzioni() {
    }

    public MaterialiOpzioni(Integer id) {
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
		return articolo6;
	}

	public void setArticolo6(Articoli6 articolo6) {
		this.articolo6 = articolo6;
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

	public String getMaterialeCmT() {
		return materialeCmT;
	}

	public void setMaterialeCmT(String materialeCmT) {
		this.materialeCmT = materialeCmT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaterialiOpzioni)) {
            return false;
        }
        MaterialiOpzioni other = (MaterialiOpzioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "MaterialiOpzioni[id=" + id + "] ";
        return result;
    }

}
