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
@Table(name = "CNTREPORTE")
@XmlRootElement
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private int id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Column(name = "TIPO")
    private int tipo;
    
    @JoinColumn(name = "MODULOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Reporte modulo;

    public Reporte() {
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

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Reporte getModulo() {
        return modulo;
    }

    public void setModulo(Reporte modulo) {
        this.modulo = modulo;
    }
    


    @Override
    public int hashCode() {
         int hash = 5;
        hash = 65 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
            // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reporte)) {
            return false;
        }
        Reporte other = (Reporte) object;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Reporte[ id=" + id + " ]";
    }
    
}
