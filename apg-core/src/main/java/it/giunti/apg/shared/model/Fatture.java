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
@Table(name = "fatture")
public class Fatture extends BaseEntity {
	private static final long serialVersionUID = 4887540667302449135L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
	@Column(name = "id_anagrafica", nullable = false)
    private Integer idAnagrafica;
	@Basic(optional = false)
	@Column(name = "id_societa", nullable = false, length=4)
    private String idSocieta;
	@Column(name = "id_periodico")
    private Integer idPeriodico;
	@Column(name = "id_istanza_abbonamento")
    private Integer idIstanzaAbbonamento;
	@Column(name = "id_nota_credito_rimborso")
    private Integer idNotaCreditoRimborso;
	@Column(name = "id_nota_credito_storno")
    private Integer idNotaCreditoStorno;
	@Column(name = "id_nota_credito_rimborso_resto")
    private Integer idNotaCreditoRimborsoResto;
	@Column(name = "id_nota_credito_storno_resto")
    private Integer idNotaCreditoStornoResto;
	@Basic(optional = false)
	@Column(name = "id_tipo_documento", nullable = false, length = 4)
	private String idTipoDocumento;
    @Basic(optional = false)
    @Column(name = "data_fattura", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataFattura;
	@Basic(optional = false)
	@Column(name = "totale_finale", nullable = false, precision = 9, scale = 2)
	private Double totaleFinale;
	@Basic(optional = false)
	@Column(name = "totale_iva", nullable = false, precision = 9, scale = 2)
	private Double totaleIva;
	@Basic(optional = false)
	@Column(name = "totale_imponibile", nullable = false, precision = 9, scale = 2)
	private Double totaleImponibile;
	@Basic(optional = false)
	@Column(name = "tipo_iva", nullable = false, length = 4)
	private String tipoIva;
	@Basic(optional = false)
    @Column(name = "numero_fattura", nullable = false, length = 64)
    private String numeroFattura;
	@Column(name = "importo_resto", precision = 9, scale = 2)
	private Double importoResto;
    @Column(name = "data_email")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmail;
    @Basic(optional = false)
    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreazione;
    @Column(name = "data_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModifica;
	@Column(name = "id_fattura_stampa")
    private Integer idFatturaStampa;
	@Basic(optional = false)
	@Column(name = "pubblica", nullable = false)
	private boolean pubblica = true;
	
	
    public Fatture() {
    }

    public Fatture(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public String getIdSocieta() {
		return idSocieta;
	}

	public void setIdSocieta(String idSocieta) {
		this.idSocieta = idSocieta;
	}

	public Integer getIdPeriodico() {
		return idPeriodico;
	}

	public void setIdPeriodico(Integer idPeriodico) {
		this.idPeriodico = idPeriodico;
	}

	public Integer getIdIstanzaAbbonamento() {
		return idIstanzaAbbonamento;
	}

	public void setIdIstanzaAbbonamento(Integer idIstanza) {
		this.idIstanzaAbbonamento = idIstanza;
	}

	public Date getDataFattura() {
		return dataFattura;
	}

	public void setDataFattura(Date dataFattura) {
		this.dataFattura = dataFattura;
	}

	public Double getTotaleFinale() {
		return totaleFinale;
	}

	public void setTotaleFinale(Double totaleFinale) {
		this.totaleFinale = totaleFinale;
	}

	public Double getTotaleIva() {
		return totaleIva;
	}

	public void setTotaleIva(Double totaleIva) {
		this.totaleIva = totaleIva;
	}

	public Double getTotaleImponibile() {
		return totaleImponibile;
	}

	public void setTotaleImponibile(Double totaleImponibile) {
		this.totaleImponibile = totaleImponibile;
	}

	public String getNumeroFattura() {
		return numeroFattura;
	}

	public void setNumeroFattura(String numeroFattura) {
		this.numeroFattura = numeroFattura;
	}

	public Date getDataEmail() {
		return dataEmail;
	}

	public void setDataEmail(Date dataEmail) {
		this.dataEmail = dataEmail;
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
	
	public Integer getIdFatturaStampa() {
		return idFatturaStampa;
	}

	public void setIdFatturaStampa(Integer idFatturaStampa) {
		this.idFatturaStampa = idFatturaStampa;
	}

	public String getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(String idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(String tipoIva) {
		this.tipoIva = tipoIva;
	}

	public Integer getIdNotaCreditoRimborso() {
		return idNotaCreditoRimborso;
	}

	public void setIdNotaCreditoRimborso(Integer idNotaCreditoRimborso) {
		this.idNotaCreditoRimborso = idNotaCreditoRimborso;
	}

	public Integer getIdNotaCreditoStorno() {
		return idNotaCreditoStorno;
	}

	public void setIdNotaCreditoStorno(Integer idNotaCreditoStorno) {
		this.idNotaCreditoStorno = idNotaCreditoStorno;
	}

	public Integer getIdNotaCreditoRimborsoResto() {
		return idNotaCreditoRimborsoResto;
	}

	public void setIdNotaCreditoRimborsoResto(Integer idNotaCreditoRimborsoResto) {
		this.idNotaCreditoRimborsoResto = idNotaCreditoRimborsoResto;
	}

	public Integer getIdNotaCreditoStornoResto() {
		return idNotaCreditoStornoResto;
	}

	public void setIdNotaCreditoStornoResto(Integer idNotaCreditoStornoResto) {
		this.idNotaCreditoStornoResto = idNotaCreditoStornoResto;
	}

	public Double getImportoResto() {
		return importoResto;
	}

	public void setImportoResto(Double importoResto) {
		this.importoResto = importoResto;
	}

	public boolean getPubblica() {
		return pubblica;
	}

	public void setPubblica(boolean pubblica) {
		this.pubblica = pubblica;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Fatture)) {
            return false;
        }
        Fatture other = (Fatture) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Fatture[id=" + id + "]";
    }

}
