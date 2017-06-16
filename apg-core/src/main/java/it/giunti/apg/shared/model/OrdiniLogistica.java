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
@Table(name = "ordini_logistica")
public class OrdiniLogistica extends BaseEntity {
	private static final long serialVersionUID = -106978509914196807L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "numero_ordine", nullable=false, length = 32)
	private String numeroOrdine;
	@Basic(optional = false)
	@Column(name = "id_anagrafica", nullable=false)
	private Integer idAnagrafica;
	@Column(name = "note", length = 256)
	private String note;
    @Column(name = "data_inserimento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInserimento;
    @Column(name = "data_rifiuto")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRifiuto;
    @Column(name = "data_chiusura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataChiusura;
    
	public OrdiniLogistica() {
	}

	public OrdiniLogistica(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumeroOrdine() {
		return numeroOrdine;
	}

	public void setNumeroOrdine(String numeroOrdine) {
		this.numeroOrdine = numeroOrdine;
	}

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getDataRifiuto() {
		return dataRifiuto;
	}

	public void setDataRifiuto(Date dataRifiuto) {
		this.dataRifiuto = dataRifiuto;
	}

	public Date getDataChiusura() {
		return dataChiusura;
	}

	public void setDataChiusura(Date dataChiusura) {
		this.dataChiusura = dataChiusura;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof OrdiniLogistica)) {
			return false;
		}
		OrdiniLogistica other = (OrdiniLogistica) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OrdiniLogistica[id=" + id + "] num="+numeroOrdine;
	}


}
