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
@Table(name = "CNTGRUPO")
@XmlRootElement
public class Grupo implements Serializable {

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

    @JoinColumn(name = "REPORTEID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Reporte reporte;

    @Basic(optional = false)
    @Size(min = 1, max = 50)
    @Column(name = "RANGO")
    private String rango;

    public Grupo() {
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

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

//    public String getCodigoSoles() {
//        return codigoSoles;
//    }
//
//    public void setCodigoSoles(String codigoSoles) {
//        this.codigoSoles = codigoSoles;
//    }
//
//    public String getCodigoDolares() {
//        return codigoDolares;
//    }
//
//    public void setCodigoDolares(String codigoDolares) {
//        this.codigoDolares = codigoDolares;
//    }
//    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grupo)) {
            return false;
        }
        Grupo other = (Grupo) object;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Grupo[ id=" + id + " ]";
    }

}
