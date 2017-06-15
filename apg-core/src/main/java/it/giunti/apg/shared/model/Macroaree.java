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
@Table(name = "macroaree")
public class Macroaree extends BaseEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 7698870564009540154L;
	@Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nome", nullable = false, length = 32)
    private String nome;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMacroarea", fetch = FetchType.EAGER)
//    private List<Listini> tipiAbbonamentoListinoList;
//    @OneToMany(mappedBy = "idMacroarea", fetch = FetchType.EAGER)
//    private List<Nazioni> nazioniList;

    public Macroaree() {
    }

    public Macroaree(Integer id) {
        this.id = id;
    }

    public Macroaree(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Macroaree)) {
            return false;
        }
        Macroaree other = (Macroaree) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Macroaree[id=" + id + "]";
    }

}
