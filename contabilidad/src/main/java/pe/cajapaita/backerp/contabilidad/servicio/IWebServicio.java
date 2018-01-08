/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.math.BigDecimal;
import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.TransaccionDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.dto.DetalleIntegracionDTO;

/**
 *
 * @author hnole
 */
public interface IWebServicio {

    List<DetalleSysoneDTO> traerAsientoSysone(int tipoAsientoId, String fecha, int agenciaId) throws ExcepcionNegocio;
    
    List<DetalleSysoneDTO> traerAsientoProvision(int tipoAsientoId, String fecha) throws ExcepcionNegocio;

    List<String> traerArchivosSiafc() throws ExcepcionNegocio;

    List<DetalleSIAFCDTO> traerDetalleArchivoSiafc(String nombreArchivo) throws ExcepcionNegocio;

    String renombrarArchivo(String nombreArchivo) throws ExcepcionNegocio;

    List<DetalleIntegracionDTO> traerDetalleIntegracionSysone(String fecha, String cuenta, int tipoAsientoId, int agenciaId) throws ExcepcionNegocio;

    List<TransaccionDTO> traerDetalleTransaccion(BigDecimal transaccion) throws ExcepcionNegocio;
}
