$(document).ready(cargarModal);

function cargarModal() {
    $("#btnBalance").click(procesarBalance);
    $("#linkMayorizar").click(function (e) {
        e.preventDefault();
        $("#modalMayorizar").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#mensajeMayorizar").addClass('hide');
        $("#mensajeMayorizar").removeClass('alert-warning');
        $("#mensajeMayorizar").removeClass('alert-success');
        $("#mensajeMayorizar").removeClass('alert-danger');
        $("#btnBalance").attr('disabled', 'disabled');

        $("#modalMayorizar").modal('show');
        ultimaFechaBalance();
    });

}

function procesarBalance() {
    $("#mayorizarProcesando").removeClass('hide');
    $("#fechaBalance").addClass('hide');
    $("#cerrarProcesando").addClass('hide');
    $("#btnBalance").attr('disabled', 'disabled');
    $("#btnCerrarBalance").attr('disabled', 'disabled');
    $("#mensajeMayorizar").addClass('hide');
    $.ajax({
        url: urlBaseGeneral+"proceso/mayorizar",
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            $("#cerrarProcesando").removeClass('hide');
            $("#mayorizarProcesando").addClass('hide');
            $("#cerrarProcesando").removeClass('hide');
            $("#mensajeMayorizar").removeClass('hide');
            $("#mensajeMayorizar").addClass('alert-' + data.tipoMensaje);
            $("#mensajeMayorizar").html(data.mensaje);
            $("#btnBalance,#btnCerrarBalance").removeAttr('disabled');    
      

        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#mayorizarProcesando").addClass('hide');
            $("#cerrarProcesando").removeClass('hide');
            $("#mensajeMayorizar").removeClass('hide');
            $("#mensajeMayorizar").addClass('alert-danger');
            $("#mensajeMayorizar").html(errores.eGeneral);
        }
    });
}

function ultimaFechaBalance() {
    $("#fechaBalance").removeClass('hide');
    $.ajax({
        url: urlBaseGeneral+'asientos/fechaBalance',
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            if (data.tipoMensaje != "hide") {
                $("#btnBalance").attr('disabled', 'disbaled');
            }
            $("#btnBalance").removeAttr('disabled');
            $("#fechaBalance").html(data.objeto);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#btnBalance").attr('disabled', 'disbaled');
            mostrarMensajesContenedor("danger", jqXHR.responseText, "#mensajeMayorizar");
        }
    });
}