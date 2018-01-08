/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.math.BigDecimal;
import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.DetalleIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.TransaccionDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author hnole
 */
public interface IIntegracionSysoneServicio {
    List<IntegracionDTO> traerIntegraciones (int tipoAsientoId,int agenciaId) throws ExcepcionNegocio;
    List<DetalleSysoneDTO> traerAsiento (int tipoAsientoId, String fecha, int agenciaId) throws ExcepcionNegocio;
    void integrarAsiento (int tipoAsientoId, String fecha, int agenciaId, int usuarioId) throws ExcepcionNegocio;
    List<DetalleIntegracionDTO> traerDetalleIntegracion(int tipoAsientoId, String fecha, String cuenta, char moneda, int agenciaId) throws ExcepcionNegocio;
    List<TransaccionDTO> traerDetalleTransaccion(BigDecimal transaccion) throws ExcepcionNegocio;
    void cambiarEstado(int id, int usuarioId) throws ExcepcionNegocio;
    List<Integer> traerNumeroVoucher(int integracionId) throws ExcepcionNegocio;
    void ejecutarIntegracionAutomatica() ;
}
