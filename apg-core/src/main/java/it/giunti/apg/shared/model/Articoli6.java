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

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "articoli")
public class Articoli6 extends BaseEntity implements IPubblicazioni {
	private static final long serialVersionUID = -298878866265848096L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "codice_interno",length = 8)
    private String codiceInterno;
    @Basic(optional = false)
    @Column(name = "codice_meccanografico", nullable = false, length = 32)
    private String codiceMeccanografico;
    @Column(name = "autore", length = 64)
    private String autore;
    @Basic(optional = false)
    @Column(name = "titolo_numero", nullable = false, length = 64)
    private String titoloNumero;
    @Basic(optional = false)
    @Column(name = "data_inizio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInizio;
    @Column(name = "data_fine")
    @Temporal(TemporalType.DATE)
    private Date dataFine;
	//@Column(name="giorno_limite")
	//private Integer giornoLimite;
	//@Column(name="mese_limite")
	//private Integer meseLimite;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
	@Basic(optional = false)
	@Column(name = "in_attesa", nullable = false)
	private boolean inAttesa;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Periodici periodico;
	//@Basic(optional = false)
	//@Column(name = "id_societa", nullable = false, length = 4)
	//private String idSocieta;
	//@Basic(optional = false)
	//@Column(name = "id_tipo_articolo", nullable = false, length = 4)
	//private String idTipoArticolo;
    @Basic(optional = false)
	@Column(name = "cartaceo", nullable = false)
	private boolean cartaceo;
	@Basic(optional = false)
	@Column(name = "digitale", nullable = false)
	private boolean digitale;
	@Basic(optional = false)
	@Column(name = "id_tipo_anagrafica_sap", nullable = false, length = 4)
	private String idTipoAnagraficaSap;
	
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
    

    public Articoli6() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodiceInterno() {
		return codiceInterno;
	}

	public void setCodiceInterno(String codiceInterno) {
		this.codiceInterno = codiceInterno;
	}

	public String getCodiceMeccanografico() {
		return codiceMeccanografico;
	}

	public void setCodiceMeccanografico(String codiceMeccanografico) {
		this.codiceMeccanografico = codiceMeccanografico;
	}

	public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getTitoloNumero() {
        return titoloNumero;
    }

    public void setTitoloNumero(String titoloNumero) {
        this.titoloNumero = titoloNumero;
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

	//public Integer getGiornoLimite() {
	//	return giornoLimite;
	//}
	//
	//public void setGiornoLimite(Integer giornoLimite) {
	//	this.giornoLimite = giornoLimite;
	//}
	//
	//public Integer getMeseLimite() {
	//	return meseLimite;
	//}
	//
	//public void setMeseLimite(Integer meseLimite) {
	//	this.meseLimite = meseLimite;
	//}

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

	public boolean getInAttesa() {
		return inAttesa;
	}

	public void setInAttesa(boolean inAttesa) {
		this.inAttesa = inAttesa;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
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

	public String getIdTipoAnagraficaSap() {
		return idTipoAnagraficaSap;
	}

	public void setIdTipoAnagraficaSap(String idTipoAnagraficaSap) {
		this.idTipoAnagraficaSap = idTipoAnagraficaSap;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Articoli6)) {
            return false;
        }
        Articoli6 other = (Articoli6) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Articoli6[id=" + id + "] "+codiceMeccanografico;
    }


}
