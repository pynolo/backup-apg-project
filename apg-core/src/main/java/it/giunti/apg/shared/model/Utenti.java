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
@Table(name = "utenti")
public class Utenti extends BaseEntity {
	private static final long serialVersionUID = 3785649454194733540L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, length = 32)
    private String id;
	@Transient
	private String newId;
	//@Basic(optional = false)
	//@Column(name = "password", nullable = false, length = 32)
	//private String password;
	@Basic(optional = false)
	@Column(name = "aziendale", nullable = false)
	private boolean aziendale;
    @Column(name = "descrizione", length = 255)
    private String descrizione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
    @JoinColumn(name = "id_ruolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Ruoli ruolo;
    @Column(name = "periodici_uid_restriction", length = 256)
    private String periodiciUidRestriction;
    @Column(name = "heartbeat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date heartbeat;

    @Transient
    private String idRuoloT;

    public Utenti() {
    }

    public Utenti(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public String getDescrizione() {
        return descrizione;
    }

    public boolean getAziendale() {
		return aziendale;
	}

	public void setAziendale(boolean aziendale) {
		this.aziendale = aziendale;
	}

	public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Date dataModifica) {
        this.dataModifica = dataModifica;
    }

    public Ruoli getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruoli ruolo) {
        this.ruolo = ruolo;
    }

    public String getNewId() {
		return newId;
	}

	public void setNewId(String newId) {
		this.newId = newId;
	}

	public String getPeriodiciUidRestriction() {
		return periodiciUidRestriction;
	}

	public void setPeriodiciUidRestriction(String periodiciUidRestriction) {
		this.periodiciUidRestriction = periodiciUidRestriction;
	}

	public Date getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Date heartbeat) {
		this.heartbeat = heartbeat;
	}

	public String getIdRuoloT() {
		return idRuoloT;
	}

	public void setIdRuoloT(String idRuoloT) {
		this.idRuoloT = idRuoloT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Utenti)) {
            return false;
        }
        Utenti other = (Utenti) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Utenti[id=" + id + "]";
    }

}
