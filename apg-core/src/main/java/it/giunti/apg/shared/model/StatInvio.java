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
@Table(name = "stat_invio")
public class StatInvio extends BaseEntity {
	private static final long serialVersionUID = 1004777319707303437L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
	@Column(name = "data_creazione", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataCreazione;
    @Column(name = "quantita")
    private Integer quantita;
    @Column(name = "id_materiale_programmazione")
    private Integer idMaterialeProgrammazione;
    @JoinColumn(name = "id_fascicolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Fascicoli6 fascicolo;//TODO remove
    @JoinColumn(name = "id_tipo_abbonamento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private TipiAbbonamento tipoAbbonamento;
    
    public StatInvio() {
    }

    public StatInvio(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Integer getQuantita() {
		return quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}
	
	public Integer getIdMaterialeProgrammazione() {
		return idMaterialeProgrammazione;
	}

	public void setIdMaterialeProgrammazione(Integer idMaterialeProgrammazione) {
		this.idMaterialeProgrammazione = idMaterialeProgrammazione;
	}

	public Fascicoli6 getFascicolo6() {
		return fascicolo;
	}

	public void setFascicolo6(Fascicoli6 fascicolo) {
		this.fascicolo = fascicolo;
	}

	public TipiAbbonamento getTipoAbbonamento() {
		return tipoAbbonamento;
	}

	public void setTipoAbbonamento(TipiAbbonamento tipoAbbonamento) {
		this.tipoAbbonamento = tipoAbbonamento;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StatInvio)) {
            return false;
        }
        StatInvio other = (StatInvio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rapporti[id=" + id + "]";
    }

}
