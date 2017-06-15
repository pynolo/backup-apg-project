/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "nazioni")
public class Nazioni extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 16956878836025980L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, length = 4)
    private String id;
    @Basic(optional = false)
    @Column(name = "id_onu", nullable = false)
    private int idOnu;
    @Basic(optional = false)
    @Column(name = "sigla_nazione", nullable = false, length = 2)
    private String siglaNazione;
    @Basic(optional = false)
    @Column(name = "nome_nazione", nullable = false, length = 64)
    private String nomeNazione;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idNazione", fetch = FetchType.EAGER)
//    private List<Regioni> regioniList;
    @JoinColumn(name = "id_macroarea", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Macroaree macroarea;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idNazione", fetch = FetchType.EAGER)
//    private List<Indirizzi> indirizziList;
	@Basic(optional = false)
	@Column(name = "ue", nullable = false)
	private boolean ue;
	
    public Nazioni() {
    }

    public Nazioni(String id) {
        this.id = id;
    }

    public Nazioni(String id, int idOnu, String siglaNazione, String nomeNazione) {
        this.id = id;
        this.idOnu = idOnu;
        this.siglaNazione = siglaNazione;
        this.nomeNazione = nomeNazione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdOnu() {
        return idOnu;
    }

    public void setIdOnu(int idOnu) {
        this.idOnu = idOnu;
    }

    public String getSiglaNazione() {
        return siglaNazione;
    }

    public void setSiglaNazione(String siglaNazione) {
        this.siglaNazione = siglaNazione;
    }

    public String getNomeNazione() {
        return nomeNazione;
    }

    public void setNomeNazione(String nomeNazione) {
        this.nomeNazione = nomeNazione;
    }

    public Macroaree getMacroarea() {
        return macroarea;
    }

    public void setMacroarea(Macroaree macroarea) {
        this.macroarea = macroarea;
    }

    public boolean getUe() {
		return ue;
	}

	public void setUe(boolean ue) {
		this.ue = ue;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Nazioni)) {
            return false;
        }
        Nazioni other = (Nazioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Nazioni[id=" + id + "]";
    }

}
