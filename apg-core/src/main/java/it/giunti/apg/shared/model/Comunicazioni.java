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
@Table(name = "comunicazioni")
public class Comunicazioni extends BaseEntity {
	private static final long serialVersionUID = 7865293070844964798L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "titolo", nullable = false, length = 128)
    private String titolo;
    @Basic(optional = false)
    @Column(name = "id_tipo_media", nullable = false, length = 4)
	private String idTipoMedia;
    @Basic(optional = false)
    @Column(name = "id_tipo_attivazione", nullable = false, length = 4)
	private String idTipoAttivazione;
    @Basic(optional = false)
    @Column(name = "solo_una_copia", nullable = false)
	private boolean soloUnaCopia;
    @Basic(optional = false)
    @Column(name = "solo_piu_copie", nullable = false)
	private boolean soloPiuCopie;
    @Basic(optional = false)
    @Column(name = "solo_non_pagati", nullable = false)
	private boolean soloNonPagati;
	@Basic(optional = false)
	@Column(name = "solo_senza_pagante", nullable = false)
	private boolean soloSenzaPagante;
	@Basic(optional = false)
	@Column(name = "solo_con_pagante", nullable = false)
	private boolean soloConPagante;
    @Basic(optional = false)
    @Column(name = "solo_una_istanza", nullable = false)
	private boolean soloUnaIstanza;
    @Basic(optional = false)
    @Column(name = "solo_molte_istanze", nullable = false)
	private boolean soloMolteIstanze;
    @Column(name = "solo_con_data_inizio")
    @Temporal(TemporalType.DATE)
    private Date soloConDataInizio;
    @Column(name = "tag_opzione", length = 16)
	private String tagOpzione;
    @Basic(optional = false)
    @Column(name = "mostra_prezzo_alternativo", nullable = false)
	private boolean mostraPrezzoAlternativo;
    @Column(name = "numeri_da_inizio_o_fine")
	private Integer numeriDaInizioOFine6;//TODO remove
    @Basic(optional = false)
    @Column(name = "mesi_da_inizio_o_fine", nullable = false)
	private Integer mesiDaInizioOFine;
    @Basic(optional = false)
    @Column(name = "id_tipo_destinatario", nullable = false, length = 4)
	private String idTipoDestinatario;
    @Basic(optional = false)
    @Column(name = "tipi_abbonamento_list", nullable = false, length = 256)
	private String tipiAbbonamentoList;
    @Column(name = "id_bandella", length = 16)
	private String idBandella;
    @Basic(optional = false)
    @Column(name = "bollettino_senza_importo", nullable = false)
    private boolean bollettinoSenzaImporto;
    @Basic(optional = false)
    @Column(name = "richiesta_rinnovo", nullable = false)
    private boolean richiestaRinnovo;
    @Column(name = "id_fascicolo_inizio")
    private Integer idFascicoloInizio6;//TODO remove
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
    @Column(name = "data_fine")
    @Temporal(TemporalType.DATE)
    private Date dataFine;
    @Column(name = "oggetto_messaggio", length = 64)
	private String oggettoMessaggio;
    @Transient
    private String idModelloBollettinoT;
    @Transient
    private String idModelloEmailT;
    @Transient
    private String idPeriodicoT;
    
    @JoinColumn(name = "id_modello_bollettino", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ModelliBollettini modelloBollettino;
    @JoinColumn(name = "id_modello_email", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ModelliEmail modelloEmail;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Periodici periodico;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;

    public Comunicazioni() {
    }

    public Comunicazioni(Integer id) {
        this.id = id;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public boolean getSoloUnaCopia() {
		return soloUnaCopia;
	}

	public void setSoloUnaCopia(boolean soloUnaCopia) {
		this.soloUnaCopia = soloUnaCopia;
	}

	public boolean getSoloPiuCopie() {
		return soloPiuCopie;
	}

	public void setSoloPiuCopie(boolean soloPiuCopie) {
		this.soloPiuCopie = soloPiuCopie;
	}

	public boolean getSoloNonPagati() {
		return soloNonPagati;
	}

	public void setSoloNonPagati(boolean soloNonPagati) {
		this.soloNonPagati = soloNonPagati;
	}
	
	public boolean getSoloSenzaPagante() {
		return soloSenzaPagante;
	}

	public void setSoloSenzaPagante(boolean soloSenzaPagante) {
		this.soloSenzaPagante = soloSenzaPagante;
	}

	public boolean getSoloConPagante() {
		return soloConPagante;
	}

	public void setSoloConPagante(boolean soloConPagante) {
		this.soloConPagante = soloConPagante;
	}

	public boolean getSoloUnaIstanza() {
		return soloUnaIstanza;
	}

	public void setSoloUnaIstanza(boolean soloUnaIstanza) {
		this.soloUnaIstanza = soloUnaIstanza;
	}

	public boolean getSoloMolteIstanze() {
		return soloMolteIstanze;
	}

	public void setSoloMolteIstanze(boolean soloMolteIstanze) {
		this.soloMolteIstanze = soloMolteIstanze;
	}

	public Date getSoloConDataInizio() {
		return soloConDataInizio;
	}

	public void setSoloConDataInizio(Date soloConDataInizio) {
		this.soloConDataInizio = soloConDataInizio;
	}

	public boolean getMostraPrezzoAlternativo() {
		return mostraPrezzoAlternativo;
	}

	public void setMostraPrezzoAlternativo(boolean mostraPrezzoAlternativo) {
		this.mostraPrezzoAlternativo = mostraPrezzoAlternativo;
	}

	public Integer getNumeriDaInizioOFine6() {
		return numeriDaInizioOFine6;
	}

	public void setNumeriDaInizioOFine6(Integer numeriDaInizioOFine6) {
		this.numeriDaInizioOFine6 = numeriDaInizioOFine6;
	}

	public Integer getMesiDaInizioOFine() {
		return mesiDaInizioOFine;
	}

	public void setMesiDaInizioOFine(Integer mesiDaInizioOFine) {
		this.mesiDaInizioOFine = mesiDaInizioOFine;
	}

	public String getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	public void setIdTipoDestinatario(String idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	public String getTipiAbbonamentoList() {
		return tipiAbbonamentoList;
	}

	public void setTipiAbbonamentoList(String tipiAbbonamentoList) {
		this.tipiAbbonamentoList = tipiAbbonamentoList;
	}
	
	public boolean getBollettinoSenzaImporto() {
		return bollettinoSenzaImporto;
	}

	public void setBollettinoSenzaImporto(boolean bollettinoSenzaImporto) {
		this.bollettinoSenzaImporto = bollettinoSenzaImporto;
	}

	public boolean getRichiestaRinnovo() {
		return richiestaRinnovo;
	}

	public void setRichiestaRinnovo(boolean richiestaRinnovo) {
		this.richiestaRinnovo = richiestaRinnovo;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public String getIdModelloBollettinoT() {
		return idModelloBollettinoT;
	}

	public void setIdModelloBollettinoT(String idModelloBollettinoT) {
		this.idModelloBollettinoT = idModelloBollettinoT;
	}

	public String getIdModelloEmailT() {
		return idModelloEmailT;
	}

	public void setIdModelloEmailT(String idModelloEmailT) {
		this.idModelloEmailT = idModelloEmailT;
	}

	public ModelliBollettini getModelloBollettino() {
		return modelloBollettino;
	}

	public void setModelloBollettino(ModelliBollettini modelloBollettino) {
		this.modelloBollettino = modelloBollettino;
	}

	public ModelliEmail getModelloEmail() {
		return modelloEmail;
	}

	public void setModelloEmail(ModelliEmail modelloEmail) {
		this.modelloEmail = modelloEmail;
	}

	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
	}

	public String getIdTipoMedia() {
		return idTipoMedia;
	}

	public void setIdTipoMedia(String idTipoMedia) {
		this.idTipoMedia = idTipoMedia;
	}

	public String getIdTipoAttivazione() {
		return idTipoAttivazione;
	}

	public void setIdTipoAttivazione(String idTipoAttivazione) {
		this.idTipoAttivazione = idTipoAttivazione;
	}

	public String getIdBandella() {
		return idBandella;
	}

	public void setIdBandella(String idBandella) {
		this.idBandella = idBandella;
	}

	public Integer getIdFascicoloInizio6() {
		return idFascicoloInizio6;
	}

	public void setIdFascicoloInizio6(Integer idFascicoloInizio) {
		this.idFascicoloInizio6 = idFascicoloInizio;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public String getTagOpzione() {
		return tagOpzione;
	}

	public void setTagOpzione(String tagOpzione) {
		this.tagOpzione = tagOpzione;
	}

	public String getOggettoMessaggio() {
		return oggettoMessaggio;
	}

	public void setOggettoMessaggio(String oggettoMessaggio) {
		this.oggettoMessaggio = oggettoMessaggio;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Comunicazioni)) {
            return false;
        }
        Comunicazioni other = (Comunicazioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Comunicazioni[id=" + id + "] "+periodico.getUid()+" "+titolo;
    }

}
