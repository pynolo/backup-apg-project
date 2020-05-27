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
@Table(name = "evasioni_comunicazioni")
public class EvasioniComunicazioni extends BaseEntity {
	private static final long serialVersionUID = -5002815724510998716L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreazione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "progressivo")
    private Integer progressivo;
    @Basic(optional = false)
    @Column(name = "eliminato", nullable = false)
    private boolean eliminato;
    @Column(name = "importo_stampato", precision = 9, scale = 2)
    private Double importoStampato;
    @Column(name = "importo_alternativo_stampato", precision = 9, scale = 2)
    private Double importoAlternativoStampato;
    @Column(name = "credito_scalato", precision = 9, scale = 2)
    private Double creditoScalato;
    @Column(name = "messaggio", length = 1024)
    private String messaggio;
    @Basic(optional = false)
    @Column(name = "id_tipo_media", nullable = false, length = 4)
	private String idTipoMedia;
    @Basic(optional = false)
    @Column(name = "id_tipo_destinatario", nullable = false, length = 4)
	private String idTipoDestinatario;
    @Column(name = "note", length = 256)
    private String note;
    @Basic(optional = false)
    @Column(name = "richiesta_rinnovo", nullable = false)
    private boolean richiestaRinnovo;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
    @JoinColumn(name = "id_istanza_abbonamento", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private IstanzeAbbonamenti istanzaAbbonamento;
    @JoinColumn(name = "id_comunicazione", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Comunicazioni comunicazione;
    @JoinColumn(name = "id_fascicolo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Fascicoli6 fascicolo;//TODO remove
    @JoinColumn(name = "id_materiale_programmazione", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private MaterialiProgrammazione materialeProgrammazione;
    
    @Transient
    private String causaleT;
    @Transient
    private String causaleAlternativaT;
    @Transient
    private Integer idIstanzaAbbonamentoT;
    @Transient
    private String idComunicazioneT;
    
    public EvasioniComunicazioni() {
    }

    public EvasioniComunicazioni(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Date getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(Date dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Integer getProgressivo() {
		return progressivo;
	}

	public void setProgressivo(Integer progressivo) {
		this.progressivo = progressivo;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public boolean getEliminato() {
		return eliminato;
	}

	public void setEliminato(boolean eliminato) {
		this.eliminato = eliminato;
	}

	public Double getImportoStampato() {
		return importoStampato;
	}

	public void setImportoStampato(Double importoStampato) {
		this.importoStampato = importoStampato;
	}

	public Double getImportoAlternativoStampato() {
		return importoAlternativoStampato;
	}

	public void setImportoAlternativoStampato(Double importoAlternativoStampato) {
		this.importoAlternativoStampato = importoAlternativoStampato;
	}

	public Double getCreditoScalato() {
		return creditoScalato;
	}

	public void setCreditoScalato(Double creditoScalato) {
		this.creditoScalato = creditoScalato;
	}

	public String getCausaleT() {
		return causaleT;
	}

	public void setCausaleT(String causaleT) {
		this.causaleT = causaleT;
	}

	public String getCausaleAlternativaT() {
		return causaleAlternativaT;
	}

	public void setCausaleAlternativaT(String causaleAlternativaT) {
		this.causaleAlternativaT = causaleAlternativaT;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public String getIdTipoMedia() {
		return idTipoMedia;
	}

	public void setIdTipoMedia(String idTipoMedia) {
		this.idTipoMedia = idTipoMedia;
	}

	public String getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	public void setIdTipoDestinatario(String idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	public boolean getRichiestaRinnovo() {
		return richiestaRinnovo;
	}

	public void setRichiestaRinnovo(boolean richiestaRinnovo) {
		this.richiestaRinnovo = richiestaRinnovo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public MaterialiProgrammazione getMaterialeProgrammazione() {
		return materialeProgrammazione;
	}

	public void setMaterialeProgrammazione(MaterialiProgrammazione materialeProgrammazione) {
		this.materialeProgrammazione = materialeProgrammazione;
	}

	public Integer getIdIstanzaAbbonamentoT() {
		return idIstanzaAbbonamentoT;
	}

	public void setIdIstanzaAbbonamentoT(Integer idIstanzaAbbonamentoT) {
		this.idIstanzaAbbonamentoT = idIstanzaAbbonamentoT;
	}


	public IstanzeAbbonamenti getIstanzaAbbonamento() {
		return istanzaAbbonamento;
	}

	public void setIstanzaAbbonamento(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}

	public String getIdComunicazioneT() {
		return idComunicazioneT;
	}

	public void setIdComunicazioneT(String idComunicazioneT) {
		this.idComunicazioneT = idComunicazioneT;
	}

	public Comunicazioni getComunicazione() {
		return comunicazione;
	}

	public void setComunicazione(Comunicazioni comunicazione) {
		this.comunicazione = comunicazione;
	}

	public Fascicoli6 getFascicolo6() {
		return fascicolo;
	}

	public void setFascicolo6(Fascicoli6 fascicolo) {
		this.fascicolo = fascicolo;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EvasioniComunicazioni)) {
            return false;
        }
        EvasioniComunicazioni other = (EvasioniComunicazioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String descr = "EvasioniComunicazioni[id=" + id + "] "+
        		istanzaAbbonamento.getAbbonamento().getCodiceAbbonamento()+" "+
        		comunicazione.getTitolo();
        return descr;
    }

}
