$(document).ready(function () {
    $("#btnCancelarEditar,#btnCancelarNueva").click(function () {
        $('#formEditarCuenta').data('bootstrapValidator').resetForm();
    });
    $("#btnCancelarNueva").click(function () {
        $('#formCrearCuenta').data('bootstrapValidator').resetForm();
    });
    $("#numCuentaCrear").keydown(soloNumeros);
    $("#numCuentaEditar").keydown(soloNumeros);
    $("#cuenTaInicioPlan").keydown(soloNumeros);
    $("#txtNumCtaElegida").keydown(soloNumeros);
    $("#txtNumCuenta").keydown(soloNumeros);
    $("#tblBuscarCta").on('keydown', '.debe_haber', soloNumeros);
    $("#tblBuscarCta").on('keyup', '.debe_haber', numerosDosDecimales);
    $("#nuevoTipoCambio").keydown(soloNumeros);
    $("#numero").keydown(soloNumeros);
    $("#formEditarCuenta").bootstrapValidator({
        fields: {
            numCuentaEditar: {
                validators: {
                    notEmpty: {
                        message: 'El número de cuenta es requerido'
                    },
                    numeric: {
                        message: 'Se requiere un número'
                    }
                }
            },
            descripcionEditar: {
                validators: {
                    notEmpty: {
                        message: 'La descripcion es requerida'
                    }
                }
            }

        }
    });
    $("#formCrearCuenta").bootstrapValidator({
        fields: {
            numCuentaNueva: {
                validators: {
                    notEmpty: {
                        message: 'El número de cuenta es requerido'
                    },
                    stringLength: {
                        min: 2,
                        message: 'Se requieren dos dígitos'
                    },
                    numeric: {
                        message: 'Se requiere un número'
                    }
                }
            },
            descripcionNueva: {
                validators: {
                    notEmpty: {
                        message: 'La descripcion es requerida'
                    }
                }
            }

        }
    });
    $("#formFiltrarAsientos").bootstrapValidator({
        fields: {
            framework: 'bootstrap',
            txtFecFin: {
                validators: {
                    notEmpty: {
                        message: 'La fecha es requerida'
                    },
                    date: {
                        format: 'DD/MM/YYYY',
                        message: 'Fecha debe tener formato: 01/01/2000'
                    }
                }
            },
            txtFecInicio: {
                validators: {
                    notEmpty: {
                        message: 'La fecha es requerida'
                    },
                    date: {
                        format: 'DD/MM/YYYY',
                        message: 'Fecha debe tener formato: 01/01/2000'
                    }

                }
            }

        }
    });
    $("#formGrabarAsiento").bootstrapValidator({
        fields: {
            txtFecAsiento: {
                validators: {
                    notEmpty: {
                        message: 'La fecha es requerida'
                    }
                }
            },
            txtGlosa: {
                validators: {
                    notEmpty: {
                        message: 'La glosa es requerida'
                    }
                }
            }
        }
    });
    $("#formBuscarCtaPlan").bootstrapValidator({
        fields: {
            cuenTaInicioPlan: {
                validators: {
                    notEmpty: {
                        message: 'El número de cuenta es requerido'
                    },
                    numeric: {
                        message: 'Se requiere un número'
                    }
                }
            }
        }
    });
    $("#formularioTipoCambio").bootstrapValidator({
        fields: {
            nuevoTipoCambio: {
                validators: {
                    notEmpty: {
                        message: 'Ingrese un número'
                    },
                    numeric: {
                        message: 'Se requiere un número'
                    },
                    between: {
                        min: 0,
                        max: 10,
                        message: 'Número no válido'
                    }
                }
            }
        }
    });
    //valida cuando el usuario selecciona una fecha desde el calendario desde el calendario
    $('.datePicker-f').on('changeDate keydowm focusout', function (e) {
        $('#formFiltrarAsientos').bootstrapValidator('revalidateField', 'txtFecFin');
    });
    $('.datePicker-f').on('changeDate keydowm focusout', function (e) {
        $('#formFiltrarAsientos').bootstrapValidator('revalidateField', 'txtFecInicio');
    });
    $("#datePicker-asiento").on('changeDate', function (e) {
        $('#formGrabarAsiento').bootstrapValidator('revalidateField', 'txtFecAsiento');
    });
    $("#txtGlosa").on('change', function (e) {
        $('#formGrabarAsiento').bootstrapValidator('revalidateField', 'txtGlosa');
    });


    $(document).on('changeDate keydowm focusout', "#datePicker-fecFin", function (e) {
        $('#formReporteEncaje').bootstrapValidator('revalidateField', 'txtFecFinEncaje');
    });


});

function validarFormulariosDinamicos() {
    $("#formReporteEncaje").bootstrapValidator({
        fields: {
            txtFecFinEncaje: {
                validators: {
                    notEmpty: {
                        message: 'La fecha es requerida'
                    },
                    date: {
                        format: 'DD/MM/YYYY',
                        message: 'Fecha debe tener formato: 01/01/2000'
                    }

                }
            }

        }
    });
}

