function loadEncaje() {
    configurarFechas();
    validarFormulariosDinamicos();
    $("#btnTraerReporteEncaje").click(consultarEncaje);
    $(".barraMenu").removeClass('active');
     $("#menuReportes").addClass('active');
}

function configurarFechas() {
    $("#datePicker-fecFin").datepicker({
        language: 'es-ES',
        autoclose: true,
        startDate: periodoActual.fechaInicialString,
        endDate: periodoActual.fechaFinalString
    });

}

function consultarEncaje(e) {
    e.preventDefault();
    var $formulario = $("#formReporteEncaje").data("bootstrapValidator");
    var $reporteId = $("#cmbReporteTipo option:selected").val();
    var $monedaId = $("#cmbMoneda option:selected").val();
    var $fecha = $("#txtFecFinEncaje").val();
    $formulario.validate();
    $("#contenedorTabla").addClass('hide');
    var $link = urlBaseGeneral + 'reportes/traerReporteEncaje/' + $reporteId + "/" + $monedaId + "?fecha=" + $fecha;
    if ($formulario.isValid()) {
            window.open( $link, '_blank');
    }

}


function construirTabla(data) {
    $.each(data.listaObjetos, function (i, item) {
        console.log(item);
    })
}