/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Grupo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.Suma;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.MayorizaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.MonedaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcesosEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IMayorizacionServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author hnole
 */
@Service

public class MayorizacionServicioImpl implements IMayorizacionServicio {

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Autowired
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Autowired
    private IRepositorioBaseDao<Cuenta> cuentaDAO;    

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;

    private final Logger logger = Logger.getLogger(MayorizacionServicioImpl.class);

    List<Saldo> saldosPorActualizar;

    @Override
    @Transactional
    public void mayorizar() throws ExcepcionNegocio {
        try {
            if(ProcesosEnum.CIERRE.estaProcesando()){
                throw new ExcepcionNegocio(Mensaje.CIERRE_PROCESANDO, Mensaje.TIPO_ALERTA);
            }
            PeriodoDTO periodoVigenteDTO = utilitarioServicio.traerPeriodoVigente();

            // Inicializamos los saldos
            String consulta = "update erp.cntsaldo saldo set saldo.totaldebe=0, saldo.totalhaber=0, saldo.saldofinal=0 "
                    + "where saldo.periodoid=" + periodoVigenteDTO.getId();
            saldoDAO.ejecutarConsulta(consulta);

            saldosPorActualizar = new ArrayList<>();
            List<Saldo> saldosAnaliticos = new ArrayList<>();
            List<Saldo> listaSaldosPorPeriodo=this.traerTodosSaldosBase(periodoVigenteDTO.getId());
            Periodo periodo= new Periodo();
            periodo.setId(periodoVigenteDTO.getId());
            
            // Se prepara resumen de cuentas analiticas
            List<MayorizaDTO> resumenEnSuMoneda = this.traerResumen(false, periodoVigenteDTO);
            for (MayorizaDTO mayorizaDTO : resumenEnSuMoneda) {
                
                saldosAnaliticos.add(this.traerSaldo(listaSaldosPorPeriodo, mayorizaDTO.getMonedaId(), mayorizaDTO.getCuentaId(), periodo, mayorizaDTO.getTotalDebe(), mayorizaDTO.getTotalHaber()));
            }

            List<MayorizaDTO> resumenConsolidado = this.traerResumen(true, periodoVigenteDTO);
            for (MayorizaDTO mayorizaDTO : resumenConsolidado) {
                saldosAnaliticos.add(this.traerSaldo(listaSaldosPorPeriodo, MonedaEnum.CONSOLIDADO.ordinal(), mayorizaDTO.getCuentaId(), periodo, mayorizaDTO.getTotalDebe(),mayorizaDTO.getTotalHaber()));
            }

            saldosPorActualizar.addAll(saldosAnaliticos);

            for (Saldo saldoAnalitico : saldosAnaliticos) {
                this.agregarPadresDeCuenta(saldoAnalitico,listaSaldosPorPeriodo);
            }

            // Se guardan los saldos ya procesados
            for (Saldo saldo : saldosPorActualizar) {
                saldoDAO.guardar(saldo);
            }

            // El saldo final de los que no tuvieron movimiento tienen el mismo saldo inicial
            consulta = "update erp.cntsaldo saldo set saldo.saldofinal=saldo.saldoInicial "
                    + "where saldo.totaldebe=0 and saldo.totalhaber=0 and periodoid=" + periodoVigenteDTO.getId();
            saldoDAO.ejecutarConsulta(consulta);
            
            // Actualizamos fecha de proceso
            this.actualizarFechaMayorizacion();
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private List<MayorizaDTO> traerResumen(boolean esConsolidado, PeriodoDTO periodoVigenteDTO) throws ExcepcionNegocio {
        Consulta consultaMayoriza = new Consulta();
        consultaMayoriza.agregaAlias(new Alias("cuenta", "cuenta"));
        consultaMayoriza.agregaAlias(new Alias("asiento", "asiento"));

        consultaMayoriza.agregaRestriccionBetweenDate(new RestriccionBetweenDate("asiento.fecha", periodoVigenteDTO.getFechaInicial(), periodoVigenteDTO.getFechaFinal()));
        consultaMayoriza.agregaRestriccionIgual(new RestriccionIgual("asiento.estado.id", EstadoEnum.ACTIVO.ordinal()));

        consultaMayoriza.agregaSuma(new Suma("debe", "totalDebe"));
        consultaMayoriza.agregaSuma(new Suma("haber", "totalHaber"));
        consultaMayoriza.agregaGrupo(new Grupo("cuenta.id", "cuentaId"));
        if (!esConsolidado) {
            consultaMayoriza.agregaGrupo(new Grupo("asiento.moneda.id", "monedaId"));
        }

        return detalleDAO.traerAgrupado(Detalle.class, consultaMayoriza, MayorizaDTO.class);
    }

    private Saldo traerDeLista(Cuenta cuenta, Moneda moneda, Periodo periodo) {
        Saldo saldo;
        try {
            saldo = saldosPorActualizar.stream()
                    .filter((x) -> x.getCuenta().equals(cuenta)
                            && x.getMoneda().equals(moneda)
                            && x.getPeriodo().equals(periodo))
                    .findFirst().get();

            return saldo;
        } catch (Exception ex) {
            saldo = null;
        }

        return saldo;
    }

    private void agregarPadresDeCuenta(Saldo saldoAnalitico,List<Saldo> listaSaldos) throws ExcepcionNegocio {
        Moneda moneda = saldoAnalitico.getMoneda();
        Periodo periodo = saldoAnalitico.getPeriodo();

        Cuenta cuentaPadre = saldoAnalitico.getCuenta();
        BigDecimal debeAnalitico = saldoAnalitico.getTotalDebe();
        BigDecimal haberAnalitico = saldoAnalitico.getTotalHaber();
        int nivel = 1;
        boolean seAgrega;
        do {
            seAgrega = false;
            cuentaPadre = cuentaPadre.getPadre();
            Saldo saldoPadre = this.traerDeLista(cuentaPadre, moneda, periodo);
            if (saldoPadre == null) {
                saldoPadre = this.traerSaldo(cuentaPadre, moneda, periodo,listaSaldos);
                seAgrega = true;
            }

            saldoPadre.setTotalDebe(saldoPadre.getTotalDebe().add(debeAnalitico));
            saldoPadre.setTotalHaber(saldoPadre.getTotalHaber().add(haberAnalitico));
            saldoPadre.calcularSaldoFinal();

            if (seAgrega) {
                saldosPorActualizar.add(saldoPadre);
            }

            nivel = cuentaPadre.getNivel();
        } while (nivel != 1);
    }
    
    private void actualizarFechaMayorizacion() throws ExcepcionNegocio{          
        int periodoVigenteId=utilitarioServicio.traerPeriodoVigente().getId();
        Periodo periodo= periodoDAO.traerPorId(Periodo.class, periodoVigenteId);
        periodo.setFechaBalance(new Date());
        
        periodoDAO.guardar(periodo);
    }
    
    private List<Saldo> traerTodosSaldosBase(int periodoId) throws ExcepcionNegocio{
        Consulta consulta= new Consulta();
        consulta.agregaAlias(new Alias("periodo","periodo"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoId));
        
        return saldoDAO.traerTodo(Saldo.class, consulta);
    }
    
    private Saldo traerSaldo(List<Saldo> listaSaldos,int monedaId, int cuentaId,Periodo periodo,BigDecimal debe, BigDecimal haber) throws ExcepcionNegocio{
        Saldo saldo=listaSaldos.stream().filter(
                x->x.getMoneda().getId()==monedaId && x.getCuenta().getId()==cuentaId).findFirst().orElse(null);
        
        
        if(saldo==null){
            Moneda moneda=new Moneda();
            moneda.setId(monedaId);
            saldo= new Saldo();
            Cuenta cuenta=listaSaldos.stream().filter(x->x.getCuenta().getId()==cuentaId)
                    .map(x->x.getCuenta()).findFirst().orElse(null);
            if(cuenta==null){
                saldo.setCuenta(cuentaDAO.traerPorId(Cuenta.class, cuentaId));
            }
            else{
                saldo.setCuenta(cuenta);
            }
            saldo.setMoneda(moneda);
            saldo.setPeriodo(periodo);
        }
        
        saldo.setTotalDebe(debe);
        saldo.setTotalHaber(haber);
        saldo.calcularSaldoFinal();

        return saldo;
    }
    
    private Saldo traerSaldo(Cuenta cuenta, Moneda moneda, Periodo periodo,List<Saldo> listaSaldos) throws ExcepcionNegocio{
         Saldo saldo=listaSaldos.stream().filter(
                x->x.getMoneda().getId()==moneda.getId() && x.getCuenta().getId()==cuenta.getId()).findFirst().orElse(null);
         
         if(saldo==null){
            saldo = new Saldo();
            saldo.setCuenta(cuenta);
            saldo.setMoneda(moneda);
            saldo.setPeriodo(periodo);
        } else {
            saldo.setTotalDebe(new BigDecimal(0));
            saldo.setTotalHaber(new BigDecimal(0));
        }
        return saldo;
    }

    //</editor-fold>
}
