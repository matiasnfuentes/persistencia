package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class EstadisticasServiceImpl (val especieDAO: EspecieDAO,
                               val vectorDAO: VectorDAO): EstadisticasService {

    override fun especieLider(): Especie {
       return runTrx { especieDAO.especieLider() }
    }

    override fun lideres(): List<Especie> {
        return runTrx { especieDAO.lideres() }
    }

    override fun reporteDeContagios(ubicacionId: Long): ReporteDeContagios {
        return runTrx{
            val vectoresPresentes = vectorDAO.vectoresEnUbicacion(ubicacionId)
            val vectoresInfectados = vectorDAO.vectoresInfectadosEnUbicacion(ubicacionId)
            val especieMasInfecciosaEnUbicacion = especieDAO.especieMasInfecciosa(ubicacionId)
            ReporteDeContagios(vectoresPresentes,vectoresInfectados,especieMasInfecciosaEnUbicacion.nombre)
        }
    }
}