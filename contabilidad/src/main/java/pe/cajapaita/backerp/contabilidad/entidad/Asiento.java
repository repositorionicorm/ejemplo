/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTASIENTO")
@XmlRootElement
public class Asiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGeneratorId",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTASIENTO",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGeneratorId")
    private int id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "NUMERO")
    private int numero;

    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 70)
    @Column(name = "GLOSA")
    private String glosa;
    
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

    @JoinColumn(name = "TIPOASIENTOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TipoAsiento tipoAsiento;

    @JoinColumn(name = "PROCEDENCIAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Procedencia procedencia;

    @JoinColumn(name = "MONEDAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Moneda moneda;

    @JoinColumn(name = "ESTADOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estado;

    @JoinColumn(name = "CREADOPOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario creadoPor;
    
    @JoinColumn(name = "EDITADOPOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario editadoPor;
    
    @JoinColumn(name = "AGENCIAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Agencia agencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "asiento")
    private List<Detalle> detalles;

    public Asiento() {
        this.detalles = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public TipoAsiento getTipoAsiento() {
        return tipoAsiento;
    }

    public void setTipoAsiento(TipoAsiento tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    public Procedencia getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Procedencia procedencia) {
        this.procedencia = procedencia;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
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

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
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
    
    
     

    @XmlTransient
    public List<Detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(Detalle detalle) {
        detalle.setAsiento(this);
        this.detalles.add(detalle);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asiento)) {
            return false;
        }
        Asiento other = (Asiento) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Asiento[ id=" + id + " ]";
    }

}
