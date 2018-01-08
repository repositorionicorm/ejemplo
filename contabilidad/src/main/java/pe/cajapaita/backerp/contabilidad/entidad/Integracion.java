/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
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
@Table(name = "CNTINTEGRACION")
@XmlRootElement
public class Integracion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGeneratorId",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTINTEGRACION",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGeneratorId")
    private Integer id;
    
    @Basic(optional = false)    
    @NotNull
    @Column(name = "FECHA") 
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    
    @Size(max = 100)
    @Column(name = "OBSERVACION")
    private String observacion;
    
    @Size(max = 20)
    @Column(name = "CODIGOASIENTO")
    private String asientoId;
    
    @JoinColumn(name = "TIPOASIENTOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TipoAsiento tipoAsiento;
    
    @JoinColumn(name = "PROCEDENCIAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Procedencia procedencia;
    
    @JoinColumn(name = "ESTADOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estado;
    
    @JoinColumn(name = "AGENCIAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Agencia agencia;

    public Integracion() {
    }

    public Integracion(Integer id) {
        this.id = id;
    }

    public Integracion(Integer id, Date fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getAsientoId() {
        return asientoId;
    }

    public void setAsientoId(String asientoId) {
        this.asientoId = asientoId;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }
    
    
    
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Integracion)) {
            return false;
        }
        Integracion other = (Integracion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Integracion[ id=" + id + " ]";
    }
    
}
