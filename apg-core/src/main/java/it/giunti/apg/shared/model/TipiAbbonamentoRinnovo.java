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
@Table(name = "tipi_abbonamento_rinnovo")
public class TipiAbbonamentoRinnovo extends BaseEntity {
	private static final long serialVersionUID = -5311630536446191881L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "id_listino", nullable = false)
	private Integer idListino;
	@JoinColumn(name = "id_tipo_abbonamento", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private TipiAbbonamento tipoAbbonamento;
	@Basic(optional = false)
	@Column(name = "ordine", nullable = false)
	private Integer ordine;
	
	public TipiAbbonamentoRinnovo() {
	}

	public TipiAbbonamentoRinnovo(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdListino() {
		return idListino;
	}

	public void setIdListino(Integer idListino) {
		this.idListino = idListino;
	}

	public TipiAbbonamento getTipoAbbonamento() {
		return tipoAbbonamento;
	}

	public void setTipoAbbonamento(TipiAbbonamento tipoAbbonamento) {
		this.tipoAbbonamento = tipoAbbonamento;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TipiAbbonamentoRinnovo)) {
			return false;
		}
		TipiAbbonamentoRinnovo other = (TipiAbbonamentoRinnovo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "TipiAbbonamentoRinnovo[id=" + id + "]";
		return result;
	}

}
