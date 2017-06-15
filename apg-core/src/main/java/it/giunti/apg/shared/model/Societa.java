/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "societa")
public class Societa extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1691870564019510154L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private String id;
    @Basic(optional = false)
    @Column(name = "nome", nullable = false, length = 256)
    private String nome;
    @Basic(optional = false)
    @Column(name = "testo_fattura_1", nullable = false, length = 1024)
    private String testoFattura1;
    @Basic(optional = false)
    @Column(name = "testo_fattura_2", nullable = false, length = 1024)
    private String testoFattura2;
    @Basic(optional = false)
    @Column(name = "codice_fiscale", nullable = false, length = 32)
    private String codiceFiscale;
    @Basic(optional = false)
    @Column(name = "codice_societa", nullable = false, length = 32)
    private String codiceSocieta;
    @Basic(optional = false)
    @Column(name = "partita_iva", nullable = false, length = 32)
    private String partitaIva;
    @Column(name = "prefisso_fatture", length = 8)
    private String prefissoFatture;
    
    public Societa() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getTestoFattura1() {
		return testoFattura1;
	}

	public void setTestoFattura1(String testoFattura1) {
		this.testoFattura1 = testoFattura1;
	}

	public String getTestoFattura2() {
		return testoFattura2;
	}

	public void setTestoFattura2(String testoFattura2) {
		this.testoFattura2 = testoFattura2;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCodiceSocieta() {
		return codiceSocieta;
	}

	public void setCodiceSocieta(String codiceSocieta) {
		this.codiceSocieta = codiceSocieta;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getPrefissoFatture() {
		return prefissoFatture;
	}

	public void setPrefissoFatture(String prefissoFatture) {
		this.prefissoFatture = prefissoFatture;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Societa)) {
            return false;
        }
        Societa other = (Societa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Societa[id=" + id + "]";
    }

}
