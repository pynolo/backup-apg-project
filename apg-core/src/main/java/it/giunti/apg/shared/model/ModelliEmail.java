/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "modelli_email")
public class ModelliEmail extends BaseEntity {
	private static final long serialVersionUID = -292878810206048097L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "descr", nullable = false, length = 256)
    private String descr;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "nome_mittente", nullable = false, length = 256)
    private String nomeMittente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "oggetto", nullable = false, length = 256)
    private String oggetto;
	//@Lob
	//@Size(max = 65535)
	//@Column(name = "testo_semplice", length = 65535)
	//private String testoSemplice;
    @Lob
    @Size(max = 16777215)
    @Column(name = "testo_html", length = 16777215)
    private String testoHtml;

    public ModelliEmail() {
    }

    public ModelliEmail(Integer id) {
        this.id = id;
    }

    public ModelliEmail(Integer id, String descr, String nomeMittente, String oggetto) {
        this.id = id;
        this.descr = descr;
        this.nomeMittente = nomeMittente;
        this.oggetto = oggetto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getNomeMittente() {
        return nomeMittente;
    }

    public void setNomeMittente(String nomeMittente) {
        this.nomeMittente = nomeMittente;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

	//public String getTestoSemplice() {
	//    return testoSemplice;
	//}
	//
	//public void setTestoSemplice(String testoSemplice) {
	//    this.testoSemplice = testoSemplice;
	//}

    public String getTestoHtml() {
        return testoHtml;
    }

    public void setTestoHtml(String testoHtml) {
        this.testoHtml = testoHtml;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ModelliEmail)) {
            return false;
        }
        ModelliEmail other = (ModelliEmail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModelliEmail[id=" + id + "] "+descr;
    }

}
