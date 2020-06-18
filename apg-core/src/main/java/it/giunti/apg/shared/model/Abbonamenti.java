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
@Table(name = "abbonamenti")
public class Abbonamenti extends BaseEntity {
	private static final long serialVersionUID = -106478567904196819L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "codice_abbonamento", length = 16, nullable = false)
	private String codiceAbbonamento;
	@Basic(optional = false)
	@Column(name = "data_creazione", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataCreazione;

	@Column(name = "data_modifica")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataModifica;
	@Basic(optional = false)
	@Column(name = "id_tipo_spedizione", length = 4, nullable=false)
	private String idTipoSpedizione;
	@JoinColumn(name = "id_periodico", referencedColumnName = "id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Periodici periodico;
	@Column(name = "id_utente", length = 32, nullable = false)
	private String idUtente;
	
	@Transient
	private String idPeriodicoT;


	//    @OneToMany(mappedBy = "idAbbonamento", fetch = FetchType.EAGER)
	//    private List<Pagamenti> pagamentiList;
	//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAbbonamento", fetch = FetchType.EAGER)
	//    private List<MaterialiSpedizioni> evasioniFascicoliList;


	public Abbonamenti() {
	}

	public Abbonamenti(Integer id) {
		this.id = id;
	}

	public Abbonamenti(Integer id, Date dataCreazione) {
		this.id = id;
		this.dataCreazione = dataCreazione;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodiceAbbonamento() {
		return codiceAbbonamento;
	}

	public void setCodiceAbbonamento(String codiceAbbonamento) {
		this.codiceAbbonamento = codiceAbbonamento;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	
	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Periodici getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodici periodico) {
		this.periodico = periodico;
	}

	public String getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	public String getIdPeriodicoT() {
		return idPeriodicoT;
	}

	public void setIdPeriodicoT(String idPeriodicoT) {
		this.idPeriodicoT = idPeriodicoT;
	}

	public String getIdTipoSpedizione() {
		return idTipoSpedizione;
	}

	public void setIdTipoSpedizione(String idTipoSpedizione) {
		this.idTipoSpedizione = idTipoSpedizione;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Abbonamenti)) {
			return false;
		}
		Abbonamenti other = (Abbonamenti) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Abbonamenti[id=" + id + "] "+codiceAbbonamento;
	}


}
