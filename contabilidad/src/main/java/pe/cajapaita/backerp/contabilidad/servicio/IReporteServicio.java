/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import pe.cajapaita.backerp.contabilidad.dto.GrupoDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author dev-out-02
 */
public interface IReporteServicio {
    public void generarLibroDiario( Map<String,Object> parameters,String tipo,HttpServletResponse response);
    public void generarLibroMayor(Map<String,Object> parameters,String tipo,HttpServletResponse response);
    public void generarBalanceComprobacion( Map<String,Object> parameters,String tipo,HttpServletResponse response);
    public void generarBalanceSituacion(Map<String,Object> parameters,String tipo,HttpServletResponse response);
    public List<GrupoDTO> generarReporteEstado(int periodoId,int reporteId)throws ExcepcionNegocio;
    void generarExcelReporteEstados(HttpServletResponse response, String nombreHoja, String empresa, String titulo, String periodo, List<GrupoDTO> listaFilas) throws ExcepcionNegocio;
    void traerReporteEncaje(int reporteId,String fechaFin, int monedaId,HttpServletResponse response)  throws ExcepcionNegocio;
    List<GrupoDTO> traerConfiguracionRepote(int reporteId) throws ExcepcionNegocio;
}
