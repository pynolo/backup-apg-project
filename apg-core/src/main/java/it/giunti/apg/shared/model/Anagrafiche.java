/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;
import java.util.List;

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
@Table(name = "anagrafiche")
public class Anagrafiche extends BaseEntity {
	private static final long serialVersionUID = -5194439277642541695L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
    @Column(name = "uid", nullable = false, length = 16)
    private String uid;
    @Column(name = "sesso", length = 1)
    private String sesso;
    @Column(name = "codice_fiscale", length = 16)
    private String codiceFiscale;
    @Column(name = "partita_iva", length = 16)
    private String partitaIva;
    @Column(name = "tel_casa", length = 32)
    private String telCasa;
    @Column(name = "tel_mobile", length = 32)
    private String telMobile;
    @Column(name = "email_primaria", length = 256)
    private String emailPrimaria;
    @Column(name = "email_secondaria", length = 256)
    private String emailSecondaria;
    @Column(name = "search_string", length = 256)
    private String searchString;
    @Column(name = "note", length = 256)
    private String note;
	//@Basic(optional = false)
	//@Column(name = "consenso_dati", nullable = false)
	//private boolean consensoDati;
	//@Basic(optional = false)
	//@Column(name = "consenso_commerciale", nullable = false)
	//private boolean consensoCommerciale;
    @Column(name = "codice_sap", length = 64)
    private String codiceSap;
    @Column(name = "data_nascita")
    @Temporal(TemporalType.DATE)
    private Date dataNascita;
    @Column(name = "data_creazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreazione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAbbonato", fetch = FetchType.EAGER)
//    private List<Abbonamenti> abbonamentiList;
//    @OneToMany(mappedBy = "idAgente", fetch = FetchType.EAGER)
//    private List<Abbonamenti> abbonamentiList1;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPagante", fetch = FetchType.EAGER)
//    private List<Abbonamenti> abbonamentiList2;
    @JoinColumn(name = "id_professione", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Professioni professione;
    @Transient
    private String idProfessioneT;
    @JoinColumn(name = "id_titolo_studio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private TitoliStudio titoloStudio;
    @Transient
    private String idTitoloStudioT;
    @Basic(optional = false)
    @JoinColumn(name = "id_indirizzo_principale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Indirizzi indirizzoPrincipale;
    @Basic(optional = false)
    @JoinColumn(name = "id_indirizzo_fatturazione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Indirizzi indirizzoFatturazione;
    @Column(name = "id_tipo_anagrafica", length = 8)
    private String idTipoAnagrafica;
    @Transient
    private List<IstanzeAbbonamenti> lastIstancesT;
    @Basic(optional = false)
    @Column(name = "giunti_card", nullable = false)
    private boolean giuntiCard;
    @Column(name = "id_anagrafica_da_aggiornare")
    private Integer idAnagraficaDaAggiornare;
    @Basic(optional = false)
    @Column(name = "necessita_verifica", nullable = false)
    private boolean necessitaVerifica;
    @Column(name = "uid_merge_list", length = 256)
    private String uidMergeList;//codiciClienteMerge;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
    @Column(name = "data_consenso_tos")
    @Temporal(TemporalType.DATE)
    private Date dataConsensoTos;
    @Column(name = "data_consenso_marketing")
    @Temporal(TemporalType.DATE)
    private Date dataConsensoMarketing;
    @Column(name = "data_consenso_profiling")
    @Temporal(TemporalType.DATE)
    private Date dataConsensoProfiling;
	
    public Anagrafiche() {
    }

    public Anagrafiche(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

    public TitoliStudio getTitoloStudio() {
		return titoloStudio;
	}

	public void setTitoloStudio(TitoliStudio titoloStudio) {
		this.titoloStudio = titoloStudio;
	}

	public String getTelCasa() {
        return telCasa;
    }

    public void setTelCasa(String telCasa) {
        this.telCasa = telCasa;
    }

    public String getTelMobile() {
        return telMobile;
    }

    public void setTelMobile(String telMobile) {
        this.telMobile = telMobile;
    }

    public String getEmailPrimaria() {
        return emailPrimaria;
    }

    public void setEmailPrimaria(String emailPrimaria) {
        this.emailPrimaria = emailPrimaria;
    }

    public String getEmailSecondaria() {
        return emailSecondaria;
    }

    public void setEmailSecondaria(String emailSecondaria) {
        this.emailSecondaria = emailSecondaria;
    }
    
    public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCodiceSap() {
		return codiceSap;
	}

	public void setCodiceSap(String codiceSap) {
		this.codiceSap = codiceSap;
	}

	public Date getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Date dataModifica) {
        this.dataModifica = dataModifica;
    }

    public Date getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Professioni getProfessione() {
		return professione;
	}

	public void setProfessione(Professioni professione) {
		this.professione = professione;
	}

	public Indirizzi getIndirizzoPrincipale() {
		return indirizzoPrincipale;
	}

	public void setIndirizzoPrincipale(Indirizzi indirizzoPrincipale) {
		this.indirizzoPrincipale = indirizzoPrincipale;
	}

	public Indirizzi getIndirizzoFatturazione() {
		return indirizzoFatturazione;
	}

	public void setIndirizzoFatturazione(Indirizzi indirizzoFatturazione) {
		this.indirizzoFatturazione = indirizzoFatturazione;
	}


	public String getIdTipoAnagrafica() {
		return idTipoAnagrafica;
	}

	public void setIdTipoAnagrafica(String idTipoAnagrafica) {
		this.idTipoAnagrafica = idTipoAnagrafica;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getIdProfessioneT() {
		return idProfessioneT;
	}
	
	public void setIdProfessioneT(String idProfessione) {
		this.idProfessioneT = idProfessione;
	}

    public String getIdTitoloStudioT() {
		return idTitoloStudioT;
	}

	public void setIdTitoloStudioT(String idTitoloStudioT) {
		this.idTitoloStudioT = idTitoloStudioT;
	}

	public List<IstanzeAbbonamenti> getLastIstancesT() {
		return lastIstancesT;
	}

	public void setLastIstancesT(List<IstanzeAbbonamenti> lastIstancesT) {
		this.lastIstancesT = lastIstancesT;
	}

	public boolean getGiuntiCard() {
		return giuntiCard;
	}

	public void setGiuntiCard(boolean giuntiCard) {
		this.giuntiCard = giuntiCard;
	}
	
	public Integer getIdAnagraficaDaAggiornare() {
		return idAnagraficaDaAggiornare;
	}

	public void setIdAnagraficaDaAggiornare(Integer idAnagraficaDaAggiornare) {
		this.idAnagraficaDaAggiornare = idAnagraficaDaAggiornare;
	}

	public boolean getNecessitaVerifica() {
		return necessitaVerifica;
	}

	public void setNecessitaVerifica(boolean necessitaVerifica) {
		this.necessitaVerifica = necessitaVerifica;
	}
	
	public String getUidMergeList() {
		return uidMergeList;
	}

	public void setUidMergeList(String uidMergeList) {
		this.uidMergeList = uidMergeList;
	}
	
	public Date getDataConsensoTos() {
		return dataConsensoTos;
	}

	public void setDataConsensoTos(Date dataConsensoTos) {
		this.dataConsensoTos = dataConsensoTos;
	}

	public Date getDataConsensoMarketing() {
		return dataConsensoMarketing;
	}

	public void setDataConsensoMarketing(Date dataConsensoMarketing) {
		this.dataConsensoMarketing = dataConsensoMarketing;
	}

	public Date getDataConsensoProfiling() {
		return dataConsensoProfiling;
	}

	public void setDataConsensoProfiling(Date dataConsensoProfiling) {
		this.dataConsensoProfiling = dataConsensoProfiling;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Anagrafiche)) {
            return false;
        }
        Anagrafiche other = (Anagrafiche) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Anagrafiche[id=" + id + "] "+uid;
        return s;
    }

}
