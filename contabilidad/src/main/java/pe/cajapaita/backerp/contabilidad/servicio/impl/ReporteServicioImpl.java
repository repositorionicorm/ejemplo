/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import com.ibm.icu.util.Calendar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleGrupoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoEncajePorFecha;
import pe.cajapaita.backerp.contabilidad.dto.GrupoDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ReporteDTO;
import pe.cajapaita.backerp.contabilidad.dto.SaldoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Detallegrupo;
import pe.cajapaita.backerp.contabilidad.entidad.Grupo;
import pe.cajapaita.backerp.contabilidad.entidad.GrupoEncaje;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExtensionReporteEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.MesEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.MonedaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.Variables;
import pe.cajapaita.backerp.contabilidad.servicio.IReporteServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Service
@Transactional
public class ReporteServicioImpl implements IReporteServicio {

    @Value("${url.Reportes}")
    private String urlReportes;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Autowired
    private IRepositorioBaseDao<Grupo> grupoDAO;

    @Autowired
    private IRepositorioBaseDao<GrupoEncaje> grupoEncajeDAO;

    @Autowired
    private IRepositorioBaseDao<Detallegrupo> detalleGrupoDAO;

    @Autowired
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Autowired
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Autowired
    private DataSource dataSource;

    private final Logger logger = Logger.getLogger(ReporteServicioImpl.class);

    @Override
    public void generarLibroDiario(Map<String, Object> parameters, String tipo, HttpServletResponse response) {
        String nombreReporte = tipo.equalsIgnoreCase(ExtensionReporteEnum.ZIP_X.name()) ? "libroDiarioExcel" : "libroDiario";
        this.armarReporte(response, tipo, parameters, nombreReporte);
    }

    @Override
    public void generarLibroMayor(Map<String, Object> parameters, String tipo, HttpServletResponse response) {
        String nombreReporte = tipo.equalsIgnoreCase(ExtensionReporteEnum.ZIP_X.name()) ? "libroMayorExcel" : "libroMayor";
        int periodoId=(int) parameters.get("periodo");
        String fechaInicio="";
        String fechaFin="";
        int periodoAnteriorId;
        try{
            Periodo periodo=periodoDAO.traerPorId(Periodo.class, periodoId);
            periodoAnteriorId=periodo.getPeriodoAnterior().getId();
            if(periodo.getEstado().getId()==EstadoEnum.PENDIENTE.ordinal()){
                Periodo periodoAnterior=periodo.getPeriodoAnterior();
                fechaInicio=Helper.convertirAFecha(periodoAnterior.getFechaInicial());
                periodoId=periodoAnterior.getId();
                periodoAnteriorId=periodoAnterior.getPeriodoAnterior().getId();
            }
            else{
                fechaInicio=Helper.convertirAFecha(periodo.getFechaInicial());
            }
            fechaFin=Helper.convertirAFecha(periodo.getFechaFinal());
            parameters.put("periodo", periodoId);
            parameters.put("periodoAnteriorId", periodoAnteriorId);
            parameters.put("fechaInicial", fechaInicio);
            parameters.put("fechaFinal", fechaFin);
            this.armarReporte(response, tipo, parameters, nombreReporte);
        }
        catch(ExcepcionNegocio en){
            
        }
        
    }

    @Override
    public void generarBalanceComprobacion(Map<String, Object> parameters, String tipo, HttpServletResponse response) {
        String nombreReporte = tipo.equalsIgnoreCase(ExtensionReporteEnum.ZIP_X.name()) ? "balanceComprobacionExcel" : "balanceComprobacion";
        this.armarReporte(response, tipo, parameters, nombreReporte);
    }

    @Override
    public void generarBalanceSituacion(Map<String, Object> parameters, String tipo, HttpServletResponse response) {
        String nombreReporte = tipo.equalsIgnoreCase(ExtensionReporteEnum.ZIP_X.name()) ? "balanceSituacionExcel" : "reporteBalanceSituacion";
        this.armarReporte(response, tipo, parameters, nombreReporte);
    }

    @Override
    public List<GrupoDTO> generarReporteEstado(int periodoId, int reporteId) throws ExcepcionNegocio {
        List<ReporteDTO> listaReportes = utilitarioServicio.traerReportesVarios();
        List<GrupoDTO> listaGrupoDTO = new ArrayList<>();

        try {
            ReporteDTO reporte = listaReportes.stream().filter(x -> x.getId() == reporteId).findAny().orElse(null);

            if (reporte == null) {
                throw new ExcepcionNegocio(Mensaje.REPORTE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            Consulta consulta = new Consulta();
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("id", periodoId));

            PeriodoDTO periodo = (PeriodoDTO) periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class);

            if (periodo == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            Consulta consultaGrupo = new Consulta();
            consultaGrupo.agregaAlias(new Alias("reporte", "reporte"));
            consultaGrupo.agregaRestriccionIgual(new RestriccionIgual("reporte.id", reporteId));
            consultaGrupo.agregaEquivalencia(new Equivalencia("id", "id"));
            consultaGrupo.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consultaGrupo.agregaEquivalencia(new Equivalencia("rango", "rango"));

            listaGrupoDTO = grupoDAO.traerTodo(Grupo.class, consultaGrupo, GrupoDTO.class);
            BigDecimal monto;
            SaldoDTO saldo;
            List<DetalleGrupoDTO> listaDetalleGrupo;
            BigDecimal saldoFinal;
            listaGrupoDTO.forEach(z -> {
                if (z.getRango() != null) {
                    z.setEsCabecera(true);
                }
            });
            for (GrupoDTO grupo : listaGrupoDTO) {
                Consulta consultaDetalle = new Consulta();
                consultaDetalle.agregaAlias(new Alias("grupo", "grupo"));
                consultaDetalle.agregaRestriccionIgual(new RestriccionIgual("grupo.id", grupo.getId()));
                consultaDetalle.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
                consultaDetalle.agregaEquivalencia(new Equivalencia("operacion", "operacion"));
                consultaDetalle.agregaOrdenAscendente("id");
                listaDetalleGrupo = detalleGrupoDAO.traerTodo(Detallegrupo.class, consultaDetalle, DetalleGrupoDTO.class);

                if (listaDetalleGrupo.isEmpty()) {
                    grupo.setMonto(BigDecimal.ZERO);
                    continue;
                }
                monto = BigDecimal.ZERO;

                for (DetalleGrupoDTO detalle : listaDetalleGrupo) {
                    Consulta consultaSaldos = new Consulta();
                    consultaSaldos.agregaAlias(new Alias("cuenta", "cuenta"));
                    consultaSaldos.agregaAlias(new Alias("periodo", "periodo"));
                    consultaSaldos.agregaAlias(new Alias("moneda", "moneda"));
                    consultaSaldos.agregaRestriccionIgual(new RestriccionIgual("cuenta.cuenta", detalle.getCuenta()));
                    consultaSaldos.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoId));
                    consultaSaldos.agregaRestriccionIgual(new RestriccionIgual("moneda.id", MonedaEnum.CONSOLIDADO.ordinal()));
                    consultaSaldos.agregaEquivalencia(new Equivalencia("saldoFinal", "saldoFinal"));

                    saldo = (SaldoDTO) saldoDAO.traerUnico(Saldo.class, consultaSaldos, SaldoDTO.class);

                    if (saldo != null) {
                        saldoFinal = saldo.getSaldoFinal();
                        saldoFinal = saldoFinal.multiply(detalle.getOperacion());

                        monto = monto.add(saldoFinal);
                    }
                }
                grupo.setMonto(monto);
            }

            listaGrupoDTO = this.calcularMontosGrupoPadres(listaGrupoDTO);

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

        return listaGrupoDTO;
    }

