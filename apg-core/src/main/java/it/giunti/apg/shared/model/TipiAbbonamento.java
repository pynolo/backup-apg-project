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
	@SuppressWarnings("unused")
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
	@Basic(optional = false)
	@Column(name = "permetti_pagante", nullable = false)
	private boolean permettiPagante;
	@Column(name = "data_modifica")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	
	@JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Periodici periodico;
	@Transient
	private String idPeriodicoT;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
	
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

	public boolean getPermettiPagante() {
		return permettiPagante;
	}

	public void setPermettiPagante(boolean permettiPagante) {
		this.permettiPagante = permettiPagante;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
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
