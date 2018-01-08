$(document).ready(inicioRenumeracion);
function inicioRenumeracion() {
    $("#btnRenumerar").click(renumerar);
    $("#linkRenumeracion").click(function (e) {
        e.preventDefault();
        $("#modalRenumeraracion").modal({
            backdrop: 'static',
            keyboard: false
        });
        $("#mensajeRenumeracion").addClass('hide');
        $("#mensajeRenumeracion").removeClass('alert-warning');
        $("#mensajeRenumeracion").removeClass('alert-success');
        $("#mensajeRenumeracion").removeClass('alert-danger');
        $("#fechaRenumeracion").removeClass('hide');
        $("#modalRenumeraracion").modal('show');
        cargarUltimaFechaRenumeracion();

    });

}
function renumerar() {
    $("#renumeracionProcesando").removeClass('hide');
    $("#cerrarProcesandoRenumeracion").addClass('hide');
    $("#mensajeRenumeracion").addClass('hide');
    $("#fechaRenumeracion").addClass('hide');
    $("#btnRenumerar").attr('disabled', 'disabled');
    $("#btnCerrarRenumerar").attr('disabled', 'disabled');
    $("#textProcesando").html("Procesando Renumeración...");
    $.ajax({
        url: urlBaseGeneral+'asientos/renumerar',
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            $("#cerrarProcesandoRenumeracion").addClass('hide');
            $("#renumeracionProcesando").addClass('hide');
            $("#cerrarProcesandoRenumeracion").removeClass('hide');
            $("#mensajeRenumeracion").removeClass('hide');
            $("#mensajeRenumeracion").addClass('alert-' + data.tipoMensaje);
            $("#mensajeRenumeracion").html(data.mensaje);
            $("#btnRenumerar").removeAttr('disabled');
            $("#btnCerrarRenumerar").removeAttr('disabled');
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#renumeracionProcesando").addClass('hide');
            $("#cerrarProcesandoRenumeracion").removeClass('hide');
            $("#mensajeRenumeracion").removeClass('hide');
            $("#mensajeRenumeracion").addClass('alert-danger');
            $("#mensajeRenumeracion").html(errores.eGeneral);
        }
    });
}
function cargarUltimaFechaRenumeracion() {
    $("#btnRenumerar").attr('disabled','disabled');
    $("#renumeracionProcesando").removeClass('hide');
    $("#textProcesando").html("Espere, cargando datos...");
    $.ajax({
        url: urlBaseGeneral+'asientos/fechaRenumeracion',
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            $("#btnRenumerar").removeAttr('disabled');
            $("#renumeracionProcesando").addClass('hide');
            if(data.tipoMensaje!="hide"){
                $("#btnRenumerar").attr('disabled','disbaled');
            }
            mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#errorNumeracion");
            $("#fechaRenumeracion").html(data.objeto);

        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#btnRenumerar").attr('disabled','disbaled');
            $("#renumeracionProcesando").addClass('hide');
             mostrarMensajesContenedor("danger",jqXHR.responseText, "#errorNumeracion");
        }
    });
}