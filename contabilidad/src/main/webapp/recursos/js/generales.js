var tiempoMensaje = 9000;
var periodoVigente;
var periodoActual;
var urlBaseGeneral = crearUrl();
$(document).ready(cargarPeriodo);
/**
 * 
 * @param {String} tipoMensaje Puede ser hide,success,danger 
 * @param {String} mensaje
 * @param {String} contenedor Elemento donde se mostrara el mensaje
 
 */
function mostrarMensajes(tipoMensaje, mensaje, contenedor) {
    $('<div id="alerta-flotante" class="alert ' + tipoMensaje + '  alter-mensaje alert-' + tipoMensaje + ' fade in"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>' + mensaje + '</div>').appendTo(contenedor);
    setTimeout(function () {
        $(".alter-mensaje").alert('close');
    }, tiempoMensaje);
}
/**
 * 
 * @param {type} tipoMensaje success- danger...
 * @param {type} mensaje Mensaje a mostrar
 * @param {type} contenedor donde se mostrara el mensaje
 */
function mostrarMensajesContenedor(tipoMensaje, mensaje, contenedor) {
    $(contenedor).html('<div class="' + tipoMensaje + ' alert alert-' + tipoMensaje + '">' + mensaje + '</div>');
}

/**
 * 
 * @param {String} mensaje Mensaje que desea mostrar
 * @param {String} contenedor Elemento donde lo desea mostrar
 * @param {String} tamanio Tamaño de la letra: h1,h2,h3,h4..
 */
function mostrarMensajeCargando(mensaje, contenedor, tamanio) {
    $(contenedor).html('<' + tamanio + ' class=" text-primary "> \n\
                            <span class=" glyphicon glyphicon-hourglass"></span> &nbsp;<span >' + mensaje + ' </span>\n\
                        </' + tamanio + '>');
}

$(".date-picker").datepicker({
    language: 'es-ES',
    autoclose: true
});
function soloNumeros(e) {

    if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
            (e.keyCode === 65 && e.ctrlKey === true) ||
            (e.keyCode >= 35 && e.keyCode <= 39)) {
        return;
    }
    if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
        e.preventDefault();
    }
}
function numerosDosDecimales(e) {
    // Backspace = 8, Enter = 13, �0? = 48, �9? = 57, �.� = 46
    if ($(this).val().indexOf('.') != -1) {
        console.log($(this).val().indexOf('.'));
        if ($(this).val().split(".")[1].length > 2) {
            if (isNaN(parseFloat(this.value)))
                return;
            this.value = parseFloat(this.value).toFixed(2);
        }
    }
    return this;

}
function diferenciaFechas(fechaInicio, fechaFin) {
    var aFecha1 = fechaInicio.split('/');
    var aFecha2 = fechaFin.split('/');
    var fFecha1 = Date.UTC(aFecha1[2], aFecha1[1] - 1, aFecha1[0]);
    var fFecha2 = Date.UTC(aFecha2[2], aFecha2[1] - 1, aFecha2[0]);
    var dif = fFecha2 - fFecha1;
    var dias = Math.floor(dif / (1000 * 60 * 60 * 24));
    return dias;
}
function llenaCombos(contenedor, url, id, value) {
    $.getJSON(url, function (data) {
        $.each(data, function (i, item) {
            $(contenedor).append('<option value="' + item[id] + '">' + item[value] + '</option>');
        });
    });
}

/**
 * 
 * @param {numero} value si no es un número retorna 0.00
 * @param {numero} decimals, cantidad de  decimales
 * @param {array} separators ['.', "'", ','] miles/millones/decimales
 * @returns numero
 */
function formatoNumero(value, decimals, separators) {
    decimals = decimals >= 0 ? parseInt(decimals, 0) : 2;
    separators = separators || ['.', "'", ','];
    var number = (parseFloat(value) || 0).toFixed(decimals);
    if (number.length <= (4 + decimals))
        return number.replace('.', separators[separators.length - 1]);
    var parts = number.split(/[-.]/);
    value = parts[parts.length > 1 ? parts.length - 2 : 0];
    var result = value.substr(value.length - 3, 3) + (parts.length > 1 ?
            separators[separators.length - 1] + parts[parts.length - 1] : '');
    var start = value.length - 6;
    var idx = 0;
    while (start > -3) {
        result = (start > 0 ? value.substr(start, 3) : value.substr(0, 3 + start))
                + separators[idx] + result;
        idx = (++idx) % 2;
        start -= 3;
    }
    return (parts.length == 3 ? '-' : '') + result;
}
/**
 * 
 * @param {value} cantidad Ej: 100
 * @returns 100.00
 */
