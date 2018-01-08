/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "CNTDETALLEGRUPO")
@XmlRootElement
public class Detallegrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private int id;
    
    @JoinColumn(name = "GRUPOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Grupo grupo;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "CUENTA")
    private String cuenta;
    
    @Basic(optional = false)
    @Column(name = "OPERACION")
    private BigDecimal operacion;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "TIPOSALDO")
    private String tipoSaldo;

    public Detallegrupo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
    
   

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public BigDecimal getOperacion() {
        return operacion;
    }

    public void setOperacion(BigDecimal operacion) {
        this.operacion = operacion;
    }

    public String getTipoSaldo() {
        return tipoSaldo;
    }

    public void setTipoSaldo(String tipoSaldo) {
        this.tipoSaldo = tipoSaldo;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 8;
        hash = 88 * hash + this.id;
        return hash;
    }

   
    

    @Override
    public boolean equals(Object object) {
          // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallegrupo)) {
            return false;
        }
        Detallegrupo other = (Detallegrupo) object;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Detallegrupo[ id=" + id + " ]";
    }
    
}
