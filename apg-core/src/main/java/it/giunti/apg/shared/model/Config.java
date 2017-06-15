/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "config")
public class Config extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -153078501000006839L;
	@Id
	@Basic(optional = false)
	@Column(name = "id", nullable = false, length = 64)
	private String id;
	@Basic(optional = false)
	@Column(name = "valore", nullable=false, length = 1024)
	private String valore;

	public Config() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Config)) {
			return false;
		}
		Config other = (Config) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Config[id=" + id + "]";
	}


}
