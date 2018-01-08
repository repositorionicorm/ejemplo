/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author dev-out-02
 */
public interface IIntegracionExcel {
    List<DetalleDTO> generarDetalle(MultipartFile archivoExcel) throws ExcepcionNegocio;
}
