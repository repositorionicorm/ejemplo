/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionProvisionServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class IntegracionProvisionServicioImpl extends IntegracionServicioImpl implements IIntegracionProvisionServicio {

    @Autowired
    private IWebServicio webServicio;    

    @Override
    public List<IntegracionDTO> traerIntegraciones() throws ExcepcionNegocio {
        return super.traerIntegraciones(0, 0, ProcedenciaEnum.PROVISION);
    }

    @Override
    public List<DetalleSysoneDTO> traerAsiento(int tipoAsientoId, String fecha) throws ExcepcionNegocio {
        return webServicio.traerAsientoProvision(tipoAsientoId, fecha);
    }

    @Override
    public void integrarAsiento(int tipoAsientoId, String fecha, int agencaId, int usuarioId) throws ExcepcionNegocio {
        super.integrarAsiento(tipoAsientoId, fecha, agencaId, usuarioId, ProcedenciaEnum.PROVISION);
    }
}
