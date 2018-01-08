function cargarPantallaEstadoResultados() {
    cargarPantalla(urlBaseGeneral + "reportes/reporteEstadoResultados/",loadEstadoResultados);
}
function cargarPantallaEncaje() {
    cargarPantalla(urlBaseGeneral + "reportes/reporteEncaje/",loadEncaje);
}
function cargarPantallaLibroDiario() {
    cargarPantalla(urlBaseGeneral + "reportes/diario/", loadLibroDiario);
}
function cargarPantallaLibroMayor() {
    cargarPantalla(urlBaseGeneral + "reportes/mayor/", loadLibroMayor);
}
function cargarPantallaBalanceComprobacion() {
    cargarPantalla(urlBaseGeneral + "reportes/balance/", loadReporteBalanceComprobacion);
}
function cargarPantallaBalanceSituacion() {
    cargarPantalla(urlBaseGeneral + "reportes/balanceSituacion/", loadReporteSituacion);
}
function cargarPantallaConfiguracionReportes(){
    cargarPantalla(urlBaseGeneral+"reportes/configuracion/",loadReporteConfiguracion);
}

function cargarPantalla(url, loadPantalla) {
    $("#contenido").html("");
    $("#contenidoDinamico").load(url, function (response, status, xhr) {
        if (status === 'success') {
            loadPantalla();
            return;
        }
        mostrarMensajesContenedor('warning', Mensajes.general_error_vista, '#contenidoDinamico');
    });
}