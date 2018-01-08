function loadEstadoResultados() {
    $(".barraMenu").removeClass('active');
    $("#menuReportes").addClass('active');
    $("#btnReporteEstados").click(btnTraerReporteEstados);

}
function btnTraerReporteEstados() {
    var periodoId = $("#cmbPeriodo").val();
    var reporteId = $("#cmbReporteTipo").val();
    $("#mensajeReporteEstado").removeClass('hide');
    $("#contenedorTabla").addClass('hide');
    mostrarMensajeCargando(Mensajes.general_cargando, '#mensajeReporteEstado', "h4");
    var nombreReporte = $("#cmbReporteTipo option:selected").text();
    var nombrePeriodo = $("#cmbPeriodo option:selected").text();
    $("#estadosTabla").bootstrapTable('destroy');
    $("#tituloReporte").html(nombreReporte+' - '+nombrePeriodo);
    $.ajax({
        type: 'POST',
        url: urlBaseGeneral + "reportes/traerReporteEstado/" + periodoId + "/" + reporteId,
        success: function (data, textStatus, jqXHR) {
            if (data.tipoMensaje == tipoMensaje.EXITO) {
                $("#contenedorTabla").removeClass('hide');
                $("#mensajeReporteEstado").addClass('hide');
                $("#estadosTabla").bootstrapTable({data:data.listaObjetos});
                
                $("#generarArchivoExcel").attr('href', urlBaseGeneral + 'reportes/getExcelReporteEstado/' + nombreReporte + "/" + nombrePeriodo + "/" + reporteId + "/" + periodoId);
                return;
            }
            mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeReporteEstado");


        },
        error: function (jqXHR, textStatus, errorThrown) {
            mostrarMensajesContenedor(tipoMensaje.ERROR, Mensajes.general_error_inesperado, "#mensajeReporteEstado");
        }
    });
}

function formatoDescripcion(value, row, index) {
    if (row.esCabecera) {
        return '<strong class="font-12">' + value + '</strong>'
    }

    return '&nbsp;&nbsp;&nbsp;&nbsp;' + value;

}