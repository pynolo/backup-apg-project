/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "materiali")
public class Materiali extends BaseEntity {
	private static final long serialVersionUID = 8337599557351739085L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "codice_meccanografico", nullable = false, length = 32)
    private String codiceMeccanografico;
    @Basic(optional = false)
    @Column(name = "titolo", nullable = false, length = 64)
    private String titolo;
    @Column(name = "sottotitolo", length = 64)
    private String sottotitolo;
    @Column(name = "note", length = 255)
    private String note;
	@Basic(optional = false)
	@Column(name = "in_attesa", nullable = false)
	private boolean inAttesa;
	@Basic(optional = false)
	@Column(name = "id_tipo_anagrafica_sap", nullable = false, length = 4)
	private String idTipoAnagraficaSap;
	@Basic(optional = false)
	@Column(name = "id_tipo_materiale", nullable = false, length = 4)
	private String idTipoMateriale;
	
	public Materiali() {
    }
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodiceMeccanografico() {
		return codiceMeccanografico;
	}

	public void setCodiceMeccanografico(String codiceMeccanografico) {
		this.codiceMeccanografico = codiceMeccanografico;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getSottotitolo() {
		return sottotitolo;
	}

	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean getInAttesa() {
		return inAttesa;
	}

	public void setInAttesa(boolean inAttesa) {
		this.inAttesa = inAttesa;
	}

	public String getIdTipoAnagraficaSap() {
		return idTipoAnagraficaSap;
	}

	public void setIdTipoAnagraficaSap(String idTipoAnagraficaSap) {
		this.idTipoAnagraficaSap = idTipoAnagraficaSap;
	}

	public String getIdTipoMateriale() {
		return idTipoMateriale;
	}

	public void setIdTipoMateriale(String idTipoMateriale) {
		this.idTipoMateriale = idTipoMateriale;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Materiali)) {
            return false;
        }
        Materiali other = (Materiali) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "Materiali[id=" + id + "] "+codiceMeccanografico+" "+titolo;
        return result;
    }

}
