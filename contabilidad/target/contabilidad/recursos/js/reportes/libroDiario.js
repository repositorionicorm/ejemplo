function loadLibroDiario(){
    $(".barraMenu").removeClass('active');
     $("#menuReportes").addClass('active');
    $("#btnFiltrarListaAsientos").click(mostrarReporte);

    $("#cmbPeriodo").change(function(){
        $("#btnExportarExcel").addClass('hide');
    });
    $("#cmbMoneda").change(function(){
        $("#btnExportarExcel").addClass('hide');
    });
}
function mostrarReporte() {
    var periodo=$("#cmbPeriodo").val();
    var monedaId=$("#cmbMoneda").val();
    var url= "/contabilidad/reportes/reporteDiario/"+ periodo+"/"+monedaId+"/";
    var id="rptDiario"; 
    buscarReporte(url,id,"#btnFiltrarListaAsientos","#btnExportarExcel");
}