/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "istanze_abbonamenti")
public class IstanzeAbbonamenti extends BaseEntity {
	private static final long serialVersionUID = 3695892802038549613L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "ultima_della_serie", nullable = false)
	private boolean ultimaDellaSerie;
	@Basic(optional = false)
	@Column(name = "data_creazione", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataCreazione;
	@Basic(optional = false)
	@Column(name = "copie", nullable = false)
	private int copie;
	@Column(name = "note", length = 2024)
	private String note;
	@JoinColumn(name = "id_fascicolo_inizio", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Fascicoli fascicoloInizio;
	@JoinColumn(name = "id_fascicolo_fine", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Fascicoli fascicoloFine;
	@Basic(optional = false)
	@Column(name = "fascicoli_spediti", nullable = false)
	private int fascicoliSpediti;
	@Basic(optional = false)
	@Column(name = "fascicoli_totali", nullable = false)
	private int fascicoliTotali;
	@Basic(optional = false)
	@Column(name = "pagato", nullable = false)
	private boolean pagato;
	@Column(name = "data_saldo")
	private Date dataSaldo;
	@Basic(optional = false)
	@Column(name = "invio_bloccato", nullable = false)
	private boolean invioBloccato;
	@Basic(optional = false)
	@Column(name = "fattura_differita", nullable = false)
	private boolean fatturaDifferita;
	@JoinColumn(name = "id_abbonamento", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Abbonamenti abbonamento;
	@JoinColumn(name = "id_listino", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Listini listino;
	//@JoinColumn(name = "id_articolo_abbonato", referencedColumnName = "id")
	//@ManyToOne(fetch = FetchType.EAGER)
	//private Articoli articoloAbbonato;
	//@Column(name = "spedizione_articolo_abbonato")
	//@Temporal(TemporalType.DATE)
	//private Date spedizioneArticoloAbbonato;
	//@JoinColumn(name = "id_articolo_promotore", referencedColumnName = "id")
	//@ManyToOne(fetch = FetchType.EAGER)
	//private Articoli articoloPromotore;
	//@Column(name = "spedizione_articolo_promotore")
	//@Temporal(TemporalType.DATE)
	//private Date spedizioneArticoloPromotore;
	@Column(name = "fattura_numero", length = 32)
    private String fatturaNumero;
	@Column(name = "fattura_data")
	@Temporal(TemporalType.DATE)
    private Date fatturaData;
	@Column(name = "fattura_importo")
    private Double fatturaImporto;
	@Basic(optional = false)
	@Column(name = "fattura_pagata", nullable = false)
	private boolean fatturaPagata;
	@Basic(optional = false)
	@Column(name = "necessita_verifica", nullable = false)
	private boolean necessitaVerifica;
	@Column(name = "adesione", length = 32)
	private String adesione = null;
	
    @OneToMany(fetch = FetchType.EAGER, mappedBy="istanza")
    private Set<OpzioniIstanzeAbbonamenti> opzioniIstanzeAbbonamentiSet;

	@Basic(optional = false)
	@Column(name = "data_modifica", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	@Column(name = "data_disdetta")
	@Temporal(TemporalType.DATE)
	private Date dataDisdetta;
	@Column(name = "id_tipo_disdetta")
	private Integer idTipoDisdetta;
	@Column(name = "data_job")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataJob;
	@JoinColumn(name = "id_abbonato", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Anagrafiche abbonato;
	@JoinColumn(name = "id_pagante", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Anagrafiche pagante;
	@JoinColumn(name = "id_promotore", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Anagrafiche promotore;
    @Column(name = "id_fattura")
    private Integer idFattura;
	//@JoinColumn(name = "id_adesione", referencedColumnName = "id")
	//@ManyToOne(fetch = FetchType.EAGER)
	//private Adesioni adesione;
	@Column(name = "data_cambio_tipo")
	@Temporal(TemporalType.DATE)
	private Date dataCambioTipo;
	@Basic(optional = false)
	@Column(name = "data_sync_mailing", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSyncMailing;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
	@Transient
	private String idAbbonatoT;
	@Transient
	private String idPaganteT;
	@Transient
	private String idPromotoreT;
	@Transient
	private String idFascicoloInizioT;
	@Transient
	private String idFascicoloFineT;
	@Transient
	private String idListinoT;
	@Transient
	private String idArticoloAbbonatoT;
	@Transient
	private String idArticoloPromotoreT;
	@Transient
	private Set<Integer> idOpzioniIstanzeAbbonamentiSetT;
	@Transient
	private String idAdesioneT;
	
	public IstanzeAbbonamenti() {
	}

	public IstanzeAbbonamenti(Integer id) {
		this.id = id;
	}

	public IstanzeAbbonamenti(Integer id, int copie, boolean ultimaDellaSerie, Date dataCreazione, boolean pagato, boolean invioBloccato) {
		this.id = id;
		this.copie = copie;
		this.ultimaDellaSerie = ultimaDellaSerie;
		this.dataCreazione = dataCreazione;
		this.pagato = pagato;
		this.invioBloccato = invioBloccato;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getCopie() {
		return copie;
	}

	public void setCopie(int copie) {
		this.copie = copie;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean getUltimaDellaSerie() {
		return ultimaDellaSerie;
	}

	public void setUltimaDellaSerie(boolean ultimaDellaSerie) {
		this.ultimaDellaSerie = ultimaDellaSerie;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Fascicoli getFascicoloInizio() {
		return fascicoloInizio;
	}

	public void setFascicoloInizio(Fascicoli fascicoloInizio) {
		this.fascicoloInizio = fascicoloInizio;
	}

	public Fascicoli getFascicoloFine() {
		return fascicoloFine;
	}

	public void setFascicoloFine(Fascicoli fascicoloFine) {
		this.fascicoloFine = fascicoloFine;
	}

	public int getFascicoliSpediti() {
		return fascicoliSpediti;
	}

	public void setFascicoliSpediti(int fascicoliSpediti) {
		this.fascicoliSpediti = fascicoliSpediti;
	}

	public int getFascicoliTotali() {
		return fascicoliTotali;
	}

	public void setFascicoliTotali(int fascicoliTotali) {
		this.fascicoliTotali = fascicoliTotali;
	}

	public boolean getPagato() {
		return pagato;
	}

	public void setPagato(boolean pagato) {
		this.pagato = pagato;
	}

	public Date getDataSaldo() {
		return dataSaldo;
	}

	public void setDataSaldo(Date dataSaldo) {
		this.dataSaldo = dataSaldo;
	}

	public boolean getInvioBloccato() {
		return invioBloccato;
	}

	public void setInvioBloccato(boolean invioBloccato) {
		this.invioBloccato = invioBloccato;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Date getDataCambioTipo() {
		return dataCambioTipo;
	}

	public void setDataCambioTipo(Date dataCambioTipo) {
		this.dataCambioTipo = dataCambioTipo;
	}

	public Abbonamenti getAbbonamento() {
		return abbonamento;
	}

	public void setAbbonamento(Abbonamenti abbonamento) {
		this.abbonamento = abbonamento;
	}

	public Listini getListino() {
		return listino;
	}

	public void setListino(Listini listino) {
		this.listino = listino;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Date getDataDisdetta() {
		return dataDisdetta;
	}

	public void setDataDisdetta(Date dataDisdetta) {
		this.dataDisdetta = dataDisdetta;
	}

	public Integer getIdTipoDisdetta() {
		return idTipoDisdetta;
	}

	public void setIdTipoDisdetta(Integer idTipoDisdetta) {
		this.idTipoDisdetta = idTipoDisdetta;
	}

	public Date getDataJob() {
		return dataJob;
	}

	public void setDataJob(Date dataJob) {
		this.dataJob = dataJob;
	}

	public Anagrafiche getAbbonato() {
		return abbonato;
	}

	public void setAbbonato(Anagrafiche abbonato) {
		this.abbonato = abbonato;
	}
	
	public Anagrafiche getPagante() {
		return pagante;
	}

	public void setPagante(Anagrafiche pagante) {
		this.pagante = pagante;
	}

	public Anagrafiche getPromotore() {
		return promotore;
	}

	public void setPromotore(Anagrafiche promotore) {
		this.promotore = promotore;
	}

	public Integer getIdFattura() {
		return idFattura;
	}

	public void setIdFattura(Integer idFattura) {
		this.idFattura = idFattura;
	}

	public String getAdesione() {
		return adesione;
	}

	public void setAdesione(String adesione) {
		this.adesione = adesione;
	}

	public String getIdAdesioneT() {
		return idAdesioneT;
	}

	public void setIdAdesioneT(String idAdesioneT) {
		this.idAdesioneT = idAdesioneT;
	}

	public String getIdAbbonatoT() {
		return idAbbonatoT;
	}

	public void setIdAbbonatoT(String idAbbonatoT) {
		this.idAbbonatoT = idAbbonatoT;
	}

	public String getIdPaganteT() {
		return idPaganteT;
	}

	public void setIdPaganteT(String idPaganteT) {
		this.idPaganteT = idPaganteT;
	}

	public String getIdPromotoreT() {
		return idPromotoreT;
	}

	public void setIdPromotoreT(String idPromotoreT) {
		this.idPromotoreT = idPromotoreT;
	}

	public String getIdListinoT() {
		return idListinoT;
	}
	
	public void setIdListinoT(String idListino) {
		this.idListinoT = idListino;
	}

	public boolean getFatturaDifferita() {
		return fatturaDifferita;
	}
	
	public void setFatturaDifferita(boolean fatturaDifferita) {
		this.fatturaDifferita = fatturaDifferita;
	}

	public String getIdArticoloAbbonatoT() {
		return idArticoloAbbonatoT;
	}

	public void setIdArticoloAbbonatoT(String idArticoloAbbonatoT) {
		this.idArticoloAbbonatoT = idArticoloAbbonatoT;
	}

	public String getIdArticoloPromotoreT() {
		return idArticoloPromotoreT;
	}

	public void setIdArticoloPromotoreT(String idArticoloPromotoreT) {
		this.idArticoloPromotoreT = idArticoloPromotoreT;
	}

	public String getFatturaNumero() {
		return fatturaNumero;
	}

	public void setFatturaNumero(String fatturaNumero) {
		this.fatturaNumero = fatturaNumero;
	}

	public Date getFatturaData() {
		return fatturaData;
	}

	public void setFatturaData(Date fatturaData) {
		this.fatturaData = fatturaData;
	}

	public Double getFatturaImporto() {
		return fatturaImporto;
	}

	public void setFatturaImporto(Double fatturaImporto) {
		this.fatturaImporto = fatturaImporto;
	}

	public boolean getFatturaPagata() {
		return fatturaPagata;
	}

	public void setFatturaPagata(boolean fatturaPagata) {
		this.fatturaPagata = fatturaPagata;
	}

	public boolean getNecessitaVerifica() {
		return necessitaVerifica;
	}

	public void setNecessitaVerifica(boolean necessitaVerifica) {
		this.necessitaVerifica = necessitaVerifica;
	}

	public Date getDataSyncMailing() {
		return dataSyncMailing;
	}

	public void setDataSyncMailing(Date dataSyncMailing) {
		this.dataSyncMailing = dataSyncMailing;
	}

	public String getIdFascicoloInizioT() {
		return idFascicoloInizioT;
	}

	public void setIdFascicoloInizioT(String idFascicoloInizioT) {
		this.idFascicoloInizioT = idFascicoloInizioT;
	}

	public String getIdFascicoloFineT() {
		return idFascicoloFineT;
	}

	public void setIdFascicoloFineT(String idFascicoloFineT) {
		this.idFascicoloFineT = idFascicoloFineT;
	}

	public Set<OpzioniIstanzeAbbonamenti> getOpzioniIstanzeAbbonamentiSet() {
		return opzioniIstanzeAbbonamentiSet;
	}

	public void setOpzioniIstanzeAbbonamentiSet(
			Set<OpzioniIstanzeAbbonamenti> opzioniIstanzeAbbonamentiSet) {
		this.opzioniIstanzeAbbonamentiSet = opzioniIstanzeAbbonamentiSet;
	}

	public Set<Integer> getIdOpzioniIstanzeAbbonamentiSetT() {
		return idOpzioniIstanzeAbbonamentiSetT;
	}

	public void setIdOpzioniIstanzeAbbonamentiSetT(
			Set<Integer> idOpzioniIstanzeAbbonamentiSetT) {
		this.idOpzioniIstanzeAbbonamentiSetT = idOpzioniIstanzeAbbonamentiSetT;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof IstanzeAbbonamenti)) {
			return false;
		}
		IstanzeAbbonamenti other = (IstanzeAbbonamenti) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "IstanzeAbbonamenti[id=" + id + "] ";
		if (abbonamento != null) result += abbonamento.getCodiceAbbonamento();
		return result;
	}

}
