/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTDETALLE")
@XmlRootElement
public class Detalle implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGenerator",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTDETALLE",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGenerator")
    private int id;    
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DEBE")
    private BigDecimal debe;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "HABER")
    private BigDecimal haber;
    
    @JoinColumn(name = "CUENTAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Cuenta cuenta;
    
    @JoinColumn(name = "ASIENTOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Asiento asiento;
    
    @JoinColumn(name = "MONEDAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Moneda moneda;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHACREACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAEDICION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    
     @JoinColumn(name = "CREADOPOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario creadoPor;
    
    @JoinColumn(name = "EDITADOPOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario editadoPor;
    

    public Detalle() {
    }    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Usuario getEditadoPor() {
        return editadoPor;
    }

    public void setEditadoPor(Usuario editadoPor) {
        this.editadoPor = editadoPor;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalle)) {
            return false;
        }
        Detalle other = (Detalle) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Detalle[ id=" + id + " ]";
    }
    
}
