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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTCUENTA")
@XmlRootElement
public class Cuenta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGenerator",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTCUENTA",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGenerator")
    private int id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "CUENTA")
    private String cuenta;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ESSBS")
    private String esSbs;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ESANALITICA")
    private String esAnalitica;

    @JoinColumn(name = "TIPOCUENTAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TipoCuenta tipoCuenta;

    @JoinColumn(name = "CUENTAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Cuenta padre;

    @JoinColumn(name = "ESTADOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estado;
    
    @JoinColumn(name = "USUARIOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario usuario;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }   
    
    public Cuenta getPadre() {
        return padre;
    }

    public void setPadre(Cuenta padre) {
        this.padre = padre;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getEsSbs() {
        return esSbs;
    }

    public void setEsSbs(String esSbs) {
        this.esSbs = esSbs;
    }

    public String getEsAnalitica() {
        return esAnalitica;
    }

    public void setEsAnalitica(String esAnalitica) {
        this.esAnalitica = esAnalitica;
    }
    
    public int getNivel() {
        return this.cuenta.trim().length();
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cuenta)) {
            return false;
        }
        Cuenta other = (Cuenta) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Cuenta[ id=" + id + " ]";
    }

    public void validarPropiedades() throws ExcepcionNegocio {

        if (this.esSbs.equals("S")) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_ES_SBS, Mensaje.TIPO_ALERTA);
        }

        if (this.esAnalitica.equals("N")) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_NO_ES_ANALITICA, Mensaje.TIPO_ALERTA);
        }
    }

    

}
