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
@Table(name = "opzioni")
public class Opzioni extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -818830600625650494L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	//@Basic(optional = false)
	//@Column(name = "codice_interno", nullable = false, length = 16)
	//private String codiceInterno;
	@Basic(optional = false)
    @Column(name = "uid", nullable = false, length = 16)
    private String uid;
    @Column(name = "tag", length = 256)
    private String tag;
    @Basic(optional = false)
    @Column(name = "nome", nullable = false, length = 64)
    private String nome;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
    @Column(name = "data_fine")
    @Temporal(TemporalType.DATE)
    private Date dataFine;
    @Basic(optional = false)
    @Column(name = "prezzo", nullable = false, precision = 9, scale = 2)
    private Double prezzo;
    @Column(name = "note", length = 255)
    private String note;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Basic(optional = false)
	@Column(name = "cartaceo", nullable = false)
	private boolean cartaceo;
	@Basic(optional = false)
	@Column(name = "digitale", nullable = false)
	private boolean digitale;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Periodici periodico;
    @JoinColumn(name = "id_aliquota_iva", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AliquoteIva aliquotaIva;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
    
	@Transient
	private Integer idPeriodicoT;
	@Transient
	private Integer idAliquotaIvaT;
	
    public Opzioni() {
    }

    public Opzioni(Integer id) {
        this.id = id;
    }

    public Opzioni(Integer id, String nome, Date dataInizio, Double prezzo) {
        this.id = id;
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.prezzo = prezzo;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public Periodici getPeriodico() {
        return periodico;
    }

    public void setPeriodico(Periodici periodico) {
        this.periodico = periodico;
    }

	public AliquoteIva getAliquotaIva() {
		return aliquotaIva;
	}

	public void setAliquotaIva(AliquoteIva aliquotaIva) {
		this.aliquotaIva = aliquotaIva;
	}

	public Integer getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(Integer idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}

	public Integer getIdAliquotaIvaT() {
		return idAliquotaIvaT;
	}

	public void setIdAliquotaIvaT(Integer idAliquotaIvaT) {
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
        if (!(object instanceof Opzioni)) {
            return false;
        }
        Opzioni other = (Opzioni) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    	String s = "Opzioni[id=" + id + "] ";
    	if (nome != null) s += nome;
        return s;
    }

}
