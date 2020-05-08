package it.giunti.apg.export.model;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "istanze_abbonamenti")
public class IstanzeAbbonamenti {
	
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
	@Basic(optional = false)
	@Column(name = "proposta_acquisto", nullable = false)
	private boolean propostaAcquisto;
	@Column(name = "fattura_numero", length = 32)
    private String fatturaNumero;
	@Column(name = "fattura_data")
	@Temporal(TemporalType.DATE)
    private Date fatturaData;
	@Column(name = "fattura_importo", columnDefinition = "decimal(9,2)")
    private Double fatturaImporto;
	@Basic(optional = false)
	@Column(name = "fattura_pagata", nullable = false)
	private boolean fatturaPagata;
	@Basic(optional = false)
	@Column(name = "necessita_verifica", nullable = false)
	private boolean necessitaVerifica;
	@Column(name = "adesione", length = 32)
	private String adesione = null;	
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
    @Column(name = "id_fattura")
    private Integer idFattura;
	@Column(name = "data_cambio_tipo")
	@Temporal(TemporalType.DATE)
	private Date dataCambioTipo;
	@Basic(optional = false)
	@Column(name = "data_sync_mailing", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSyncMailing;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
    @Column(name = "update_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp;
    
	@Column(name = "id_abbonato")
	private Integer idAbbonato;
	@Column(name = "id_pagante")
	private Integer idPagante;
	@Column(name = "id_promotore")
	private Integer idPromotore;
	
	//@JoinColumn(name = "id_abbonato", referencedColumnName = "id", nullable = false)
	//@ManyToOne(optional = false, fetch = FetchType.EAGER)
	//private Anagrafiche abbonato;
	//@JoinColumn(name = "id_pagante", referencedColumnName = "id")
	//@ManyToOne(fetch = FetchType.EAGER)
	//private Anagrafiche pagante;
	//@JoinColumn(name = "id_promotore", referencedColumnName = "id")
	//@ManyToOne(fetch = FetchType.EAGER)
	//private Anagrafiche promotore;
	@JoinColumn(name = "id_fascicolo_inizio", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Fascicoli fascicoloInizio;
	@JoinColumn(name = "id_fascicolo_fine", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Fascicoli fascicoloFine;
	@JoinColumn(name = "id_abbonamento", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Abbonamenti abbonamento;
	@JoinColumn(name = "id_listino", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Listini listino;
    
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
	
	public IstanzeAbbonamenti() {
	}

	public IstanzeAbbonamenti(Integer id) {
		this.id = id;
	}

	public IstanzeAbbonamenti(Integer id, boolean ultimaDellaSerie, Date dataCreazione, int copie, String note,
			Fascicoli fascicoloInizio, Fascicoli fascicoloFine, int fascicoliSpediti, int fascicoliTotali,
			boolean pagato, Date dataSaldo, boolean invioBloccato, boolean fatturaDifferita, boolean propostaAcquisto,
			Abbonamenti abbonamento, Listini listino, String fatturaNumero, Date fatturaData, Double fatturaImporto,
			boolean fatturaPagata, boolean necessitaVerifica, String adesione, Date dataModifica, Date dataDisdetta,
			Integer idTipoDisdetta, Date dataJob, Integer idAbbonato, Integer idPagante, Integer idPromotore,
			Integer idFattura, Date dataCambioTipo, Date dataSyncMailing, String idUtente, Date updateTimestamp) {
		super();
		this.id = id;
		this.ultimaDellaSerie = ultimaDellaSerie;
		this.dataCreazione = dataCreazione;
		this.copie = copie;
		this.note = note;
		this.fascicoloInizio = fascicoloInizio;
		this.fascicoloFine = fascicoloFine;
		this.fascicoliSpediti = fascicoliSpediti;
		this.fascicoliTotali = fascicoliTotali;
		this.pagato = pagato;
		this.dataSaldo = dataSaldo;
		this.invioBloccato = invioBloccato;
		this.fatturaDifferita = fatturaDifferita;
		this.propostaAcquisto = propostaAcquisto;
		this.abbonamento = abbonamento;
		this.listino = listino;
		this.fatturaNumero = fatturaNumero;
		this.fatturaData = fatturaData;
		this.fatturaImporto = fatturaImporto;
		this.fatturaPagata = fatturaPagata;
		this.necessitaVerifica = necessitaVerifica;
		this.adesione = adesione;
		this.dataModifica = dataModifica;
		this.dataDisdetta = dataDisdetta;
		this.idTipoDisdetta = idTipoDisdetta;
		this.dataJob = dataJob;
		this.idAbbonato = idAbbonato;
		this.idPagante = idPagante;
		this.idPromotore = idPromotore;
		this.idFattura = idFattura;
		this.dataCambioTipo = dataCambioTipo;
		this.dataSyncMailing = dataSyncMailing;
		this.idUtente = idUtente;
		this.updateTimestamp = updateTimestamp;
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

	public boolean getPropostaAcquisto() {
		return propostaAcquisto;
	}

	public void setPropostaAcquisto(boolean propostaAcquisto) {
		this.propostaAcquisto = propostaAcquisto;
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

	public Integer getIdAbbonato() {
		return idAbbonato;
	}

	public void setIdAbbonato(Integer idAbbonato) {
		this.idAbbonato = idAbbonato;
	}

	public Integer getIdPagante() {
		return idPagante;
	}

	public void setIdPagante(Integer idPagante) {
		this.idPagante = idPagante;
	}

	public Integer getIdPromotore() {
		return idPromotore;
	}

	public void setIdPromotore(Integer idPromotore) {
		this.idPromotore = idPromotore;
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

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
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
