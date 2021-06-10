package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class VectorServiceImpl(var vectorDAO: VectorDAO,
                        val especieDAO : EspecieDAO,
                        val ubicacionDAO: UbicacionDAO): VectorService {

    override fun infectar(vectorId: Long, especieId: Long) {
        runTrx{
            val vector = vectorDAO.recuperar(vectorId)
            if(!vector.estaContagiadoCon(especieId)){
                val especie = especieDAO.recuperar(especieId)
                vector.infectar(especie)
                vectorDAO.actualizar(vector)
            }
        }
    }

    override fun enfermedades(vectorId: Long): List<Especie> {
        return runTrx {
            val vector = vectorDAO.recuperar(vectorId)
            vector.especiesPadecidas
        }
    }

    override fun crear(tipo: TipoDeVector, ubicacionId: Long): Vector {
        return runTrx{
            val ubicacion = ubicacionDAO.recuperar(ubicacionId)
            val vector = Vector(tipo,ubicacion)
            vectorDAO.crear(vector)
        }
    }

    override fun recuperar(vectorId: Long): Vector {
        return runTrx{ vectorDAO.recuperar(vectorId) }
    }

    override fun recuperarTodos(): List<Vector> {
        return runTrx{ vectorDAO.recuperarATodos() }
    }
}