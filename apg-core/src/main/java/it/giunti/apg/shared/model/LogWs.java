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
@Table(name = "log_ws")
public class LogWs extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1204140319777300330L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "service", nullable = false, length = 32)
    private String service;
    @Basic(optional = false)
    @Column(name = "operation", nullable = false, length = 64)
    private String operation;
    @Basic(optional = false)
    @Column(name = "parameters", nullable = false, length = 1024)
    private String parameters;
    @Column(name = "result", length = 256)
    private String result;
	@Basic(optional = false)
	@Column(name = "log_datetime", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date logDatetime;
    
    public LogWs() {
    }

    public LogWs(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		if (result != null) {
			if (result.length() > 255) {
				result = result.substring(0, 255);
			}
		}
		this.result = result;
	}

	public Date getLogDatetime() {
		return logDatetime;
	}

	public void setLogDatetime(Date logDatetime) {
		this.logDatetime = logDatetime;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof LogWs)) {
            return false;
        }
        LogWs other = (LogWs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LogWs[id=" + id + "]";
    }

}
