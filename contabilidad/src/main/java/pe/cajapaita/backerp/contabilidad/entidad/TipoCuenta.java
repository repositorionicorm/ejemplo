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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTTIPOCUENTA")
@XmlRootElement
public class TipoCuenta implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private int id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "SIGNODEBE")
    private String signoDebe;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "SIGNOHABER")
    private String signoHaber;
    
    public TipoCuenta() {
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

    public String getSignoDebe() {
        return signoDebe;
    }

    public void setSignoDebe(String signoDebe) {
        this.signoDebe = signoDebe;
    }

    public String getSignoHaber() {
        return signoHaber;
    }

    public void setSignoHaber(String signoHaber) {
        this.signoHaber = signoHaber;
    }

    public TipoCuenta(Short id, String descripcion, String signoDebe, String signoHaber) {
        this.id = id;
        this.descripcion = descripcion;
        this.signoDebe = signoDebe;
        this.signoHaber = signoHaber;
    }
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoCuenta)) {
            return false;
        }
        TipoCuenta other = (TipoCuenta) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 4;
        hash = 47 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.TipoCuenta[ id=" + id + " ]";
    }
    
}
