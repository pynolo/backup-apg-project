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
@Table(name = "aliquote_iva")
public class AliquoteIva extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 2099639401477130250L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "codice_ita_pvt", length = 8, nullable = false)
	private String codiceItaPvt;
	@Basic(optional = false)
	@Column(name = "codice_ita_soc", length = 8, nullable = false)
	private String codiceItaSoc;
	@Basic(optional = false)
	@Column(name = "codice_ue_pvt", length = 8, nullable = false)
	private String codiceUePvt;
	@Basic(optional = false)
	@Column(name = "codice_ue_soc", length = 8, nullable = false)
	private String codiceUeSoc;
	@Basic(optional = false)
	@Column(name = "codice_extra_ue", length = 8, nullable = false)
	private String codiceExtraUe;
	@Basic(optional = false)
	@Column(name = "descr", length = 128, nullable = false)
	private String descr;
	@Basic(optional = false)
	@Column(name = "valore", nullable = false, precision = 9, scale = 2)
	private Double valore;
	@Basic(optional = false)
	@Column(name = "data_inizio", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataInizio;
	@Column(name = "data_fine")
	@Temporal(TemporalType.DATE)
	private Date dataFine;

	public AliquoteIva() {
	}

	public AliquoteIva(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Double getValore() {
		return valore;
	}

	public void setValore(Double valore) {
		this.valore = valore;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public String getCodiceItaPvt() {
		return codiceItaPvt;
	}

	public void setCodiceItaPvt(String codiceItaPvt) {
		this.codiceItaPvt = codiceItaPvt;
	}

	public String getCodiceItaSoc() {
		return codiceItaSoc;
	}

	public void setCodiceItaSoc(String codiceItaSoc) {
		this.codiceItaSoc = codiceItaSoc;
	}

	public String getCodiceUePvt() {
		return codiceUePvt;
	}

	public void setCodiceUePvt(String codiceUePvt) {
		this.codiceUePvt = codiceUePvt;
	}

	public String getCodiceUeSoc() {
		return codiceUeSoc;
	}

	public void setCodiceUeSoc(String codiceUeSoc) {
		this.codiceUeSoc = codiceUeSoc;
	}

	public String getCodiceExtraUe() {
		return codiceExtraUe;
	}

	public void setCodiceExtraUe(String codiceExtraUe) {
		this.codiceExtraUe = codiceExtraUe;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AliquoteIva)) {
			return false;
		}
		AliquoteIva other = (AliquoteIva) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "AliquoteIva[id=" + id + "]";
		return result;
	}

}
