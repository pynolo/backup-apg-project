/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "contatori")
public class Contatori extends BaseEntity {
	private static final long serialVersionUID = -136078507914196899L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "ckey", nullable=false, length = 64)
	private String ckey;
	@Basic(optional = false)
	@Column(name = "progressivo", nullable=false)
	private Integer progressivo;
	@Column(name = "temp_progressivo")
	private Integer tempProgressivo;
	@Basic(optional = false)
	@Column(name = "locked", nullable = false)
	private boolean locked;

	public Contatori() {
	}

	public Contatori(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCkey() {
		return ckey;
	}

	public void setCkey(String ckey) {
		this.ckey = ckey;
	}

	public Integer getProgressivo() {
		return progressivo;
	}

	public void setProgressivo(Integer progressivo) {
		this.progressivo = progressivo;
	}
	
	public Integer getTempProgressivo() {
		return tempProgressivo;
	}

	public void setTempProgressivo(Integer tempProgressivo) {
		this.tempProgressivo = tempProgressivo;
	}
	
	public boolean getLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Contatori)) {
			return false;
		}
		Contatori other = (Contatori) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Contatori[key=" + ckey + "] "+progressivo;
	}


}
