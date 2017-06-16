/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

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
import javax.persistence.Transient;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "modelli_bollettini")
public class ModelliBollettini extends BaseEntity {
	private static final long serialVersionUID = -293878890206248091L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
    @Column(name = "predefinito_periodico", nullable = false)
    private boolean predefinitoPeriodico;
	@Basic(optional = false)
    @Column(name = "descr", nullable = false, length = 256)
    private String descr;
    @Basic(optional = false)
    @Column(name = "codice_modello", nullable = false, length = 4)
    private String codiceModello;
    @Basic(optional = false)
    @Column(name = "autorizzazione", nullable = false, length = 256)
    private String autorizzazione;
    @Basic(optional = false)
    @Column(name = "report_file_path", nullable = false, length = 256)
    private String reportFilePath;
    @Basic(optional = false)
    @Column(name = "logo_vertical_path", nullable = false, length = 256)
    private String logoVerticalPath;
    @Basic(optional = false)
    @Column(name = "logo_small_path", nullable = false, length = 256)
    private String logoSmallPath;
    @Column(name = "testo_bandella", length = 4096)
    private String testoBandella;
    
    @Transient
    private String idPeriodicoT;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private Periodici periodico;
    
    public ModelliBollettini() {
    }

    public ModelliBollettini(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public boolean getPredefinitoPeriodico() {
		return predefinitoPeriodico;
	}

	public void setPredefinitoPeriodico(boolean predefinitoPeriodico) {
		this.predefinitoPeriodico = predefinitoPeriodico;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getCodiceModello() {
		return codiceModello;
	}

	public void setCodiceModello(String codiceModello) {
		this.codiceModello = codiceModello;
	}

	public String getAutorizzazione() {
		return autorizzazione;
	}

	public void setAutorizzazione(String autorizzazione) {
		this.autorizzazione = autorizzazione;
	}

	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public String getLogoVerticalPath() {
		return logoVerticalPath;
	}

	public void setLogoVerticalPath(String logoVerticalPath) {
		this.logoVerticalPath = logoVerticalPath;
	}

	public String getLogoSmallPath() {
		return logoSmallPath;
	}

	public void setLogoSmallPath(String logoSmallPath) {
		this.logoSmallPath = logoSmallPath;
	}

	public String getTestoBandella() {
		return testoBandella;
	}

	public void setTestoBandella(String testoBandella) {
		this.testoBandella = testoBandella;
	}
	
	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ModelliBollettini)) {
            return false;
        }
        ModelliBollettini other = (ModelliBollettini) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModelliBollettini[id=" + id + "] "+descr;
    }

}
