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
@Table(name = "tipi_abbonamento")
public class TipiAbbonamento extends BaseEntity {
	private static final long serialVersionUID = -8318630536446191880L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "codice", length = 8, nullable = false)
	private String codice;
	@Basic(optional = false)
	@Column(name = "nome", nullable = false, length = 64)
	private String nome;
	@Column(name = "data_modifica")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	@JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Periodici periodico;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
	@Column(name = "delta_inizio_blocco_offerta")
	private Integer deltaInizioBloccoOfferta;
	@Column(name = "delta_inizio_avviso_pagamento")
	private Integer deltaInizioAvvisoPagamento;
	@Column(name = "delta_inizio_pagamento_automatico")
	private Integer deltaInizioPagamentoAutomatico;
	@Column(name = "delta_fine_rinnovo_abilitato")
	private Integer deltaFineRinnovoAbilitato;
	@Column(name = "delta_fine_avviso_rinnovo")
	private Integer deltaFineAvvisoRinnovo;
	@Column(name = "delta_fine_rinnovo_automatico")
	private Integer deltaFineRinnovoAutomatico;
	
	@Transient
	private String idPeriodicoT;
	
	
	public TipiAbbonamento() {
	}

	public TipiAbbonamento(Integer id) {
		this.id = id;
	}

	public TipiAbbonamento(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
	}
	
	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Integer getDeltaFineRinnovoAutomatico() {
		return deltaFineRinnovoAutomatico;
	}

	public void setDeltaFineRinnovoAutomatico(Integer deltaFineRinnovoAutomatico) {
		this.deltaFineRinnovoAutomatico = deltaFineRinnovoAutomatico;
	}

	public Integer getDeltaInizioAvvisoPagamento() {
		return deltaInizioAvvisoPagamento;
	}

	public void setDeltaInizioAvvisoPagamento(Integer deltaInizioAvvisoPagamento) {
		this.deltaInizioAvvisoPagamento = deltaInizioAvvisoPagamento;
	}

	public Integer getDeltaInizioPagamentoAutomatico() {
		return deltaInizioPagamentoAutomatico;
	}

	public void setDeltaInizioPagamentoAutomatico(
			Integer deltaInizioPagamentoAutomatico) {
		this.deltaInizioPagamentoAutomatico = deltaInizioPagamentoAutomatico;
	}

	public Integer getDeltaFineRinnovoAbilitato() {
		return deltaFineRinnovoAbilitato;
	}

	public void setDeltaFineRinnovoAbilitato(Integer deltaFineRinnovoAbilitato) {
		this.deltaFineRinnovoAbilitato = deltaFineRinnovoAbilitato;
	}

	public Integer getDeltaFineAvvisoRinnovo() {
		return deltaFineAvvisoRinnovo;
	}

	public void setDeltaFineAvvisoRinnovo(Integer deltaFineAvvisoRinnovo) {
		this.deltaFineAvvisoRinnovo = deltaFineAvvisoRinnovo;
	}

	public Integer getDeltaInizioBloccoOfferta() {
		return deltaInizioBloccoOfferta;
	}

	public void setDeltaInizioBloccoOfferta(Integer deltaInizioBloccoOfferta) {
		this.deltaInizioBloccoOfferta = deltaInizioBloccoOfferta;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TipiAbbonamento)) {
			return false;
		}
		TipiAbbonamento other = (TipiAbbonamento) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "TipiAbbonamento[id=" + id + "]";
		result += " "+periodico.getUid()+
			" '"+codice+
			"' "+nome;
		return result;
	}

	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodico) {
		this.idPeriodicoT = idPeriodico;
	}

}
