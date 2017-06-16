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
@Table(name = "stat_abbonati")
public class StatAbbonati extends BaseEntity {
	private static final long serialVersionUID = 1004109319707303430L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
	@Column(name = "data_creazione", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCreazione;
    @Column(name = "tiratura")
    private Integer tiratura;
    @Column(name = "nuovi")
    private Integer nuovi;
//    @Column(name = "riattivati")
//    private Integer riattivati;
    @Column(name = "disdette")
    private Integer disdette;
//    @Column(name = "in_sospeso")
//    private Integer inSospeso;
    @Column(name = "omaggi")
    private Integer omaggi;
    @Column(name = "pagati")
    private Integer pagati;
    @Column(name = "morosi_anno_prec")
    private Integer morosiAnnoPrec;
    @Column(name = "morosi_attuali")
    private Integer morosiAttuali;
//    @Column(name = "a_carico_agenti")
//    private Integer aCaricoAgenti;
//    @Column(name = "procurati_agenti")
//    private Integer procuratiAgenti;
    @JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Periodici periodico;
    
    public StatAbbonati() {
    }

    public StatAbbonati(Integer id) {
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

	public Integer getTiratura() {
		return tiratura;
	}

	public void setTiratura(Integer tiratura) {
		this.tiratura = tiratura;
	}

	public Integer getNuovi() {
		return nuovi;
	}

	public void setNuovi(Integer nuovi) {
		this.nuovi = nuovi;
	}

	public Integer getDisdette() {
		return disdette;
	}

	public void setDisdette(Integer disdette) {
		this.disdette = disdette;
	}

	public Integer getOmaggi() {
		return omaggi;
	}

	public void setOmaggi(Integer omaggi) {
		this.omaggi = omaggi;
	}

	public Integer getPagati() {
		return pagati;
	}

	public void setPagati(Integer pagati) {
		this.pagati = pagati;
	}

	public Integer getMorosiAnnoPrec() {
		return morosiAnnoPrec;
	}

	public void setMorosiAnnoPrec(Integer morosiAnnoPrec) {
		this.morosiAnnoPrec = morosiAnnoPrec;
	}

	public Integer getMorosiAttuali() {
		return morosiAttuali;
	}

	public void setMorosiAttuali(Integer morosiAttuali) {
		this.morosiAttuali = morosiAttuali;
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
        if (!(object instanceof StatAbbonati)) {
            return false;
        }
        StatAbbonati other = (StatAbbonati) object;
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
