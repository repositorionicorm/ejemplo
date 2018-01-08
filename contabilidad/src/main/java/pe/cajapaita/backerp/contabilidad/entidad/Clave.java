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
 * @author dev-out-02
 */
@Entity
@Table(name = "CLAVE")
@XmlRootElement
public class Clave implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "ENTIDAD")
    private String entidad;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "SIGUIENTEID")
    private int siguienteid;
    
    @JoinColumn(name = "MODULOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Modulo moduloid;

    public Clave() {
    }

    public Clave(String entidad) {
        this.entidad = entidad;
    }

    public Clave(String entidad, int siguienteid) {
        this.entidad = entidad;
        this.siguienteid = siguienteid;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public int getSiguienteid() {
        return siguienteid;
    }

    public void setSiguienteid(int siguienteid) {
        this.siguienteid = siguienteid;
    }

    public Modulo getModuloid() {
        return moduloid;
    }

    public void setModuloid(Modulo moduloid) {
        this.moduloid = moduloid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (entidad != null ? entidad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clave)) {
            return false;
        }
        Clave other = (Clave) object;
        if ((this.entidad == null && other.entidad != null) || (this.entidad != null && !this.entidad.equals(other.entidad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.configuracion.Clave[ entidad=" + entidad + " ]";
    }
    
}
