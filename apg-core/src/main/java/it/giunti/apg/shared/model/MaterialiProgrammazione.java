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

@Entity
@Table(name = "materiali_programmazione")
public class MaterialiProgrammazione extends BaseEntity {
	private static final long serialVersionUID = 5233710123877075773L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "data_nominale", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataNominale;
    @Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
    @Basic(optional = false)
	@Column(name = "comunicazioni_inviate", nullable = false)
	private boolean comunicazioniInviate;
    
    @JoinColumn(name = "id_materiale", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Materiali materiale;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Periodici periodico6;
    @JoinColumn(name = "id_opzione", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Opzioni opzione;

	@Transient
    private String materialeCmT;
	@Transient
    private String idPeriodicoT;
    @Transient
    private String idOpzioneT;
    
	public MaterialiProgrammazione() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataNominale() {
		return dataNominale;
	}

	public void setDataNominale(Date dataNominale) {
		this.dataNominale = dataNominale;
	}

	public Date getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(Date dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}

	public boolean getComunicazioniInviate() {
		return comunicazioniInviate;
	}

	public void setComunicazioniInviate(boolean comunicazioniInviate) {
		this.comunicazioniInviate = comunicazioniInviate;
	}

	public Materiali getMateriale() {
		return materiale;
	}

	public void setMateriale(Materiali materiale) {
		this.materiale = materiale;
	}

	public Periodici getPeriodico6() {
		return periodico6;
	}

	public void setPeriodico6(Periodici periodico) {
		this.periodico6 = periodico;
	}

	public Opzioni getOpzione() {
		return opzione;
	}

	public void setOpzione(Opzioni opzione) {
		this.opzione = opzione;
	}

	
	public String getMaterialeCmT() {
		return materialeCmT;
	}

	public void setMaterialeCmT(String materialeCmT) {
		this.materialeCmT = materialeCmT;
	}

	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}

	public String getIdOpzioneT() {
		return idOpzioneT;
	}

	public void setIdOpzioneT(String idOpzioneT) {
		this.idOpzioneT = idOpzioneT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaterialiProgrammazione)) {
            return false;
        }
        MaterialiProgrammazione other = (MaterialiProgrammazione) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "MaterialiProgrammazione[id=" + id + "] ";
        return result;
    }

}