    @Override
    public void generarExcelReporteEstados(HttpServletResponse response, String nombreHoja, String empresa, String titulo, String periodo, List<GrupoDTO> listaFilas) throws ExcepcionNegocio {

        XSSFWorkbook libro = new XSSFWorkbook();
        XSSFSheet hoja = libro.createSheet(nombreHoja);

        DataFormat formatoNumero = libro.createDataFormat();

        //Font Negrita
        Font fontNegrita = libro.createFont();
        fontNegrita.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontNegrita.setFontHeightInPoints((short) 8);

        //Font Tamanio 8
        Font fontHeight8 = libro.createFont();
        fontHeight8.setFontHeightInPoints((short) 8);

        //Negrita Texto normal
        XSSFCellStyle textoNegrita = libro.createCellStyle();
        textoNegrita.setFont(fontNegrita);

        //Negrita Numero formato 10,000.50
        XSSFCellStyle numeroNegrita = libro.createCellStyle();
        numeroNegrita.setFont(fontNegrita);
        numeroNegrita.setDataFormat(formatoNumero.getFormat("#,##0.00"));

        //Identacion Texto
        XSSFCellStyle textoIdentacion = libro.createCellStyle();
        textoIdentacion.setFont(fontHeight8);
        textoIdentacion.setIndention((short) 2);

        //Numeros normal formato 10,000.50
        XSSFCellStyle numeroNormal = libro.createCellStyle();
        numeroNormal.setFont(fontHeight8);
        numeroNormal.setDataFormat(formatoNumero.getFormat("#,##0.00"));

        //Alineacion Center
        XSSFCellStyle alineacionCenter = libro.createCellStyle();
        alineacionCenter.setAlignment(HorizontalAlignment.CENTER);
        alineacionCenter.setFont(fontNegrita);

        //Alineacion Center border inferior
        XSSFCellStyle borderInferior = libro.createCellStyle();
        borderInferior.setAlignment(HorizontalAlignment.CENTER);
        borderInferior.setFont(fontNegrita);
        borderInferior.setBorderBottom(BorderStyle.THIN);

        XSSFRow rowEmpresa = hoja.createRow(0);

        hoja.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));

        XSSFCell celda = rowEmpresa.createCell(0);
        celda.setCellValue(empresa);
        celda.setCellStyle(textoNegrita);

        XSSFRow rowTitulo = hoja.createRow(2);
        XSSFCell celdaTitulo = rowTitulo.createCell(0);
        celdaTitulo.setCellValue(titulo);
        celdaTitulo.setCellStyle(alineacionCenter);

        XSSFRow rowPeriodo = hoja.createRow(3);
        XSSFCell celdaPeriodo = rowPeriodo.createCell(0);
        celdaPeriodo.setCellValue("PERIODO: " + periodo);
        celdaPeriodo.setCellStyle(borderInferior);

        XSSFCell celdaPeriodo2 = rowPeriodo.createCell(1);
        celdaPeriodo2.setCellStyle(borderInferior);

        int i = 5;
        for (GrupoDTO fila : listaFilas) {
            XSSFRow nuevaFila = hoja.createRow(i);

            XSSFCell celdaConcepto = nuevaFila.createCell(0);
            celdaConcepto.setCellValue(fila.getDescripcion());

            XSSFCell celdaMonto = nuevaFila.createCell(1);
            celdaMonto.setCellValue(fila.getMonto().doubleValue());

            i++;
            if (fila.isEsCabecera()) {
                celdaConcepto.setCellStyle(textoNegrita);
                celdaMonto.setCellStyle(numeroNegrita);

                continue;
            }
            celdaConcepto.setCellStyle(textoIdentacion);
            celdaMonto.setCellStyle(numeroNormal);
        }

        hoja.autoSizeColumn(0);
        hoja.setColumnWidth(1, 4000);

        hoja.setDefaultRowHeight((short) 225);
        hoja.setDisplayGridlines(false);

        try {
            OutputStream outputStream = response.getOutputStream();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment; filename=" + nombreHoja + "_" + periodo + ".xlsx");
            response.setHeader("Pragma", "public");
            libro.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public void traerReporteEncaje(int reporteId, String fechaFin, int monedaId, HttpServletResponse response) throws ExcepcionNegocio {
        List<ReporteDTO> listaReportes = utilitarioServicio.traerListaReportesEncaje();
        List<MonedaDTO> listaMonedaActiva = utilitarioServicio.traerListaMonedaActiva();
        PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();
        String descripcionReporte;

        List<GrupoDTO> listaGrupoDTO;
        List<GrupoDTO> listaGrupoTemp = new ArrayList<>();

        ReporteDTO reporte = listaReportes.stream().filter(x -> x.getId() == reporteId).findFirst().orElse(null);
        MonedaDTO moneda = listaMonedaActiva.stream().filter(x -> x.getId() == monedaId).findFirst().orElse(null);

        if (periodoDTO == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }

        if (reporte == null) {
            throw new ExcepcionNegocio(Mensaje.REPORTE_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }
        descripcionReporte = reporte.getDescripcion();
        if (moneda == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_MONEDA_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }
        Date fechaFinal = Helper.convertirAFecha(fechaFin);
        Date fechaInicial = periodoDTO.getFechaInicial();
        if (!Helper.between(fechaFinal, periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal())) {
            throw new ExcepcionNegocio(Mensaje.FECHA_NO_PERTENECE_AL_PERIODO, Mensaje.TIPO_ALERTA);
        }
        //TRAER TOTAL CAJA PERIODO ANTERIOR
        Periodo periodoAnterior = periodoDAO.traerPorId(Periodo.class, periodoDTO.getPeriodoAnteriorId());
        BigDecimal totalCajaPeriodo = BigDecimal.ZERO;
        if (periodoAnterior != null) {
            totalCajaPeriodo = periodoAnterior.getTotalCaja() == null ? BigDecimal.ZERO : periodoAnterior.getTotalCaja();
        }

        //CALCULAR TIPO CAMBIO
        BigDecimal tipoCambioActual = periodoDTO.getTipoCambio();
        BigDecimal tipoCambioPeriodoAnterior = periodoDTO.getTipoCambioAnterior();

        //TRAER LISTA DE GRUPOS
        listaGrupoDTO = this.traerListaGrupos(reporteId, monedaId);

        listaGrupoDTO.forEach(x -> {
            GrupoDTO grupoTemp = new GrupoDTO();
            grupoTemp.setId(x.getId());
            grupoTemp.setRango(x.getRango());
            listaGrupoTemp.add(grupoTemp);
        });
        Calendar calendarioFinal = Calendar.getInstance();
        calendarioFinal.setTime(fechaFinal);

        Calendar calendarioInicial = Calendar.getInstance();
        calendarioInicial.setTime(fechaInicial);

        int cantidadDias = calendarioFinal.get(Calendar.DAY_OF_MONTH);
        int mes = calendarioInicial.get(Calendar.MONTH);
        int anio = calendarioInicial.get(Calendar.YEAR);

        List<DetalleGrupoDTO> listaDetalleGrupo;
        BigDecimal monto;

        //TRAER EL ULTIMO DIA DEL MES
        int ultimoDiaMes = calendarioInicial.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (monedaId != MonedaEnum.DOLARES.ordinal()) {
            tipoCambioActual = BigDecimal.ONE;
            tipoCambioPeriodoAnterior = BigDecimal.ONE;
        }

        //TRAER LISTA ASIENTOS
        for (int i = 1; i <= cantidadDias; i++) {
            for (GrupoDTO grupoDTO : listaGrupoDTO) {
                if (Variables.CODIGO_CAJA_PERIODO_ANTERIOR.equalsIgnoreCase(grupoDTO.getOperacionSoles())) {
                    grupoDTO.setMonto(totalCajaPeriodo);
                    ResultadoEncajePorFecha nuevoResultado = new ResultadoEncajePorFecha();
                    nuevoResultado.setMonto(totalCajaPeriodo);
                    if (grupoDTO.getListaPorFecha() != null) {
                        grupoDTO.getListaPorFecha().add(nuevoResultado);
                    } else {
                        List<ResultadoEncajePorFecha> listaFecha = new ArrayList<>();
                        listaFecha.add(nuevoResultado);
                        grupoDTO.setListaPorFecha(listaFecha);
                    }
                    continue;
                }
                listaDetalleGrupo = this.traerListaDetalleGrupo(grupoDTO.getId());
                if (listaDetalleGrupo.isEmpty()) {
                    grupoDTO.setMonto(BigDecimal.ZERO);
                    continue;
                }
                monto = BigDecimal.ZERO;

                for (DetalleGrupoDTO detalleGrupo : listaDetalleGrupo) {

                    int tipoCuentaId;

                    List<DetalleDTO> listaDetalle = this.traerDetalleAsiento(detalleGrupo.getCuenta(), calendarioInicial.getTime(), monedaId, periodoDTO.getFechaInicial());
                    BigDecimal totalDebe = listaDetalle.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalHaber = listaDetalle.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

                    Consulta consultaSaldo = new Consulta();
                    consultaSaldo.agregaAlias(new Alias("periodo", "periodo"));
                    consultaSaldo.agregaAlias(new Alias("moneda", "moneda"));
                    consultaSaldo.agregaAlias(new Alias("cuenta", "cuenta"));
                    consultaSaldo.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoDTO.getId()));
                    consultaSaldo.agregaRestriccionIgual(new RestriccionIgual("moneda.id", monedaId));
                    consultaSaldo.agregaRestriccionIgual(new RestriccionIgual("cuenta.cuenta", detalleGrupo.getCuenta()));
                    consultaSaldo.agregaEquivalencia(new Equivalencia("saldoInicial", "saldoInicial"));
                    consultaSaldo.agregaEquivalencia(new Equivalencia("cuenta.tipoCuenta.id", "tipoCuentaId"));
                    consultaSaldo.agregaEquivalencia(new Equivalencia("periodo.id", "periodoId"));
                    consultaSaldo.agregaEquivalencia(new Equivalencia("cuenta.cuenta", "cuenta"));
                    consultaSaldo.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));

                    SaldoDTO saldo = (SaldoDTO) saldoDAO.traerUnico(Saldo.class, consultaSaldo, SaldoDTO.class);

                    BigDecimal saldoInicial = BigDecimal.ZERO;

                    if (saldo != null) {
                        saldoInicial = saldo.getSaldoInicial();
                    }
                    if (!listaDetalle.isEmpty() && Variables.TIPO_SALDO_INICIAL.equalsIgnoreCase(detalleGrupo.getTipoSaldo())) {
                        tipoCuentaId = listaDetalle.stream().map(x -> x.getTipoCuentaId()).distinct().findAny().get();

                        switch (tipoCuentaId) {
                            case 1:
                                saldoInicial = saldoInicial.add(totalDebe);
                                saldoInicial = saldoInicial.subtract(totalHaber);
                                break;
                            case 2:
                                saldoInicial = saldoInicial.subtract(totalDebe);
                                saldoInicial = saldoInicial.add(totalHaber);
                                break;
                        }
                    }

                    monto = monto.add(saldoInicial);

                }
                ResultadoEncajePorFecha porFecha = new ResultadoEncajePorFecha();

                porFecha.setFecha(Helper.convertirAFecha(calendarioInicial.getTime()));

                if (i == ultimoDiaMes) {
                    monto = monto.divide(tipoCambioActual, RoundingMode.HALF_UP);
                } else {
                    monto = monto.divide(tipoCambioPeriodoAnterior, RoundingMode.HALF_UP);

                }

                porFecha.setMonto(monto);
                grupoDTO.setMonto(monto);

                if (grupoDTO.getListaPorFecha() != null) {
                    grupoDTO.getListaPorFecha().add(porFecha);
                } else {
                    List<ResultadoEncajePorFecha> listaFecha = new ArrayList<>();
                    listaFecha.add(porFecha);
                    grupoDTO.setListaPorFecha(listaFecha);
                }

            }
            listaGrupoDTO = this.calcularMontosGrupoPadres(listaGrupoDTO);

            int j = 0;
            for (GrupoDTO grupoTemp : listaGrupoTemp) {
                listaGrupoDTO.get(j).setRango(grupoTemp.getRango());
                j++;
            }
            calendarioInicial.add(Calendar.DAY_OF_YEAR, 1);
        }

        String nombreMes = "";
        MesEnum mesEnum = Helper.traerMesPorId(mes);
        if (mesEnum != null) {
            nombreMes = mesEnum.name();
        }
        nombreMes = nombreMes + " " + anio;

        List<BigDecimal> listaPieReporte = this.calcularTotalCajaPeriodo(reporteId, periodoDTO.getId(), cantidadDias, listaGrupoDTO, monedaId);
        this.generarReporteExcelEncaje(nombreMes, ultimoDiaMes, monedaId, tipoCambioPeriodoAnterior, tipoCambioActual, descripcionReporte, "cajapaita", listaGrupoDTO, response, listaPieReporte);

    }

    @Override
    public List<GrupoDTO> traerConfiguracionRepote(int reporteId) throws ExcepcionNegocio {
        List<GrupoDTO> listaGrupoDTO = new ArrayList<>();
        try {

            Consulta consultaGrupo = new Consulta();
            consultaGrupo.agregaRestriccionIgual(new RestriccionIgual("reporte.id", reporteId));
            consultaGrupo.agregaEquivalencia(new Equivalencia("id", "id"));
            consultaGrupo.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consultaGrupo.agregaEquivalencia(new Equivalencia("rango", "rango"));

            listaGrupoDTO = grupoDAO.traerTodo(Grupo.class, consultaGrupo, GrupoDTO.class);

            if (listaGrupoDTO.isEmpty()) {
                return listaGrupoDTO;
            }
            int minimoFila = listaGrupoDTO.stream().map(x -> x.getId()).mapToInt(Integer::intValue).min().orElse(0) - 1;

            Consulta consultaDetalle;
            String nuevoRango;
            char signo;
            int grupoId;
            for (GrupoDTO grupo : listaGrupoDTO) {
                Consulta consultaGrupoEncaje = new Consulta();
                consultaGrupoEncaje.agregaAlias(new Alias("grupo", "grupo"));
                consultaGrupoEncaje.agregaRestriccionIgual(new RestriccionIgual("grupo.id", grupo.getId()));
                consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("operacionsoles", "operacionSoles"));
                consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("operaciondolares", "operacionDolares"));

                GrupoDTO operacionSolesDolares = (GrupoDTO) grupoEncajeDAO.traerUnico(GrupoEncaje.class, consultaGrupoEncaje, GrupoDTO.class);
                if (operacionSolesDolares != null) {
                    grupo.setOperacionSoles(operacionSolesDolares.getOperacionSoles());
                    grupo.setOperacionDolares(operacionSolesDolares.getOperacionDolares());
                }

                consultaDetalle = new Consulta();
                consultaDetalle.agregaAlias(new Alias("grupo", "grupo"));
                consultaDetalle.agregaRestriccionIgual(new RestriccionIgual("grupo.id", grupo.getId()));
                consultaDetalle.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
                consultaDetalle.agregaEquivalencia(new Equivalencia("grupo.id", "grupoId"));
                grupo.setListaPorFecha(detalleGrupoDAO.traerTodo(Detallegrupo.class, consultaDetalle, ResultadoEncajePorFecha.class));

                if (grupo.getRango() == null) {
                    continue;

                }
                if (grupo.getRango().isEmpty()) {
                    continue;
                }

                List<String> listaRango = Helper.traerListadeStringSeparador(grupo.getRango(), ",");
                nuevoRango = "";
                for (String operar : listaRango) {
                    signo = operar.charAt(0);
                    grupoId = Integer.parseInt(operar.substring(1, operar.length()));
                    grupoId = grupoId - minimoFila;
                    nuevoRango = nuevoRango + signo + grupoId + ", ";
                }
                nuevoRango = nuevoRango.substring(0, nuevoRango.length() - 2);
                grupo.setRango(nuevoRango);

            }
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

        return listaGrupoDTO;

    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private List<GrupoDTO> calcularMontosGrupoPadres(List<GrupoDTO> listaGrupoDTO) {
        try {
            long faltanOperar = 1;
            while (faltanOperar != 0) {
                for (GrupoDTO grupo : listaGrupoDTO) {

                    if (grupo.getRango() == null) {
                        continue;

                    }
                    if (grupo.getRango().isEmpty()) {
                        continue;
                    }

                    BigDecimal montoFinal = BigDecimal.ZERO;
                    BigDecimal obtenerMonto;
                    char signo;
                    List<String> gruposOperar = Helper.traerListadeStringSeparador(grupo.getRango(), ",");

                    boolean calculoTodos = true;
                    for (String operar : gruposOperar) {
                        int grupoId = Integer.parseInt(operar.substring(1, operar.length()));
                        GrupoDTO miGrupo = listaGrupoDTO.stream().filter(t -> t.getId() == grupoId).findFirst().orElse(null);

                        if (miGrupo == null) {
                            continue;
                        }

                        if (miGrupo.getRango() != null) {
                            calculoTodos = false;
                            break;
                        }

                        obtenerMonto = listaGrupoDTO.stream().filter(t -> t.getId() == grupoId).findAny().get().getMonto();

                        signo = operar.charAt(0);
                        switch (signo) {
                            case '+':
                                montoFinal = montoFinal.add(obtenerMonto);
                                break;
                            case '-':
                                montoFinal = montoFinal.subtract(obtenerMonto);
                                break;
                        }
                    }
                    if (calculoTodos) {
                        grupo.setMonto(montoFinal);
                        grupo.setRango(null);
                        //Para Calcular el total cuando es por fechas
                        if (grupo.getOperacionDolares() != null || grupo.getOperacionSoles() != null) {
                            this.calcularTotalParaFechas(grupo, montoFinal);
                        }

                    }
                }

                faltanOperar = listaGrupoDTO.stream().filter(t -> t.getRango() != null).count();
            }

        } catch (Exception en) {
            logger.error(en.getMessage());
        }

        return listaGrupoDTO;
    }

    private byte[] traerReporte(String nombreReporte, Map<String, Object> parameters, String tipo) {
        byte[] response = null;
        JasperPrint jasperprint;
        JasperReport report;
        Connection conexion;
        jasperprint = null;
        try {
            report = (JasperReport) JRLoader.loadObject(new File(urlReportes + nombreReporte + ".jasper"));
            conexion = dataSource.getConnection();
            jasperprint = JasperFillManager.fillReport(report, parameters, conexion);
            conexion.close();
            if (tipo.equalsIgnoreCase(ExtensionReporteEnum.PDF.getValor())) {
                response = JasperExportManager.exportReportToPdf(jasperprint);
            }
            if (tipo.equalsIgnoreCase(ExtensionReporteEnum.EXCEL.getValor())) {

                response = xlsReportToArray(jasperprint);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return response;
    }

    private byte[] xlsReportToArray(JasperPrint jasperPrint) {
        byte[] bytes = null;
        try {
            JRXlsExporter jasperXlsExportMgr = new JRXlsExporter();

            ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();

            jasperXlsExportMgr.setExporterInput(new SimpleExporterInput(jasperPrint));
            jasperXlsExportMgr.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));

            SimpleXlsReportConfiguration configuracion = new SimpleXlsReportConfiguration();

            configuracion.setOnePagePerSheet(Boolean.FALSE);
            configuracion.setDetectCellType(Boolean.TRUE);
            configuracion.setRemoveEmptySpaceBetweenRows(true);
            configuracion.setWhitePageBackground(Boolean.TRUE);

            jasperXlsExportMgr.exportReport();
            bytes = xlsReport.toByteArray();
        } catch (JRException jex) {
            logger.error(jex, jex);
        }
        return bytes;
    }

    private void generarZIP(byte[] contenido, HttpServletResponse response, String nombre) {
        try {
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            ZipEntry entry = new ZipEntry(nombre);
            entry.setSize(contenido.length);
            zos.putNextEntry(entry);
            zos.write(contenido);
            zos.closeEntry();
            zos.close();
        } catch (Exception ex) {
            logger.error(Mensaje.REPORTE_ERROR_ZIP + ex.getMessage());
        }

    }

    private void generarReporte(byte[] contenido, HttpServletResponse response) {
        try {
            OutputStream os;
            os = response.getOutputStream();
            os.write(contenido);
            os.close();
        } catch (Exception ex) {
            logger.error(Mensaje.REPORTE_ERROR_ZIP + ex.getMessage());
        }
    }

    private void armarReporte(HttpServletResponse response, String tipo, Map<String, Object> parameters, String nombreArchivo) {
        ExtensionReporteEnum tipoReporte = null;
        if (tipo.equalsIgnoreCase(ExtensionReporteEnum.PDF.getValor())) {
            tipoReporte = ExtensionReporteEnum.PDF;
        }
        if (tipo.equalsIgnoreCase(ExtensionReporteEnum.EXCEL.getValor())) {
            tipoReporte = ExtensionReporteEnum.EXCEL;
        }
        if (tipo.equalsIgnoreCase(ExtensionReporteEnum.ZIP_X.name())) {
            tipoReporte = ExtensionReporteEnum.ZIP_X;
        }
        if (tipoReporte != null) {
            byte[] contenido = this.traerReporte(nombreArchivo, parameters, tipoReporte.getValor());
            response.setContentType(tipoReporte.getContent());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Cache-Control", "private, post-check=0, pre-check=0, max-age=60");
            response.setHeader("Pragma", "public");
            if (!tipoReporte.getExtensionComprimido().isEmpty()) {
                response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo + "." + tipoReporte.getExtension());
                this.generarZIP(contenido, response, nombreArchivo + tipoReporte.getExtensionComprimido());
            } else {
                this.generarReporte(contenido, response);
            }
        }
    }

    private List<GrupoDTO> traerListaGrupos(int reporteId, int monedaId) throws ExcepcionNegocio {
        List<GrupoDTO> listaGrupoDTO;
        Consulta consultaGrupo = new Consulta();
        consultaGrupo.agregaAlias(new Alias("reporte", "reporte"));
        consultaGrupo.agregaRestriccionIgual(new RestriccionIgual("reporte.id", reporteId));
        consultaGrupo.agregaEquivalencia(new Equivalencia("id", "id"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("codigoSoles", "codigoSoles"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("codigoDolares", "codigoDolares"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("rango", "rango"));

        Consulta consultaGrupoEncaje = new Consulta();
        consultaGrupoEncaje.agregaAlias(new Alias("grupo", "grupo"));
        consultaGrupoEncaje.agregaRestriccionIgual(new RestriccionIgual("grupo.reporte.id", reporteId));
        consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("grupo.id", "id"));
        consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("grupo.descripcion", "descripcion"));
        consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("operacionsoles", "operacionSoles"));
        consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("operaciondolares", "operacionDolares"));
        consultaGrupoEncaje.agregaEquivalencia(new Equivalencia("grupo.rango", "rango"));

        if (monedaId == MonedaEnum.SOLES.ordinal()) {
            consultaGrupoEncaje.agregaRestriccionSql("operacionsoles is not null");
        }
        if (monedaId == MonedaEnum.DOLARES.ordinal()) {
            consultaGrupoEncaje.agregaRestriccionSql("operaciondolares is not null");
        }

        listaGrupoDTO = grupoEncajeDAO.traerTodo(GrupoEncaje.class, consultaGrupoEncaje, GrupoDTO.class);
        listaGrupoDTO.forEach(z -> {
            if (z.getRango() != null) {
                z.setEsCabecera(true);
            }
        });
        return listaGrupoDTO;
    }

    private List<DetalleGrupoDTO> traerListaDetalleGrupo(int grupoId) throws ExcepcionNegocio {
        List<DetalleGrupoDTO> listaDetalleGrupo;
        Consulta consultaDetalle = new Consulta();
        consultaDetalle.agregaAlias(new Alias("grupo", "grupo"));
        consultaDetalle.agregaRestriccionIgual(new RestriccionIgual("grupo.id", grupoId));
        consultaDetalle.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consultaDetalle.agregaEquivalencia(new Equivalencia("tipoSaldo", "tipoSaldo"));
        consultaDetalle.agregaOrdenAscendente("id");
        listaDetalleGrupo = detalleGrupoDAO.traerTodo(Detallegrupo.class, consultaDetalle, DetalleGrupoDTO.class);

        return listaDetalleGrupo;
    }

    private List<DetalleDTO> traerDetalleAsiento(String cuenta, Date fechaFinal, int monedaId, Date fechaInicial) throws ExcepcionNegocio {
        List<DetalleDTO> listaDetalleAsiento;

        Consulta consultaDetalle = new Consulta();
        consultaDetalle.agregaAlias(new Alias("cuenta", "cuenta"));
        consultaDetalle.agregaAlias(new Alias("asiento", "asiento"));
        consultaDetalle.agregaAlias(new Alias("moneda", "moneda"));
        consultaDetalle.agregaAlias(new Alias("cuenta.tipoCuenta", "tipoCuenta"));
        consultaDetalle.agregaRestriccionLike(new RestriccionLike("cuenta.cuenta", cuenta));
        consultaDetalle.agregaRestriccionIgual(new RestriccionIgual("asiento.estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaDetalle.agregaRestriccionBetweenDate(new RestriccionBetweenDate("asiento.fecha", fechaInicial, fechaFinal));
        consultaDetalle.agregaRestriccionIgual(new RestriccionIgual("moneda.id", monedaId));
        consultaDetalle.agregaEquivalencia(new Equivalencia("debe", "debe"));
        consultaDetalle.agregaEquivalencia(new Equivalencia("haber", "haber"));
        consultaDetalle.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
        consultaDetalle.agregaEquivalencia(new Equivalencia("cuenta.tipoCuenta.id", "tipoCuentaId"));
        consultaDetalle.agregaEquivalencia(new Equivalencia("asiento.fecha", "fechaAsiento"));
        consultaDetalle.agregaOrdenAscendente("asiento.fecha");
        listaDetalleAsiento = detalleDAO.traerTodo(Detalle.class, consultaDetalle, DetalleDTO.class);

        return listaDetalleAsiento;
    }

    private void generarReporteExcelEncaje(String nombreMes, int ultimoDiaMes, int monedaId, BigDecimal tcAnterior, BigDecimal tcActual, String nombreReporte, String titulo, List<GrupoDTO> listaGrupos, HttpServletResponse response, List<BigDecimal> listaPieReporte) throws ExcepcionNegocio {
        String nombreMoneda = MonedaEnum.DOLARES.name();
        if (monedaId != MonedaEnum.DOLARES.ordinal()) {
//            tcActual = BigDecimal.ONE;
//            tcAnterior = BigDecimal.ONE;
            nombreMoneda = MonedaEnum.SOLES.name();
        }

        int posicionLlenanDatos = 12;
        boolean mostrarDatos = false;
        String nombreLibro = nombreReporte.replace(":", "");

        XSSFWorkbook libro = new XSSFWorkbook();
        XSSFSheet hoja = libro.createSheet(nombreLibro);

        //Font Tamanio 8
        Font fontHeight8 = libro.createFont();
        fontHeight8.setFontHeightInPoints((short) 8);

        XSSFCellStyle tamanio8 = libro.createCellStyle();
        tamanio8.setFont(fontHeight8);

        XSSFCellStyle estiloFont8 = libro.createCellStyle();
        estiloFont8.setFont(fontHeight8);
        estiloFont8.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle alineacionCenter = libro.createCellStyle();
        alineacionCenter.setAlignment(HorizontalAlignment.CENTER);
        alineacionCenter.setFont(fontHeight8);
        alineacionCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        Helper.colocarTodosBordesCelda(alineacionCenter);

        XSSFCellStyle textoDobleFila = libro.createCellStyle();
        textoDobleFila.setAlignment(HorizontalAlignment.CENTER);
        textoDobleFila.setFont(fontHeight8);
        textoDobleFila.setWrapText(true);
        textoDobleFila.setVerticalAlignment(VerticalAlignment.CENTER);
        Helper.colocarTodosBordesCelda(textoDobleFila);

        DataFormat formatoNumero = libro.createDataFormat();

        XSSFCellStyle numeroNormal = libro.createCellStyle();
        numeroNormal.setFont(fontHeight8);
        numeroNormal.setDataFormat(formatoNumero.getFormat("#,##0.00"));

        XSSFCellStyle numeroBorde = libro.createCellStyle();
        numeroBorde.setFont(fontHeight8);
        numeroBorde.setDataFormat(formatoNumero.getFormat("#,##0.00"));
        Helper.colocarTodosBordesCelda(numeroBorde);

        Calendar calendarHelper = Calendar.getInstance();
        if (listaGrupos.size() > 0) {

            GrupoDTO grupo = listaGrupos.get(0);
            List<ResultadoEncajePorFecha> listaFechas = grupo.getListaPorFecha();

            XSSFRow nuevaFila = hoja.createRow(posicionLlenanDatos - 3);
            XSSFCell nuevaCelda = nuevaFila.createCell(0);
            nuevaCelda.setCellValue("DESCRIP.");
            nuevaCelda.setCellStyle(alineacionCenter);

            nuevaFila = hoja.createRow(posicionLlenanDatos - 2);
            Helper.llenarCelda(0, "COD.OPER", alineacionCenter, nuevaFila);

            nuevaFila = hoja.createRow(posicionLlenanDatos - 1);
            Helper.llenarCelda(0, "DIA", alineacionCenter, nuevaFila);

            tamanio8.setAlignment(HorizontalAlignment.GENERAL);
            //CABECERA
            nuevaFila = hoja.createRow(1);
            Helper.llenarCelda(0, Mensaje.ENCAJE_BANCO, tamanio8, nuevaFila);

            nuevaFila = hoja.createRow(2);
            Helper.llenarCelda(0, nombreReporte, tamanio8, nuevaFila);

            nuevaFila = hoja.createRow(4);
            Helper.llenarCelda(0, "INSTITUCIÓN:" + Mensaje.CMAC_PAITA, tamanio8, nuevaFila);

            nuevaFila = hoja.createRow(5);
            Helper.llenarCelda(0, "PERIODO:" + nombreMes, tamanio8, nuevaFila);

            nuevaFila = hoja.createRow(6);
            Helper.llenarCelda(0, "MONEDA: (" + nombreMoneda + ")", tamanio8, nuevaFila);

            int posicionFila = posicionLlenanDatos;
            //LLENAR LA COLUMNA DE FECHAS
            int posicionFinMes = 1;

            int dia;

            for (ResultadoEncajePorFecha fecha : listaFechas) {

                calendarHelper.setTime(Helper.convertirAFecha(fecha.getFecha()));
                dia = calendarHelper.get(Calendar.DAY_OF_MONTH);
                nuevaFila = hoja.createRow(posicionFila);
                nuevaCelda = nuevaFila.createCell(0);
                nuevaCelda.setCellStyle(estiloFont8);
                nuevaCelda.setCellValue(dia);

                if (dia == ultimoDiaMes) {
                    posicionFinMes = posicionFila;
                }

                posicionFila++;
            }
            nuevaFila = hoja.createRow(posicionFila);
            Helper.llenarCelda(0, "TOTAL", alineacionCenter, nuevaFila);

            BigDecimal monto;
            String codigo;

            posicionFila = posicionLlenanDatos;
            XSSFRow fila;
            int posicionColumna = 1;
            int posicionFilaPieReporte = 0;
            for (GrupoDTO grupoDTO : listaGrupos) {
                List<ResultadoEncajePorFecha> listaSaldosPorFecha = grupoDTO.getListaPorFecha();

                if (listaSaldosPorFecha == null) {
                    continue;
                }
                BigDecimal saldoTotales = listaSaldosPorFecha.stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (saldoTotales.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                if (MonedaEnum.DOLARES.ordinal() == monedaId) {
                    codigo = grupoDTO.getOperacionDolares();
                } else {
                    codigo = grupoDTO.getOperacionSoles();
                }

                fila = hoja.getRow(posicionFila - 3);
                nuevaCelda = fila.createCell(posicionColumna);
                nuevaCelda.setCellValue(grupoDTO.getDescripcion());
                nuevaCelda.setCellStyle(textoDobleFila);

                fila = hoja.getRow(posicionFila - 2);
                nuevaCelda = fila.createCell(posicionColumna);
                nuevaCelda.setCellValue(codigo);
                nuevaCelda.setCellStyle(alineacionCenter);

                fila = hoja.getRow(posicionFila - 1);
                nuevaCelda = fila.createCell(posicionColumna);
                nuevaCelda.setCellStyle(textoDobleFila);

                for (ResultadoEncajePorFecha porFecha : listaSaldosPorFecha) {
                    fila = hoja.getRow(posicionFila);
                    nuevaCelda = fila.createCell(posicionColumna);

                    monto = porFecha.getMonto();

//                    if (posicionFinMes == posicionFila) {
//                        monto = monto.divide(tcActual, RoundingMode.HALF_UP);
//                    } else {
//                        monto = monto.divide(tcAnterior, RoundingMode.HALF_UP);
//
//                    }

                    nuevaCelda.setCellValue(monto.doubleValue());
                    nuevaCelda.setCellStyle(numeroNormal);
                    posicionFila++;
                }

                BigDecimal totalColumna = listaSaldosPorFecha.stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);

                fila = hoja.getRow(posicionFila);
                nuevaCelda = fila.createCell(posicionColumna);
                nuevaCelda.setCellValue(totalColumna.doubleValue());
                nuevaCelda.setCellStyle(numeroBorde);
                posicionFilaPieReporte = posicionFila;
                hoja.setColumnWidth(posicionColumna, 4000);
                posicionFila = posicionLlenanDatos;
                posicionColumna++;
                mostrarDatos = true;
            }
            if (!listaPieReporte.isEmpty()) {
                //Font Negrita
                Font fontNegrita = libro.createFont();
                fontNegrita.setBoldweight(Font.BOLDWEIGHT_BOLD);
                fontNegrita.setFontHeightInPoints((short) 8);

                //Negrita Texto normal
                XSSFCellStyle textoNegrita = libro.createCellStyle();
                textoNegrita.setFont(fontNegrita);
                textoNegrita.setAlignment(HorizontalAlignment.CENTER);

                posicionFilaPieReporte = posicionFilaPieReporte + 3;

                fila = hoja.createRow(posicionFilaPieReporte);
                Helper.llenarCelda(2, "SITUACION ENCAJE", textoNegrita, fila);
                hoja.addMergedRegion(new CellRangeAddress(posicionFilaPieReporte, posicionFilaPieReporte, 2, 3));
                posicionFilaPieReporte++;

                fila = hoja.createRow(posicionFilaPieReporte);
                Helper.llenarCelda(2, "ENCAJE EXIGIBLE", tamanio8, fila);
                nuevaCelda = fila.createCell(3);
                nuevaCelda.setCellValue(listaPieReporte.get(0).doubleValue());
                nuevaCelda.setCellStyle(numeroNormal);
                posicionFilaPieReporte++;

                fila = hoja.createRow(posicionFilaPieReporte);
                Helper.llenarCelda(2, "FONDOS DE ENCAJE", tamanio8, fila);
                nuevaCelda = fila.createCell(3);
                nuevaCelda.setCellValue(listaPieReporte.get(1).doubleValue());
                nuevaCelda.setCellStyle(numeroNormal);
                posicionFilaPieReporte++;

                fila = hoja.createRow(posicionFilaPieReporte);
                Helper.llenarCelda(2, "RESULTADO", tamanio8, fila);
                nuevaCelda = fila.createCell(3);
                nuevaCelda.setCellValue(listaPieReporte.get(2).doubleValue());
                nuevaCelda.setCellStyle(numeroNormal);
                posicionFilaPieReporte++;

            }
            if (!mostrarDatos) {
                XSSFRow removingRow;
                for (ResultadoEncajePorFecha fecha : listaFechas) {
                    removingRow = hoja.getRow(posicionFila);
                    hoja.removeRow(removingRow);
                    posicionFila++;
                }
                removingRow = hoja.getRow(posicionLlenanDatos - 3);
                hoja.removeRow(removingRow);
                removingRow = hoja.getRow(posicionLlenanDatos - 2);
                hoja.removeRow(removingRow);
                removingRow = hoja.getRow(posicionLlenanDatos - 1);
                hoja.removeRow(removingRow);
                removingRow = hoja.getRow(posicionFila);
                hoja.removeRow(removingRow);

            }

            hoja.setDisplayGridlines(false);
        }
        try {
            OutputStream outputStream = response.getOutputStream();
            nombreReporte = nombreReporte.replace(":", "");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment; filename=" + nombreReporte + ".xlsx");
            response.setHeader("Pragma", "public");
            libro.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    private void calcularTotalParaFechas(GrupoDTO grupo, BigDecimal montoFinal) {

        ResultadoEncajePorFecha porFecha = new ResultadoEncajePorFecha();

        porFecha.setMonto(montoFinal);
        grupo.setMonto(montoFinal);

        if (grupo.getListaPorFecha() != null) {
            grupo.getListaPorFecha().add(porFecha);
        } else {
            List<ResultadoEncajePorFecha> listaFecha = new ArrayList<>();
            listaFecha.add(porFecha);
            grupo.setListaPorFecha(listaFecha);
        }
    }

    private List<BigDecimal> calcularTotalCajaPeriodo(int reporteId, int periodoId, int cantidadDias, List<GrupoDTO> listaGrupoDTO, int monedaId) throws ExcepcionNegocio {
        List<BigDecimal> listaPieReporte = new ArrayList<>();
        if (reporteId == Variables.ID_REPORTE_CALCULO_TOTAL_CAJA_PERIODO) {
            Periodo periodoActual = periodoDAO.traerPorId(Periodo.class, periodoId);
            GrupoDTO grupoDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_TOTAL_CAJA.equalsIgnoreCase(x.getOperacionSoles())).findAny().orElse(null);
            listaPieReporte = this.calcularPiePaginaEncaje01(periodoActual, listaGrupoDTO, monedaId, cantidadDias);
            if (grupoDTO != null) {
                List<ResultadoEncajePorFecha> listaResultadoFecha = grupoDTO.getListaPorFecha();
                BigDecimal totalCajaPeriodo = listaResultadoFecha.stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalDias = new BigDecimal(cantidadDias);
                totalCajaPeriodo = totalCajaPeriodo.divide(totalDias, RoundingMode.HALF_UP);

                periodoActual.setTotalCaja(totalCajaPeriodo);
                periodoDAO.guardar(periodoActual);

            }

        }
        return listaPieReporte;
    }

    private List<BigDecimal> calcularPiePaginaEncaje01(Periodo periodoActual, List<GrupoDTO> listaGrupoDTO, int monedaId, int cantidadDias) {
        BigDecimal total;
        BigDecimal totalFondosEncaje;
        BigDecimal totalCajaPeriodo;
        GrupoDTO grupoTotalDTO;
        GrupoDTO grupoFondosEncajeDTO;
        GrupoDTO grupoCajaPeriodoDTO;
        BigDecimal diferenciaTotal = BigDecimal.ZERO;
        BigDecimal diferenciaTemporal;
        BigDecimal baseAnterior = BigDecimal.ZERO;
        BigDecimal numeroDias = new BigDecimal(cantidadDias);
        BigDecimal baseDeduccion;
        BigDecimal columnaTotalCorregido;
        BigDecimal tramo1;
        BigDecimal tramo2;
        BigDecimal encajeExijible;
        List<BigDecimal> listaPieReporte = new ArrayList<>();

        BigDecimal baseSoles = periodoActual.getBaseSoles();
        BigDecimal baseDolares = periodoActual.getBaseDolares();
        BigDecimal tasaDeduccion = periodoActual.getTasaDeduccion();
        BigDecimal tasaImplicitaMN = periodoActual.getTasaImplicitaMN();
        BigDecimal tasaAdicionalMN = periodoActual.getTasaAdicionalMN();
        BigDecimal tasaImplicitaME = periodoActual.getTasaImplicitaME();
        BigDecimal tasaObligacion = periodoActual.getTasaObligacion();

        if (monedaId == MonedaEnum.SOLES.ordinal()) {
            grupoTotalDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_COLUMNA_TOTAL.equalsIgnoreCase(x.getOperacionSoles())).findAny().orElse(null);
            grupoFondosEncajeDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_COLUMNA_FONDOS_ENCAJE.equalsIgnoreCase(x.getOperacionSoles())).findAny().orElse(null);;
            grupoCajaPeriodoDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_TOTAL_CAJA_PERIODO.equalsIgnoreCase(x.getOperacionSoles())).findAny().orElse(null);;

            total = grupoTotalDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalFondosEncaje = grupoFondosEncajeDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalCajaPeriodo = grupoCajaPeriodoDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ResultadoEncajePorFecha> listaResultadoFecha = grupoTotalDTO.getListaPorFecha();

            for (ResultadoEncajePorFecha resultado : listaResultadoFecha) {
                diferenciaTemporal = resultado.getMonto().subtract(baseSoles);
                diferenciaTotal = diferenciaTotal.add(diferenciaTemporal);
            }
            baseAnterior = baseSoles.multiply(numeroDias);
            baseDeduccion = baseAnterior.multiply(tasaDeduccion);
            columnaTotalCorregido = baseAnterior.subtract(baseDeduccion);

            tramo1 = columnaTotalCorregido.multiply(tasaImplicitaMN);
            tramo2 = diferenciaTotal.multiply(tasaAdicionalMN);

            encajeExijible = tramo1.add(tramo2);

            listaPieReporte.add(encajeExijible);
            listaPieReporte.add(totalFondosEncaje);
            listaPieReporte.add(totalFondosEncaje.subtract(encajeExijible));
            return listaPieReporte;
        }
        if (monedaId == MonedaEnum.DOLARES.ordinal()) {
            grupoTotalDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_COLUMNA_TOTAL.equalsIgnoreCase(x.getOperacionDolares())).findAny().orElse(null);
            grupoFondosEncajeDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_COLUMNA_FONDOS_ENCAJE.equalsIgnoreCase(x.getOperacionDolares())).findAny().orElse(null);;
            grupoCajaPeriodoDTO = listaGrupoDTO.stream().filter(x -> Variables.CODIGO_TOTAL_CAJA_PERIODO.equalsIgnoreCase(x.getOperacionDolares())).findAny().orElse(null);;

            total = grupoTotalDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalFondosEncaje = grupoFondosEncajeDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalCajaPeriodo = grupoCajaPeriodoDTO.getListaPorFecha().stream().map(x -> x.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);

            baseAnterior = baseDolares.multiply(numeroDias);
            BigDecimal temporalImplicita;
            if (baseAnterior.compareTo(total) == -1) {
                temporalImplicita = baseAnterior.multiply(tasaImplicitaME);
                diferenciaTemporal = total.subtract(baseAnterior);
                diferenciaTotal = diferenciaTemporal.multiply(tasaObligacion);
                encajeExijible = temporalImplicita.add(diferenciaTotal);

            } else {
                encajeExijible = total.multiply(tasaImplicitaME);

            }
            listaPieReporte.add(encajeExijible);
            listaPieReporte.add(totalFondosEncaje);
            listaPieReporte.add(totalFondosEncaje.subtract(encajeExijible));
            return listaPieReporte;
        }
        return listaPieReporte;
    }

//</editor-fold>
}
