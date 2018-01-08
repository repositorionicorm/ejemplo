/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionExcel;

/**
 *
 * @author dev-out-02
 */
@Service
public class IntegracionExcelImpl implements IIntegracionExcel {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public List<DetalleDTO> generarDetalle(MultipartFile archivoExcel) throws ExcepcionNegocio {
        int i = 0;
        int monedaId;
        String nombre=archivoExcel.getOriginalFilename();

        if(!nombre.endsWith(".xlsx")){
            throw  new ExcepcionNegocio(Mensaje.INTEGRACION_EXCEL_ARCHIVO_NO_EXCEL, Mensaje.TIPO_ALERTA);
        }
        List<DetalleDTO> listaDetalle = new ArrayList<>();
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(archivoExcel.getInputStream());
            XSSFSheet worksheet = workBook.getSheetAt(0);
            while (i < worksheet.getLastRowNum()) {
                i++;
                DetalleDTO detalle = new DetalleDTO();
                XSSFRow row = worksheet.getRow(i);

                if (row == null) {
                    continue;
                }
                Cell celdaDebe = row.getCell(2);
                BigDecimal debe = this.obtenerValorCelda(celdaDebe);
                debe = debe.setScale(2, RoundingMode.HALF_UP);

                Cell celdahaber = row.getCell(3);
                BigDecimal haber = this.obtenerValorCelda(celdahaber);
                haber = haber.setScale(2, RoundingMode.HALF_UP);

                if (debe.signum() == 0 && haber.signum() == 0) {
                    continue;
                }

                Cell celdaCuenta = row.getCell(0);
                if (celdaCuenta == null) {
                    throw new ExcepcionNegocio(Mensaje.INTEGRACION_EXCEL_CUENTA_VACIA + (i + 1), Mensaje.TIPO_ALERTA);
                }
                int tipo = celdaCuenta.getCellType();

                String cuenta = "";
                switch (tipo) {
                    case XSSFCell.CELL_TYPE_BLANK:
                        throw new ExcepcionNegocio(Mensaje.INTEGRACION_EXCEL_CUENTA_VACIA + (i + 1), Mensaje.TIPO_ALERTA);

                    case XSSFCell.CELL_TYPE_NUMERIC:
                        cuenta = String.valueOf(new BigDecimal(celdaCuenta.getNumericCellValue()));
                        break;
                    case XSSFCell.CELL_TYPE_STRING:
                        cuenta = celdaCuenta.getStringCellValue();
                        if(!Helper.datoEsNumerico(cuenta)){
                            throw new ExcepcionNegocio("Fila "+(i+1)+" : "+Mensaje.CUENTA_NO_EXISTE, Mensaje.TIPO_ALERTA);
                        }
                        break;
                }
                monedaId=Character.getNumericValue(cuenta.charAt(2)) ;
                System.out.println(monedaId);
                detalle.setMonedaId(monedaId);
                detalle.setCuentaCuenta(cuenta);
                detalle.setDebe(debe);
                detalle.setHaber(haber);
                listaDetalle.add(detalle);
            }

            workBook.close();
        } catch (ExcepcionNegocio exn) {
            logger.error(exn.getMessage() + (i + 1));
            throw exn;
        } catch (IllegalStateException iex) {
            logger.error(iex.getMessage());
            throw new ExcepcionNegocio(Mensaje.INTEGRACION_EXCEL_DATO_INCORRECTO + (i + 1), Mensaje.TIPO_ALERTA);

        } catch (Exception ex) {
            logger.error(ex.getMessage()+" Linea "+(i+1));
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL , Mensaje.TIPO_ERROR);
        }

        return listaDetalle;
    }

    private BigDecimal obtenerValorCelda(Cell celda) throws Exception {
        BigDecimal valor = BigDecimal.ZERO;
        
        if (celda != null) {
            int tipoHaber = celda.getCellType();

            switch (tipoHaber) {
                case XSSFCell.CELL_TYPE_NUMERIC:
                    valor = new BigDecimal(String.valueOf(celda.getNumericCellValue()));
                    break;
                case XSSFCell.CELL_TYPE_STRING:
                    String cadena=celda.getStringCellValue();
                    if(!Helper.datoEsNumerico(cadena)){
                         int fila=celda.getRowIndex()+1;
                         int columna=celda.getColumnIndex()+1;
                        throw new ExcepcionNegocio(Mensaje.INTEGRACION_EXCEL_CAMPO_NO_NUMERICO+" la fila "+fila+" , columna "+columna, Mensaje.TIPO_ALERTA);
                    }
                    valor = new BigDecimal(cadena);
                    
                    break;
            }
        }
        return valor;
    }

}
