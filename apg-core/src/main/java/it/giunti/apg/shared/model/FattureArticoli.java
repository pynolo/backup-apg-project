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
@Table(name = "fatture_articoli")
public class FattureArticoli extends BaseEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
	@Column(name = "id_fattura", nullable = false)
    private Integer idFattura;
	@JoinColumn(name = "id_aliquota_iva", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.EAGER)
	private AliquoteIva aliquotaIva;
	@Basic(optional = false)
	@Column(name = "importo_tot_unit", nullable = false, precision = 9, scale = 2)
	private Double importoTotUnit;
	@Basic(optional = false)
	@Column(name = "importo_iva_unit", nullable = false, precision = 9, scale = 2)
	private Double importoIvaUnit;
	@Basic(optional = false)
	@Column(name = "importo_imp_unit", nullable = false, precision = 9, scale = 2)
	private Double importoImpUnit;
	@Basic(optional = false)
	@Column(name = "quantita", nullable = false)
	private Integer quantita;
	@Basic(optional = false)
	@Column(name = "descrizione", nullable = false, length = 256)
	private String descrizione;
    @Basic(optional = false)
	@Column(name = "is_resto", nullable = false)
	private boolean isResto;
    @Basic(optional = false)
	@Column(name = "is_iva_scorporata", nullable = false)
	private boolean isIvaScorporata;
    
    public FattureArticoli() {
    }

    public FattureArticoli(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getIdFattura() {
		return idFattura;
	}

	public void setIdFattura(Integer idFattura) {
		this.idFattura = idFattura;
	}

	public AliquoteIva getAliquotaIva() {
		return aliquotaIva;
	}

	public void setAliquotaIva(AliquoteIva aliquotaIva) {
		this.aliquotaIva = aliquotaIva;
	}

	public Double getImportoTotUnit() {
		return importoTotUnit;
	}

	public void setImportoTotUnit(Double importoTotUnit) {
		this.importoTotUnit = importoTotUnit;
	}

	public Double getImportoIvaUnit() {
		return importoIvaUnit;
	}

	public void setImportoIvaUnit(Double importoIvaUnit) {
		this.importoIvaUnit = importoIvaUnit;
	}

	public Double getImportoImpUnit() {
		return importoImpUnit;
	}

	public void setImportoImpUnit(Double importoImpUnit) {
		this.importoImpUnit = importoImpUnit;
	}

	public Integer getQuantita() {
		return quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public boolean getResto() {
		return isResto;
	}

	public void setResto(boolean isResto) {
		this.isResto = isResto;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public boolean getIvaScorporata() {
		return isIvaScorporata;
	}

	public void setIvaScorporata(boolean isIvaScorporata) {
		this.isIvaScorporata = isIvaScorporata;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FattureArticoli)) {
            return false;
        }
        FattureArticoli other = (FattureArticoli) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FattureArticoli[id=" + id + "]";
    }

}
