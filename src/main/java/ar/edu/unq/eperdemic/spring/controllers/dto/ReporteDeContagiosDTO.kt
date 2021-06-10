package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(val vectoresPresentes:Int,
                            val vectoresInfectados:Int,
                            val nombreDeEspecieMasInfecciosa: String,
                            val nombreDeUbicacion: String,
                            val nombreDelEquipo: String) {
    companion object {
        fun desdeModelo(reporte: ReporteDeContagios, nombreDeUbicacion: String, nombreDelEquipo: String) =
            ReporteDeContagiosDTO(
                reporte.vectoresPresentes,
                reporte.vectoresInfectados,
                reporte.nombreDeEspecieMasInfecciosa!!,
                nombreDeUbicacion,
                nombreDelEquipo
            )
    }
}