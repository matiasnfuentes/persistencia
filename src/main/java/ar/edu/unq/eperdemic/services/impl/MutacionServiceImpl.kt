package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.eventos.TipoMutacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos

class MutacionServiceImpl(val mutacionDAO: MutacionDAO,
                          val especieDAO: EspecieDAO,
                          val alarmaDeEventos:AlarmaDeEventos): MutacionService {

    override fun mutar(especieId: Long, mutacionId: Long): Especie {
        return runTrx {
                val especie = especieDAO.recuperar(especieId)
                val mutacion = recuperar(mutacionId)
                especie.mutar(mutacion)
                especieDAO.actualizar(especie)
                alarmaDeEventos.notificar(especie, mutacion)
                especie
        }
    }

    override fun crear(mutacion: Mutacion): Mutacion {
        return runTrx { mutacionDAO.crear(mutacion) }
    }

    override fun recuperar(mutacionId: Long): Mutacion {
        return runTrx { mutacionDAO.recuperar(mutacionId) }
    }

    override fun recuperarTodos(): List<Mutacion> {
        return runTrx { mutacionDAO.recuperarATodos() }
    }
}