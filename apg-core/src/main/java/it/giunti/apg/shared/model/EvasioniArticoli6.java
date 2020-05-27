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
@Table(name = "evasioni_articoli")
public class EvasioniArticoli6 extends BaseEntity implements IEvasioni6 {
	private static final long serialVersionUID = 7016062584501644179L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;
    @Column(name = "data_ordine")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOrdine;
    @Column(name = "data_invio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInvio;
    @Column(name = "copie")
    private Integer copie;
    @Column(name = "prenotazione_istanza_futura")
    private Boolean prenotazioneIstanzaFutura;
	@Basic(optional = false)
	@Column(name = "id_tipo_destinatario", nullable = false, length = 4)
	private String idTipoDestinatario;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "data_limite")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLimite;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
	@Column(name = "id_istanza_abbonamento")
	private Integer idIstanzaAbbonamento;
	@Column(name = "id_abbonamento")
	private Integer idAbbonamento;
	@Column(name = "id_articolo_listino")
	private Integer idArticoloListino;
	@Column(name = "id_articolo_opzione")
	private Integer idArticoloOpzione;
	@Column(name = "id_anagrafica", nullable = false)
	private Integer idAnagrafica;
    @JoinColumn(name = "id_articolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Articoli6 articolo;
    @JoinColumn(name = "id_ordine_logistica", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private OrdiniLogistica ordiniLogistica;

    @Column(name = "data_conferma_evasione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConfermaEvasione;
    @Column(name = "data_annullamento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAnnullamento;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
    @Transient
    private String idArticoloT;
    
    
    public EvasioniArticoli6() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

    public Date getDataOrdine() {
		return dataOrdine;
	}

	public void setDataOrdine(Date dataOrdine) {
		this.dataOrdine = dataOrdine;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}
	
	public Integer getCopie() {
		return copie;
	}

	public void setCopie(Integer copie) {
		this.copie = copie;
	}

	public Boolean getPrenotazioneIstanzaFutura() {
		return prenotazioneIstanzaFutura;
	}

	public void setPrenotazioneIstanzaFutura(Boolean prenotazioneIstanzaFutura) {
		this.prenotazioneIstanzaFutura = prenotazioneIstanzaFutura;
	}

	public String getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	public void setIdTipoDestinatario(String idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getDataLimite() {
		return dataLimite;
	}
	
	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getIdArticoloT() {
		return idArticoloT;
	}

	public void setIdArticoloT(String idArticoloT) {
		this.idArticoloT = idArticoloT;
	}

	public Articoli6 getArticolo6() {
		return articolo;
	}

	public void setArticolo6(Articoli6 articolo) {
		this.articolo = articolo;
	}

	public Integer getIdIstanzaAbbonamento() {
		return idIstanzaAbbonamento;
	}

	public void setIdIstanzaAbbonamento(Integer idIstanzaAbbonamento) {
		this.idIstanzaAbbonamento = idIstanzaAbbonamento;
	}

	public Integer getIdAbbonamento() {
		return idAbbonamento;
	}

	public void setIdAbbonamento(Integer idAbbonamento) {
		this.idAbbonamento = idAbbonamento;
	}

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public Integer getIdArticoloListino() {
		return idArticoloListino;
	}

	public void setIdArticoloListino(Integer idArticoloListino) {
		this.idArticoloListino = idArticoloListino;
	}

	public Integer getIdArticoloOpzione() {
		return idArticoloOpzione;
	}

	public void setIdArticoloOpzione(Integer idArticoloOpzione) {
		this.idArticoloOpzione = idArticoloOpzione;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public OrdiniLogistica getOrdiniLogistica() {
		return ordiniLogistica;
	}

	public void setOrdiniLogistica(OrdiniLogistica ordiniLogistica) {
		this.ordiniLogistica = ordiniLogistica;
	}

	public Date getDataConfermaEvasione() {
		return dataConfermaEvasione;
	}

	public void setDataConfermaEvasione(Date dataConfermaEvasione) {
		this.dataConfermaEvasione = dataConfermaEvasione;
	}

	public Date getDataAnnullamento() {
		return dataAnnullamento;
	}

	public void setDataAnnullamento(Date dataAnnullamento) {
		this.dataAnnullamento = dataAnnullamento;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EvasioniArticoli6)) {
            return false;
        }
        EvasioniArticoli6 other = (EvasioniArticoli6) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "EvasioniArticoli6[id=" + id + "] ";
        if (articolo != null) {
        	result += articolo.getCodiceInterno()+" ";
        }
        return result;
    }

}
