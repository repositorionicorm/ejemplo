/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author hnole
 */
public class Helper {

    public static boolean esNumerico(String valor) {
        for (int i = 0; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean esNuloVacio(String valor) {
        if (valor == null) {
            return true;
        }
        if (valor.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean esNuloVacio(ArrayList lista) {
        if (lista == null) {
            return true;
        }
        if (lista.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean esPar(String valor) {
        return (valor.length() % 2 == 0);
    }

    public static boolean between(Date fecha, Date fechaInicio, Date fechaFinal) {
        if (fecha != null && fechaInicio != null && fechaFinal != null) {
            if (fecha.after(fechaInicio) && fecha.before(fechaFinal)) {
                return true;
            } else if (fecha.equals(fechaInicio) || fecha.equals(fechaFinal)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static Date convertirAFecha(String fecha) throws ExcepcionNegocio {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formato.setLenient(false);

            return formato.parse(fecha);
        } catch (Exception ex) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_FORMATO_FECHA_INCORRECTA, Mensaje.TIPO_ALERTA);
        }
    }

    public static String convertirAFecha(Date fecha) throws ExcepcionNegocio {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formato.format(fecha);
        } catch (Exception ex) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_FORMATO_FECHA_INCORRECTA, Mensaje.TIPO_ALERTA);
        }

    }

    public static boolean existeDato(List<Integer> listaBase, List<Integer> listaEvaluar) {
        boolean existe = true;
        for (Integer propiedadEvaluar : listaEvaluar) {
            if (listaBase.stream().filter(x -> x == propiedadEvaluar).findFirst().orElse(null) == null) {
                existe = false;
                break;
            }
        }

        return existe;
    }

    public static String transformaCuentaMoneda(String cuenta, char moneda) {
        String cuentaATransformar = cuenta;
        if (cuentaATransformar.length() > 3) {
            char[] temp = cuentaATransformar.toCharArray();
            temp[2] = moneda;
            cuentaATransformar = new String(temp);
        }
        return cuentaATransformar;
    }

    public static List<Integer> traerListadeIntegerSeparador(String cadena, String separador) {
        String[] asientos = cadena.split(separador);

        List<Integer> asientosId = new ArrayList<>();
        for (String asiento : asientos) {
            asientosId.add(Integer.parseInt(asiento));
        }
        return asientosId;
    }

    public static List<String> traerListadeStringSeparador(String cadena, String separador) {
        String[] cadenas = cadena.split(separador);

        List<String> items = new ArrayList<>();
        for (String item : cadenas) {
            items.add(item);
        }
        return items;
    }

    public static boolean datoEsNumerico(String cadena) {
        return (cadena.matches("[0-9]+.[0-9]*") && cadena.equals("") == false);
    }

    public static MesEnum traerMesPorId(int id) {
        for (MesEnum mes : MesEnum.values()) {
            if (mes.getId() == id) {
                return mes;
            }
        }
        return null;
    }

    public static void colocarTodosBordesCelda(XSSFCellStyle estiloCelda) {
        estiloCelda.setBorderTop(BorderStyle.THIN);
        estiloCelda.setBorderBottom(BorderStyle.THIN);
        estiloCelda.setBorderLeft(BorderStyle.THIN);
        estiloCelda.setBorderRight(BorderStyle.THIN);
    }

    public static void llenarCelda(int columnaCelda, String texto, XSSFCellStyle estiloCelda, XSSFRow fila) {
        
        XSSFCell nuevaCelda = fila.createCell(columnaCelda);
        nuevaCelda.setCellValue(texto);
        nuevaCelda.setCellStyle(estiloCelda);
    }
}
