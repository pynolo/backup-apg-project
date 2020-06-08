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

@Entity
@Table(name = "materiali_spedizione")
public class MaterialiSpedizione extends BaseEntity {
	private static final long serialVersionUID = -1138397232914546911L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Column(name = "id_anagrafica", nullable = false)
	private Integer idAnagrafica;
	@Column(name = "id_abbonamento")
	private Integer idAbbonamento;
	@Column(name = "id_articolo_listino")
	private Integer idArticoloListino;
	@Column(name = "id_articolo_opzione")
	private Integer idArticoloOpzione;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;
    @Column(name = "data_invio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInvio;
    @Column(name = "copie")
    private Integer copie;
    @Column(name = "prenotazione_istanza_futura")
    private boolean prenotazioneIstanzaFutura;
    @Column(name = "data_limite")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLimite;
    @Column(name = "data_annullamento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAnnullamento;
    @Column(name = "data_ordine")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOrdine;
    @Column(name = "data_conferma_evasione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConfermaEvasione;
    @Column(name = "rispedizione")
    private boolean rispedizione;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "id_fascicolo")
	private Integer idFascicolo;//TODO rimuovere
    @Column(name = "id_articolo")
	private Integer idArticolo;//TODO rimuovere
    
    @JoinColumn(name = "id_materiale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Materiali materiale;
    @JoinColumn(name = "id_ordine_logistica", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private OrdiniLogistica ordineLogistica;
    
	@Transient
    private String materialeCmT;
	
    
    public MaterialiSpedizione() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public Integer getIdAbbonamento() {
		return idAbbonamento;
	}

	public void setIdAbbonamento(Integer idAbbonamento) {
		this.idAbbonamento = idAbbonamento;
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

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
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

	public Date getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	public Date getDataAnnullamento() {
		return dataAnnullamento;
	}

	public void setDataAnnullamento(Date dataAnnullamento) {
		this.dataAnnullamento = dataAnnullamento;
	}

	public Date getDataOrdine() {
		return dataOrdine;
	}

	public void setDataOrdine(Date dataOrdine) {
		this.dataOrdine = dataOrdine;
	}

	public Date getDataConfermaEvasione() {
		return dataConfermaEvasione;
	}

	public void setDataConfermaEvasione(Date dataConfermaEvasione) {
		this.dataConfermaEvasione = dataConfermaEvasione;
	}

	public boolean getRispedizione() {
		return rispedizione;
	}

	public void setRispedizione(Boolean rispedizione) {
		this.rispedizione = rispedizione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Materiali getMateriale() {
		return materiale;
	}

	public void setMateriale(Materiali materiale) {
		this.materiale = materiale;
	}

	public OrdiniLogistica getOrdineLogistica() {
		return ordineLogistica;
	}

	public void setOrdineLogistica(OrdiniLogistica ordineLogistica) {
		this.ordineLogistica = ordineLogistica;
	}

	public Integer getIdFascicolo() {
		return idFascicolo;
	}

	public void setIdFascicolo(Integer idFascicolo) {
		this.idFascicolo = idFascicolo;
	}

	public Integer getIdArticolo() {
		return idArticolo;
	}

	public void setIdArticolo(Integer idArticolo) {
		this.idArticolo = idArticolo;
	}

	public String getMaterialeCmT() {
		return materialeCmT;
	}

	public void setMaterialeCmT(String materialeCmT) {
		this.materialeCmT = materialeCmT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaterialiSpedizione)) {
            return false;
        }
        MaterialiSpedizione other = (MaterialiSpedizione) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "MaterialiSpedizione[id=" + id + "] ";
        return result;
    }

}
