package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

interface EstadisticasService {

    fun especieLider(): Especie
    fun lideres(): List<Especie>
    fun reporteDeContagios(ubicacionId: Long) : ReporteDeContagios

}