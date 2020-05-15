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
@Table(name = "indirizzi")
public class Indirizzi extends BaseEntity {
	private static final long serialVersionUID = 3157538677600921601L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "cognome_ragione_sociale", length = 64)
    private String cognomeRagioneSociale;
    @Column(name = "nome", length = 32)
    private String nome;
    @Column(name = "titolo", length = 32)
    private String titolo;
    @Column(name = "indirizzo", length = 128)
    private String indirizzo;
    @Column(name = "cap", length = 8)
    private String cap;
    @Column(name = "localita", length = 64)
    private String localita;
    @Column(name = "presso", length = 64)
    private String presso;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @Column(name = "id_provincia", length = 4)
    private String provincia;
    @JoinColumn(name = "id_nazione", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Nazioni nazione;
    @Transient
    private String idNazioneT;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;

    public Indirizzi() {
    }

    public Indirizzi(Integer id) {
        this.id = id;
    }

    public Indirizzi(Integer id, String indirizzo) {
        this.id = id;
        this.indirizzo = indirizzo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCognomeRagioneSociale() {
		return cognomeRagioneSociale;
	}

	public void setCognomeRagioneSociale(String cognomeRagioneSociale) {
		this.cognomeRagioneSociale = cognomeRagioneSociale;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getLocalita() {
        return localita;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public String getPresso() {
        return presso;
    }

    public void setPresso(String presso) {
        this.presso = presso;
    }

    public Date getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Date dataModifica) {
        this.dataModifica = dataModifica;
    }

	public Nazioni getNazione() {
		return nazione;
	}

	public void setNazione(Nazioni nazione) {
		this.nazione = nazione;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getIdNazioneT() {
		return idNazioneT;
	}

	public void setIdNazioneT(String idNazione) {
		this.idNazioneT = idNazione;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Indirizzi)) {
            return false;
        }
        Indirizzi other = (Indirizzi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Indirizzi[id=" + id + "]";
    }


}
