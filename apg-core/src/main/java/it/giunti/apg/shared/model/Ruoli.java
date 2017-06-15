/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "ruoli")
public class Ruoli extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 4151063809487660064L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "descrizione", nullable = false, length = 32)
    private String descrizione;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idRuolo", fetch = FetchType.EAGER)
//    private List<Utenti> utentiList;

    public Ruoli() {
    }

    public Ruoli(Integer id) {
        this.id = id;
    }

    public Ruoli(Integer id, String descrizione) {
        this.id = id;
        this.descrizione = descrizione;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ruoli)) {
            return false;
        }
        Ruoli other = (Ruoli) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ruoli[id=" + id + "]";
    }

}
