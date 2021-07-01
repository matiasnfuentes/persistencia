package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos

class PatogenoServiceImpl(val patogenoDAO: PatogenoDAO,
                          val especieDAO: EspecieDAO,
                          val ubicacionDAO: UbicacionDAO,
                          val alarmaDeEventos: AlarmaDeEventos
) : PatogenoService {

    override fun crear(patogeno: Patogeno): Patogeno {
        return runTrx { patogenoDAO.crear(patogeno) }
    }

    override fun recuperar(id: Long): Patogeno {
        return runTrx { patogenoDAO.recuperar(id) }
    }

    override fun recuperarTodos(): List<Patogeno> {
        return runTrx { patogenoDAO.recuperarATodos() }
    }

    override fun agregarEspecie(id: Long, nombre: String,  ubicacionId: Long): Especie {
        return runTrx {
                        val patogenoDeLaEspecie = patogenoDAO.recuperar(id)
                        val ubicacion = ubicacionDAO.recuperar(ubicacionId)
                        val especieNueva = patogenoDeLaEspecie.crearEspecie(nombre,ubicacion)
                        val especiePeristida = especieDAO.crear(especieNueva)
                        alarmaDeEventos.notificar(especiePeristida)
                        especiePeristida
        }
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        return runTrx { especieDAO.especiesDelPatogeno(patogenoId) }
    }

}