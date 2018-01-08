/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;

/**
 * @author hnole
 */
public class AsientoDTO {

    private int id;
    private int numero;
    private int tipoAsientoId;
    private int monedaId;
    private int procedenciaId;
    private Date fecha;
    private String fechaString;
    private String glosa;
    private String tipoAsientoDescripcion;
    private String procedenciaDescripcion;
    private String monedaDescripcion;
    private String usuarioNombre;
    private BigDecimal total;
    private String agenciaAbreviatura;
    private int agenciaId;
    private List<DetalleDTO> detalles;
    private String accion;

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

    public int getTipoAsientoId() {
        return tipoAsientoId;
    }

    public void setTipoAsientoId(int tipoAsientoId) {
        this.tipoAsientoId = tipoAsientoId;
    }

    public int getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(int monedaId) {
        this.monedaId = monedaId;
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

    public String getTipoAsientoDescripcion() {
        return tipoAsientoDescripcion;
    }

    public void setTipoAsientoDescripcion(String tipoAsientoDescripcion) {
        this.tipoAsientoDescripcion = tipoAsientoDescripcion;
    }

    public String getProcedenciaDescripcion() {
        return procedenciaDescripcion;
    }

    public void setProcedenciaDescripcion(String procedenciaDescripcion) {
        this.procedenciaDescripcion = procedenciaDescripcion;
    }

    public String getMonedaDescripcion() {
        return monedaDescripcion;
    }

    public void setMonedaDescripcion(String monedaDescripcion) {
        this.monedaDescripcion = monedaDescripcion;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getFechaString() {
        return fechaString;
    }

    public void setFechaString(String fechaString) {
        this.fechaString = fechaString;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleDTO> detalles) {
        this.detalles = detalles;
    }

    public String getAgenciaAbreviatura() {
        return agenciaAbreviatura;
    }

    public void setAgenciaAbreviatura(String agenciaAbreviatura) {
        this.agenciaAbreviatura = agenciaAbreviatura;
    }

    public int getAgenciaId() {
        return agenciaId;
    }

    public void setAgenciaId(int agenciaId) {
        this.agenciaId = agenciaId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public int getProcedenciaId() {
        return procedenciaId;
    }

    public void setProcedenciaId(int procedenciaId) {
        this.procedenciaId = procedenciaId;
    }
    
    

    public void validaPropiedades() throws ExcepcionNegocio {

        if (Helper.esNuloVacio(this.getFechaString())) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_SELECCIONE_FECHA, Mensaje.TIPO_ALERTA);
        }

        if (this.tipoAsientoId == 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_SELECCIONE_TIPO_ASIENTO, Mensaje.TIPO_ALERTA);
        }

        if (Helper.esNuloVacio(this.glosa)) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_INGRESE_GLOSA, Mensaje.TIPO_ALERTA);
        }

        if (Helper.esNuloVacio((ArrayList) this.detalles)) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO, Mensaje.TIPO_ALERTA);
        }

    }
}
