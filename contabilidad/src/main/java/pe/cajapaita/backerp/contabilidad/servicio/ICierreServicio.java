
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.EstadoCierreDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author dev-out-02
 */
public interface ICierreServicio {
    List<EstadoCierreDTO> validacionCierre() throws ExcepcionNegocio;
    
    void ejecutarCierre() throws ExcepcionNegocio;
}
