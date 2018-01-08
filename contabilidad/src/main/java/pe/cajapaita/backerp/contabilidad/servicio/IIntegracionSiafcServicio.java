/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.ArchivoSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.RespuestaIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author dev-out-02
 */
public interface IIntegracionSiafcServicio {
    
    List<ArchivoSIAFCDTO> listaArchivosSIAFC() throws ExcepcionNegocio;
    List<DetalleSIAFCDTO> detalleArchivoSIAFC(String nombreArchivo) throws ExcepcionNegocio;
    List<RespuestaIntegracionDTO> integrarArchivosSIAFC(List<ArchivoSIAFCDTO> listaAsientos, int usuarioId) throws ExcepcionNegocio;
}
