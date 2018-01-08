/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTMONEDA")
@XmlRootElement
public class Moneda implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private int id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "DENOMINACIONISO")
    private String denominacionIso;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "SIMBOLO")
    private String simbolo;   
    
    @JoinColumn(name = "ESTADOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estado;
    
    public Moneda() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDenominacionIso() {
        return denominacionIso;
    }

    public void setDenominacionIso(String denominacionIso) {
        this.denominacionIso = denominacionIso;
    }
    
     public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    
     
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Moneda)) {
            return false;
        }
        Moneda other = (Moneda) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 9;
        hash = 31 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Moneda[ id=" + id + " ]";
    }
    
}
