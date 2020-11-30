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

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "materiali_listini")
public class MaterialiListini extends BaseEntity {
	private static final long serialVersionUID = 2082192059768219131L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
    @Column(name = "id_tipo_destinatario", length = 4, nullable = false)
    private String idTipoDestinatario;
	@Basic(optional = false)
	@Column(name = "giorno_limite_pagamento", nullable = false)
	private Integer giornoLimitePagamento;
	@Basic(optional = false)
	@Column(name = "mese_limite_pagamento", nullable = false)
	private Integer meseLimitePagamento;
	@Column(name = "data_estrazione")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstrazione;
	
    @JoinColumn(name = "id_articolo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Articoli6 articolo6;//TODO remove
    @JoinColumn(name = "id_materiale", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Materiali materiale;
    @JoinColumn(name = "id_listino", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Listini listino;
    
	@Transient
    private String materialeCmT;
    
    public MaterialiListini() {
    }

    public MaterialiListini(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdTipoDestinatario() {
		return idTipoDestinatario;
	}

	public void setIdTipoDestinatario(String idTipoDestinatario) {
		this.idTipoDestinatario = idTipoDestinatario;
	}

	public Integer getGiornoLimitePagamento() {
		return giornoLimitePagamento;
	}

	public void setGiornoLimitePagamento(Integer giornoLimitePagamento) {
		this.giornoLimitePagamento = giornoLimitePagamento;
	}

	public Integer getMeseLimitePagamento() {
		return meseLimitePagamento;
	}

	public void setMeseLimitePagamento(Integer meseLimitePagamento) {
		this.meseLimitePagamento = meseLimitePagamento;
	}

	public Date getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(Date dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}

	public Articoli6 getArticolo6() {
		return articolo6;
	}

	public void setArticolo6(Articoli6 articolo) {
		this.articolo6 = articolo;
	}

	public Materiali getMateriale() {
		return materiale;
	}

	public void setMateriale(Materiali materiale) {
		this.materiale = materiale;
	}

	public Listini getListino() {
		return listino;
	}

	public void setListino(Listini listino) {
		this.listino = listino;
	}

	public String getMaterialeCmT() {
		return materialeCmT;
	}

	public void setMaterialeCmT(String materialeCmT) {
		this.materialeCmT = materialeCmT;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaterialiListini)) {
            return false;
        }
        MaterialiListini other = (MaterialiListini) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "MaterialiListini[id=" + id + "] ";
        return result;
    }

}
