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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTPERIODO")
@XmlRootElement
public class Periodo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGeneratorId",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTPERIODO",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGeneratorId")
    private int id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAINICIAL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicial;

    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAFINAL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinal;

    @Basic(optional = false)
    @NotNull
    @Column(name = "ULTIMOASIENTO")
    private int ultimoAsiento;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @JoinColumn(name = "PERIODOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Periodo periodoAnterior;

    @JoinColumn(name = "ESTADOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estado;

    @Basic(optional = false)
    @Column(name = "FECHARENUMERACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRenumeracion;

    @Basic(optional = false)
    @Column(name = "FECHABALANCE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaBalance;

    @Basic(optional = false)
    @Column(name = "FECHAINTEGRACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIntegracion;

    @Basic(optional = false)
    @Column(name = "TCFIJO")
    private BigDecimal tcFijo;

    @Basic(optional = false)
    @Column(name = "TOTALCAJA")
    private BigDecimal totalCaja;

    @Basic(optional = false)
    @Column(name = "BASEDOLARES")
    private BigDecimal baseDolares;
    
    @Basic(optional = false)
    @Column(name = "BASESOLES")
    private BigDecimal baseSoles;
    
    @Basic(optional = false)
    @Column(name = "TASAIMPLICITAME")
    private BigDecimal tasaImplicitaME;
    
    @Basic(optional = false)
    @Column(name = "TASAOBLIGACIONES")
    private BigDecimal tasaObligacion;
    
    @Basic(optional = false)
    @Column(name = "TASADEDUCCION")
    private BigDecimal tasaDeduccion;
    
    @Basic(optional = false)
    @Column(name = "TASAIMPLICITAMN")
    private BigDecimal tasaImplicitaMN;
    
    @Basic(optional = false)
    @Column(name = "TASAADICIONALMN")
    private BigDecimal tasaAdicionalMN;

    
    public Periodo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public int getUltimoAsiento() {
        return ultimoAsiento;
    }

    public void setUltimoAsiento(int ultimoAsiento) {
        this.ultimoAsiento = ultimoAsiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Periodo getPeriodoAnterior() {
        return periodoAnterior;
    }

    public void setPeriodoAnterior(Periodo periodoAnterior) {
        this.periodoAnterior = periodoAnterior;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Date getFechaRenumeracion() {
        return fechaRenumeracion;
    }

    public void setFechaRenumeracion(Date fechaRenumeracion) {
        this.fechaRenumeracion = fechaRenumeracion;
    }

    public Date getFechaBalance() {
        return fechaBalance;
    }

    public void setFechaBalance(Date fechaBalance) {
        this.fechaBalance = fechaBalance;
    }

    public BigDecimal getTcFijo() {
        return tcFijo;
    }

    public void setTcFijo(BigDecimal tcFijo) {
        this.tcFijo = tcFijo;
    }

    public Date getFechaIntegracion() {
        return fechaIntegracion;
    }

    public void setFechaIntegracion(Date fechaIntegracion) {
        this.fechaIntegracion = fechaIntegracion;
    }

    public BigDecimal getTotalCaja() {
        return totalCaja;
    }

    public void setTotalCaja(BigDecimal totalCaja) {
        this.totalCaja = totalCaja;
    }

    public BigDecimal getBaseDolares() {
        return baseDolares;
    }

    public void setBaseDolares(BigDecimal baseDolares) {
        this.baseDolares = baseDolares;
    }

    public BigDecimal getBaseSoles() {
        return baseSoles;
    }

    public void setBaseSoles(BigDecimal baseSoles) {
        this.baseSoles = baseSoles;
    }

    public BigDecimal getTasaImplicitaME() {
        return tasaImplicitaME;
    }

    public void setTasaImplicitaME(BigDecimal tasaImplicitaME) {
        this.tasaImplicitaME = tasaImplicitaME;
    }

    public BigDecimal getTasaObligacion() {
        return tasaObligacion;
    }

    public void setTasaObligacion(BigDecimal tasaObligacion) {
        this.tasaObligacion = tasaObligacion;
    }

    public BigDecimal getTasaDeduccion() {
        return tasaDeduccion;
    }

    public void setTasaDeduccion(BigDecimal tasaDeduccion) {
        this.tasaDeduccion = tasaDeduccion;
    }

    public BigDecimal getTasaImplicitaMN() {
        return tasaImplicitaMN;
    }

    public void setTasaImplicitaMN(BigDecimal tasaImplicitaMN) {
        this.tasaImplicitaMN = tasaImplicitaMN;
    }

    public BigDecimal getTasaAdicionalMN() {
        return tasaAdicionalMN;
    }

    public void setTasaAdicionalMN(BigDecimal tasaAdicionalMN) {
        this.tasaAdicionalMN = tasaAdicionalMN;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Periodo)) {
            return false;
        }
        Periodo other = (Periodo) object;
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
        return "pe.cajapaita.backerp.contabilidad.entidad.Periodo[ id=" + id + " ]";
    }

}
