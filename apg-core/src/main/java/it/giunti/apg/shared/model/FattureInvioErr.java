/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "fatture_invio_err")
public class FattureInvioErr extends BaseEntity {
	private static final long serialVersionUID = -3361114312797267140L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "id_invio", nullable = false)
    private Integer idInvio;
    @Basic(optional = false)
    @Column(name = "riga_invio", nullable = false)
    private Integer rigaInvio;
	@Column(name = "err_table", length = 8)
	private String errTable;
	@Column(name = "err_field", length = 16)
	private String errField;
	@Column(name = "err_message", length = 256)
	private String errMessage;
	@Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreazione;
	
    public FattureInvioErr() {
    }

    public FattureInvioErr(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getIdInvio() {
		return idInvio;
	}

	public void setIdInvio(Integer idInvio) {
		this.idInvio = idInvio;
	}

	public Integer getRigaInvio() {
		return rigaInvio;
	}

	public void setRigaInvio(Integer rigaInvio) {
		this.rigaInvio = rigaInvio;
	}

	public String getErrTable() {
		return errTable;
	}

	public void setErrTable(String errTable) {
		this.errTable = errTable;
	}

	public String getErrField() {
		return errField;
	}

	public void setErrField(String errField) {
		this.errField = errField;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FattureInvioErr)) {
            return false;
        }
        FattureInvioErr other = (FattureInvioErr) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FileUpload[id=" + id + "]";
    }

}
