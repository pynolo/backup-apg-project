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
@Table(name = "listini")
public class Listini {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "data_inizio", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataInizio;
	@Basic(optional = false)
	@Column(name = "prezzo", nullable = false, precision = 9, scale = 2)
	private Double prezzo;
	@Column(name = "note", length = 255)
	private String note;
	@Column(name = "mese_inizio")
	private Integer meseInizio;
	@Basic(optional = false)
	@Column(name = "gracing_iniziale", nullable = false)
	private Integer gracingIniziale;
	@Basic(optional = false)
	@Column(name = "gracing_finale", nullable = false)
	private Integer gracingFinale;
	@Column(name = "data_fine")
	@Temporal(TemporalType.DATE)
	private Date dataFine;
	@Basic(optional = false)
	@Column(name = "num_fascicoli", nullable = false)
	private int numFascicoli;
	@Basic(optional = false)
	@Column(name = "id_macroarea", nullable = false)
	private Integer idMacroarea;
	@Basic(optional = false)
	@Column(name = "invio_senza_pagamento", nullable = false)
	private boolean invioSenzaPagamento;
	@Basic(optional = false)
	@Column(name = "fattura_differita", nullable = false)
	private boolean fatturaDifferita;//pagatoConFattura;
	@Basic(optional = false)
	@Column(name = "fattura_inibita", nullable = false)
	private boolean fatturaInibita;
    @Basic(optional = false)
    @Column(name = "stampa_donatore", nullable = false)
	private boolean stampaDonatore;
    @Basic(optional = false)
    @Column(name = "stampa_scritta_omaggio", nullable = false)
	private boolean stampaScrittaOmaggio;
	//@Column(name = "prezzo_opz_obbligatori", precision = 9, scale = 2)
	//private Double prezzoOpzObbligatori;
	@Column(name = "data_modifica")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	@Basic(optional = false)
	@Column(name = "cartaceo", nullable = false)
	private boolean cartaceo;
	@Basic(optional = false)
	@Column(name = "digitale", nullable = false)
	private boolean digitale;
    @Column(name = "tag", length = 256)
    private String tag;
	@JoinColumn(name = "id_tipo_abbonamento", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private TipiAbbonamento tipoAbbonamento;
	//@JoinColumn(name = "id_aliquota_iva", referencedColumnName = "id", nullable = false)
	//@ManyToOne(optional = false, fetch = FetchType.EAGER)
	//private AliquoteIva aliquotaIva;
    @Basic(optional = false)
	@Column(name = "uid", length = 16, nullable = false)
	private String uid;
	//@OneToMany(fetch = FetchType.EAGER, mappedBy="listino")
	//private Set<OpzioniListini> opzioniListiniSet;
	//@OneToMany(fetch = FetchType.EAGER, mappedBy="listino")
	//private Set<ArticoliListini> articoliListiniSet;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
	@Transient
    private String idAliquotaIvaT;
	@Transient
	private Set<Integer> idOpzioniListiniSetT;
	
	public Listini() {
	}

	public Listini(Integer id) {
		this.id = id;
	}

	public Listini(Integer id, Date dataInizio, Double prezzo, int numFascicoli, boolean invioSenzaPagamento) {
		this.id = id;
		this.dataInizio = dataInizio;
		this.prezzo = prezzo;
		this.numFascicoli = numFascicoli;
		this.invioSenzaPagamento = invioSenzaPagamento;
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

	public Double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getGracingIniziale() {
		return gracingIniziale;
	}

	public void setGracingIniziale(Integer numeriAntepagamento) {
		this.gracingIniziale = numeriAntepagamento;
	}

	public Integer getGracingFinale() {
		return gracingFinale;
	}

	public void setGracingFinale(Integer numeriSuccessivi) {
		this.gracingFinale = numeriSuccessivi;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public int getNumFascicoli() {
		return numFascicoli;
	}

	public void setNumFascicoli(int numFascicoli) {
		this.numFascicoli = numFascicoli;
	}

	public boolean getInvioSenzaPagamento() {
		return invioSenzaPagamento;
	}

	public void setInvioSenzaPagamento(boolean invioSenzaPagamento) {
		this.invioSenzaPagamento = invioSenzaPagamento;
	}

	public boolean getFatturaDifferita() {
		return fatturaDifferita;
	}

	public void setFatturaDifferita(boolean fatturaDifferita) {
		this.fatturaDifferita = fatturaDifferita;
	}

	public boolean getFatturaInibita() {
		return fatturaInibita;
	}

	public void setFatturaInibita(boolean fatturaInibita) {
		this.fatturaInibita = fatturaInibita;
	}

	public boolean getStampaDonatore() {
		return stampaDonatore;
	}

	public void setStampaDonatore(boolean stampaDonatore) {
		this.stampaDonatore = stampaDonatore;
	}

	public boolean getStampaScrittaOmaggio() {
		return stampaScrittaOmaggio;
	}

	public void setStampaScrittaOmaggio(boolean stampaScrittaOmaggio) {
		this.stampaScrittaOmaggio = stampaScrittaOmaggio;
	}

	//public Double getPrezzoOpzObbligatori() {
	//	return prezzoOpzObbligatori;
	//}
	//
	//public void setPrezzoOpzObbligatori(Double prezzoOpzObbligatori) {
	//	this.prezzoOpzObbligatori = prezzoOpzObbligatori;
	//}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public boolean getCartaceo() {
		return cartaceo;
	}

	public void setCartaceo(boolean cartaceo) {
		this.cartaceo = cartaceo;
	}

	public boolean getDigitale() {
		return digitale;
	}

	public void setDigitale(boolean digitale) {
		this.digitale = digitale;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public TipiAbbonamento getTipoAbbonamento() {
		return tipoAbbonamento;
	}

	public void setTipoAbbonamento(TipiAbbonamento tipoAbbonamento) {
		this.tipoAbbonamento = tipoAbbonamento;
	}

	public Integer getIdMacroarea() {
		return idMacroarea;
	}

	public void setIdMacroarea(Integer idMacroarea) {
		this.idMacroarea = idMacroarea;
	}

	public Integer getMeseInizio() {
		return meseInizio;
	}

	public void setMeseInizio(Integer meseInizio) {
		this.meseInizio = meseInizio;
	}
	
//
//	public Integer getIdArticolo() {
//		return idArticolo;
//	}
//
//	public void setIdArticolo(Integer idArticolo) {
//		this.idArticolo = idArticolo;
//	}
//
//	public String getIdTipoDestinatarioArticolo() {
//		return idTipoDestinatarioArticolo;
//	}
//
//	public void setIdTipoDestinatarioArticolo(String idTipoDestinatarioArticolo) {
//		this.idTipoDestinatarioArticolo = idTipoDestinatarioArticolo;
//	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Set<Integer> getIdOpzioniListiniSetT() {
		return idOpzioniListiniSetT;
	}

	public void setIdOpzioniListiniSetT(Set<Integer> idOpzioniListiniSetT) {
		this.idOpzioniListiniSetT = idOpzioniListiniSetT;
	}

	public String getIdAliquotaIvaT() {
		return idAliquotaIvaT;
	}

	public void setIdAliquotaIvaT(String idAliquotaIvaT) {
		this.idAliquotaIvaT = idAliquotaIvaT;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Listini)) {
			return false;
		}
		Listini other = (Listini) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String result = "Listini[id=" + id + "]";
		if (tipoAbbonamento!=null) {
			result += " "+tipoAbbonamento.getPeriodico().getUid()+
					" '"+tipoAbbonamento.getCodice()+
					"' "+tipoAbbonamento.getNome();
		}
		return result;
	}

}
