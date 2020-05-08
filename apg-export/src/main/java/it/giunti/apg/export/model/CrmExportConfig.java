package it.giunti.apg.export.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "crm_export_config")
public class CrmExportConfig {
	
	@Id
	@Basic(optional = false)
	@Column(name = "id", length = 32, nullable = false)
	private String id;
	@Basic(optional = false)
	@Column(name = "val", length = 64, nullable = false)
	private String val;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CrmExportConfig)) {
            return false;
        }
        CrmExportConfig other = (CrmExportConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "CrmExportConfig[id=" + id + "] ";
        return s;
    }

}
