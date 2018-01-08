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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dev-out-02
 */
@Entity
@Table(name = "CNTGRUPOENCAJE")
@XmlRootElement
public class GrupoEncaje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private int id;
    @Size(max = 10)
    @Column(name = "OPERACIONSOLES")
    private String operacionsoles;
    @Size(max = 10)
    @Column(name = "OPERACIONDOLARES")
    private String operaciondolares;
    
    @JoinColumn(name = "GRUPOID", referencedColumnName = "ID")
    @OneToOne(optional = false)
    private Grupo grupo;

    public GrupoEncaje() {
    }

    public GrupoEncaje(Short id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getOperacionsoles() {
        return operacionsoles;
    }

    public void setOperacionsoles(String operacionsoles) {
        this.operacionsoles = operacionsoles;
    }

    public String getOperaciondolares() {
        return operaciondolares;
    }

    public void setOperaciondolares(String operaciondolares) {
        this.operaciondolares = operaciondolares;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }


    @Override
    public int hashCode() {
       int hash = 6;
        hash = 86 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
          // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallegrupo)) {
            return false;
        }
        GrupoEncaje other = (GrupoEncaje) object;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.GrupoEncaje[ id=" + id + " ]";
    }
    
}