function formatoMoneda(value) {
    return formatoNumero(value, 2, [',', '´', '.']);
}
/**
 * 
 * @param {String} url que retornara el reporte en pdf
 * @param {String} id del div que contendra el reporte
 * @param {String} btnBuscar  es el "id" del boton de buscar
 * @param {String} btnExcel es el "id" del boton excel
 * 
 * 
 */
function buscarReporte(url, id, btnBuscar, btnExcel) {
    $(btnExcel).attr('href', url + "ZIP_X");
    var options = {
        height: "600px"
    };
    $("#"+id).html("");
    var urlFinal = url + "PDF";
    $(btnBuscar).attr('disabled','disabled');
    mostrarMensajeCargando(Mensajes.general_cargando,"#"+id,"h4");
    $.ajax({
        url: urlFinal,
        cache: true,
        mimeType: 'application/pdf',
        success: function () {
            PDFObject.embed(urlFinal, "#" + id, options);
            $(btnExcel).removeClass('hide');
            $(btnBuscar).removeAttr('disabled')
        },
        
    });
}
function buscarReporteTemp(url, id, link) {
    $(link).removeClass('hide');
    $(link).attr('href', url);
    var options = {
        height: "600px"
    };
    var resultado = PDFObject.embed(url, "#" + id, options);

}
/**
 * 
 * @param {Object} locales
 * @param {Object} recibidas
 * @returns {Boolean} true: si las variables locales estan dentro de las recibidas
 */

function VerificarVariablesRequeridas(locales, recibidas) {
    try {
        for (var propiedad in locales) {
            if (!recibidas.hasOwnProperty(propiedad))
                throw new Error("Propiedad '" + propiedad + "' no encontrada");
        }
        return true;
    } catch (err) {
        console.error(err.stack);
        return false;
    }
}
function cargarPeriodo() {
    $.getJSON("/contabilidad/utilitario/periodoVigente", function (data) {
        $("#periodoActual").html(data.descripcion);
        $("#periodoAct").html(data.descripcion);
        periodoVigente = data.id;
        periodoActual = data;
    });
}
var errores = {
    eGeneral: 'OCURRIÓ UN ERROR INESPERADO COMUNÍQUESE CON TI'
};
function sumFormatter(data) {
    var field = this.field;
    var total_sum = 0;
    var i = 0;
    total_sum = data.reduce(function (sum, row) {
        i++;
        return (sum) + ((row[field]) || 0);
    }, 0);
    if (field === "debe")
        totalDebe = formatoMoneda(total_sum);
    else
        totalHaber = formatoMoneda(total_sum);
    if (total_sum === 0 && i > 0)
        total_sum = "0.00";
    if (total_sum > 0 && i > 0)
        return formatoMoneda(total_sum);
    else
        return total_sum;
}
function ocultarMostrarElementos(elementosMostrar, elementosOcultar) {
    var ocultar = $.grep(elementosOcultar, function (item) {
        return $.inArray(item, elementosMostrar) == -1;
    });
    $.each(ocultar, function (i, item) {
        $("#" + item).addClass('hide');
    });
    $.each(elementosMostrar, function (i, item) {
        $("#" + item).removeClass('hide');
    });
}

var enumProcedencia = {
    CONTABILIDAD: 'CONTABILIDAD',
    SYSONE: 'SYSONE',
    SIAFC: 'SIAFC',
    PROVISION: 'PROVISION'
};
var estadosSistema = {
    ACTIVO: 1,
    INACTIVO: 2,
    PENDIENTE: 3,
    INTEGRADO: 4
};
/**
 * 
 * @type String 
 * <ul>
 * <li>EXITO</li>
 * <li>ERROR</li>
 * <li>ALERTA</li>
 * <li>OCULTO</li>
 * </ul>
 */
var tipoMensaje = {
    EXITO: "success",
    ERROR: "danger",
    ALERTA: "warning",
    OCULTO: "hide"
};
function crearUrl() {
    var host = window.location.origin;
    var base = window.location.pathname.split("/");

    return host + "/" + base[1] + "/";
}
/**
 * 
 * @param {type} responseTxt
 * @param {type} statusTxt
 * @param {type} xhr
 * @param {type} functionSuccess Funcion que hará si se carga correctamente
 *
 */
function controlLoad(responseTxt, statusTxt, xhr, functionSuccess) {
    if (statusTxt == 'success') {
        functionSuccess();
    }
    if (statusTxt == 'error') {
        if (xhr.status == 404) {
            mostrarMensajes(tipoMensaje.ERROR, Mensajes.general_url_no_existe, 'body');
            return;
        }
        mostrarMensajes(tipoMensaje.ERROR, "Error: " + xhr.statusText, 'body');

    }
}
function mostrarIndex(value, row, index) {
    return index + 1;
}