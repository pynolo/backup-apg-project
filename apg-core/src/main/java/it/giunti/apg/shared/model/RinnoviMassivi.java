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

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "rinnovi_massivi")
public class RinnoviMassivi extends BaseEntity {
	private static final long serialVersionUID = 2709630461477135202L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
    @Column(name = "regola_attiva", nullable = false)
	private boolean regolaAttiva;
	@Basic(optional = false)
	@Column(name = "id_periodico", nullable = false)
	private Integer idPeriodico;
	@Basic(optional = false)
	@Column(name = "id_tipo_abbonamento", nullable = false)
	private Integer idTipoAbbonamento;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
	@JoinColumn(name = "id_fascicolo_inizio", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Fascicoli6 fascicoloInizio6;//TODO remove
	@Basic(optional = false)
    @Column(name = "solo_regolari", nullable = false)
	private boolean soloRegolari;
	@Basic(optional = false)
    @Column(name = "solo_con_pagante", nullable = false)
	private boolean soloConPagante;
	@Basic(optional = false)
    @Column(name = "solo_senza_pagante", nullable = false)
	private boolean soloSenzaPagante;
	@Basic(optional = false)
	@Column(name = "id_tipo_abbonamento_rinnovo", nullable = false)
	private Integer idTipoAbbonamentoRinnovo;
	
	public RinnoviMassivi() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean getRegolaAttiva() {
		return regolaAttiva;
	}

	public void setRegolaAttiva(boolean regolaAttiva) {
		this.regolaAttiva = regolaAttiva;
	}
	
	public boolean getSoloRegolari() {
		return soloRegolari;
	}

	public void setSoloRegolari(boolean soloRegolari) {
		this.soloRegolari = soloRegolari;
	}

	public Integer getIdPeriodico() {
		return idPeriodico;
	}

	public void setIdPeriodico(Integer idPeriodico) {
		this.idPeriodico = idPeriodico;
	}

	public Integer getIdTipoAbbonamento() {
		return idTipoAbbonamento;
	}

	public void setIdTipoAbbonamento(Integer idTipoAbbonamento) {
		this.idTipoAbbonamento = idTipoAbbonamento;
	}

	public Fascicoli6 getFascicoloInizio6() {
		return fascicoloInizio6;
	}

	public void setFascicoloInizio6(Fascicoli6 fascicoloInizio6) {
		this.fascicoloInizio6 = fascicoloInizio6;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Integer getIdTipoAbbonamentoRinnovo() {
		return idTipoAbbonamentoRinnovo;
	}

	public void setIdTipoAbbonamentoRinnovo(Integer idTipoAbbonamentoRinnovo) {
		this.idTipoAbbonamentoRinnovo = idTipoAbbonamentoRinnovo;
	}

	public boolean getSoloConPagante() {
		return soloConPagante;
	}

	public void setSoloConPagante(boolean soloConPagante) {
		this.soloConPagante = soloConPagante;
	}

	public boolean getSoloSenzaPagante() {
		return soloSenzaPagante;
	}

	public void setSoloSenzaPagante(boolean soloSenzaPagante) {
		this.soloSenzaPagante = soloSenzaPagante;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RinnoviMassivi)) {
			return false;
		}
		RinnoviMassivi other = (RinnoviMassivi) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "RinnoviMassivi[id=" + id + "]";
		return result;
	}

}
