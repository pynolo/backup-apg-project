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
@Table(name = "province")
public class Province extends BaseEntity {
	private static final long serialVersionUID = -8108404771590288682L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, length = 4)
    private String id;
    @Column(name = "nome_provincia", length = 30)
    private String nomeProvincia;
    @Column(name = "istat", length = 4)
    private String istat;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
//    @JoinColumn(name = "id_regione", referencedColumnName = "id")
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Regioni regione;
//    @OneToMany(mappedBy = "idProvincia", fetch = FetchType.EAGER)
//    private List<Indirizzi> indirizziList;

    public Province() {
    }

    public Province(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeProvincia() {
        return nomeProvincia;
    }

    public void setNomeProvincia(String nomeProvincia) {
        this.nomeProvincia = nomeProvincia;
    }

    public String getIstat() {
        return istat;
    }

    public void setIstat(String istat) {
        this.istat = istat;
    }

    public Date getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Date dataModifica) {
        this.dataModifica = dataModifica;
    }

//    public Regioni getRegione() {
//        return regione;
//    }
//
//    public void setRegione(Regioni regione) {
//        this.regione = regione;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Province)) {
            return false;
        }
        Province other = (Province) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Province[id=" + id + "]";
    }

}
