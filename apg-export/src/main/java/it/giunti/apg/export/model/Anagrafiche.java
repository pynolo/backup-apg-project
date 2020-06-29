package it.giunti.apg.export.model;

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

@Entity
@Table(name = "anagrafiche")
public class Anagrafiche {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
    @Column(name = "uid", nullable = false, length = 16)
    private String uid;
    @Column(name = "merged_into_uid", length = 16)
    private String mergedIntoUid;
    @Basic(optional = false)
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
    @Column(name = "identity_uid", length = 16)
	private String identityUid;
	
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
    @Column(name = "email_pec", length = 256)
    private String emailPec;
    @Column(name = "search_string", length = 256)
    private String searchString;
    @Column(name = "note", length = 2048)
    private String note;
    @Column(name = "codice_sap", length = 64)
    private String codiceSap;
    @Column(name = "codice_destinatario", length = 8)
    private String codiceDestinatario;
    @Column(name = "cuf", length = 8)
    private String cuf;
    @Column(name = "data_nascita")
    @Temporal(TemporalType.DATE)
    private Date dataNascita;
    @Column(name = "data_creazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreazione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "id_professione")
    private Integer idProfessione;
    @Column(name = "id_titolo_studio")
    private Integer idTitoloStudio;
    @Column(name = "id_tipo_anagrafica", length = 8)
    private String idTipoAnagrafica;
    @Column(name = "giunti_card_club", length = 16)
    private String giuntiCardClub;
    @Basic(optional = false)
    @Column(name = "pa", nullable = false)
    private boolean pa;
    @Column(name = "id_anagrafica_da_aggiornare")
    private Integer idAnagraficaDaAggiornare;
    @Basic(optional = false)
    @Column(name = "necessita_verifica", nullable = false)
    private boolean necessitaVerifica;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
    @Basic(optional = false)
    @Column(name = "consenso_tos", nullable = false)
    private boolean consensoTos;
    @Basic(optional = false)
    @Column(name = "consenso_marketing", nullable = false)
    private boolean consensoMarketing;
    @Basic(optional = false)
    @Column(name = "consenso_profilazione", nullable = false)
    private boolean consensoProfilazione;
    @Column(name = "data_aggiornamento_consenso")
    @Temporal(TemporalType.DATE)
    private Date dataAggiornamentoConsenso;
    @Column(name = "update_timestamp", updatable=false, insertable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp;
    
    @Basic(optional = false)
    @JoinColumn(name = "id_indirizzo_principale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Indirizzi indirizzoPrincipale;
    @Basic(optional = false)
    @JoinColumn(name = "id_indirizzo_fatturazione", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Indirizzi indirizzoFatturazione;
    
    public Anagrafiche() {
    }

    public Anagrafiche(Integer id, String uid, String mergedIntoUid, boolean deleted, String identityUid, String sesso,
			String codiceFiscale, String partitaIva, String telCasa, String telMobile, String emailPrimaria,
			String emailPec, String searchString, String note, String codiceSap, String codiceDestinatario, String cuf,
			Date dataNascita, Date dataCreazione, Date dataModifica, Integer idProfessione, String idProfessioneT,
			Integer idTitoloStudio, String idTitoloStudioT, Indirizzi indirizzoPrincipale,
			Indirizzi indirizzoFatturazione, String idTipoAnagrafica, String giuntiCardClub, boolean pa,
			Integer idAnagraficaDaAggiornare, boolean necessitaVerifica, String idUtente, boolean consensoTos,
			boolean consensoMarketing, boolean consensoProfilazione, Date dataAggiornamentoConsenso) {
		super();
		this.id = id;
		this.uid = uid;
		this.mergedIntoUid = mergedIntoUid;
		this.deleted = deleted;
		this.identityUid = identityUid;
		this.sesso = sesso;
		this.codiceFiscale = codiceFiscale;
		this.partitaIva = partitaIva;
		this.telCasa = telCasa;
		this.telMobile = telMobile;
		this.emailPrimaria = emailPrimaria;
		this.emailPec = emailPec;
		this.searchString = searchString;
		this.note = note;
		this.codiceSap = codiceSap;
		this.codiceDestinatario = codiceDestinatario;
		this.cuf = cuf;
		this.dataNascita = dataNascita;
		this.dataCreazione = dataCreazione;
		this.dataModifica = dataModifica;
		this.idProfessione = idProfessione;
		this.idTitoloStudio = idTitoloStudio;
		this.indirizzoPrincipale = indirizzoPrincipale;
		this.indirizzoFatturazione = indirizzoFatturazione;
		this.idTipoAnagrafica = idTipoAnagrafica;
		this.giuntiCardClub = giuntiCardClub;
		this.pa = pa;
		this.idAnagraficaDaAggiornare = idAnagraficaDaAggiornare;
		this.necessitaVerifica = necessitaVerifica;
		this.idUtente = idUtente;
		this.consensoTos = consensoTos;
		this.consensoMarketing = consensoMarketing;
		this.consensoProfilazione = consensoProfilazione;
		this.dataAggiornamentoConsenso = dataAggiornamentoConsenso;
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

    public String getMergedIntoUid() {
		return mergedIntoUid;
	}

	public void setMergedIntoUid(String mergedIntoUid) {
		this.mergedIntoUid = mergedIntoUid;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getIdentityUid() {
		return identityUid;
	}

	public void setIdentityUid(String identityUid) {
		this.identityUid = identityUid;
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

    public String getEmailPec() {
		return emailPec;
	}

	public void setEmailPec(String emailPec) {
		this.emailPec = emailPec;
	}

	public String getCodiceDestinatario() {
		return codiceDestinatario;
	}

	public void setCodiceDestinatario(String codiceDestinatario) {
		this.codiceDestinatario = codiceDestinatario;
	}

	public String getCuf() {
		return cuf;
	}

	public void setCuf(String cuf) {
		this.cuf = cuf;
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

	public String getGiuntiCardClub() {
		return giuntiCardClub;
	}

	public void setGiuntiCardClub(String giuntiCardClub) {
		this.giuntiCardClub = giuntiCardClub;
	}

	public boolean getPa() {
		return pa;
	}

	public void setPa(boolean pa) {
		this.pa = pa;
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

	public boolean getConsensoTos() {
		return consensoTos;
	}

	public void setConsensoTos(boolean consensoTos) {
		this.consensoTos = consensoTos;
	}

	public boolean getConsensoMarketing() {
		return consensoMarketing;
	}

	public void setConsensoMarketing(boolean consensoMarketing) {
		this.consensoMarketing = consensoMarketing;
	}

	public boolean getConsensoProfilazione() {
		return consensoProfilazione;
	}

	public void setConsensoProfilazione(boolean consensoProfilazione) {
		this.consensoProfilazione = consensoProfilazione;
	}

	public Date getDataAggiornamentoConsenso() {
		return dataAggiornamentoConsenso;
	}

	public void setDataAggiornamentoConsenso(Date dataAggiornamentoConsenso) {
		this.dataAggiornamentoConsenso = dataAggiornamentoConsenso;
	}

	public Integer getIdProfessione() {
		return idProfessione;
	}

	public void setIdProfessione(Integer idProfessione) {
		this.idProfessione = idProfessione;
	}

	public Integer getIdTitoloStudio() {
		return idTitoloStudio;
	}

	public void setIdTitoloStudio(Integer idTitoloStudio) {
		this.idTitoloStudio = idTitoloStudio;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
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
