package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class EspecieServiceImpl(val especieDAO: EspecieDAO,
                         val ubicacionDAO: UbicacionDAO) : EspecieService {

    override fun cantidadDeInfectados(especieId: Long): Int {
        return runTrx{
            val especie = especieDAO.recuperar(especieId)
            especie.cantidadDeInfectados
        }
    }
    override fun recuperar(id: Long): Especie {
        return runTrx{ especieDAO.recuperar(id)}
    }

    override fun recuperarTodos(): List<Especie> {
        return runTrx { especieDAO.recuperarATodos() }
    }

    override fun esPandemia(especieId: Long): Boolean {
        return runTrx {
                val cantidadDeLocaciones = ubicacionDAO.cantidadDeUbicaciones()
                val ubicacionesDeLaEspecie = especieDAO.cantidadDeUbicacionesDeLaEspecie(especieId)
               (ubicacionesDeLaEspecie / cantidadDeLocaciones) > 0.5
        }
    }

}