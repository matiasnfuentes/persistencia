package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.eventos.Evento
import ar.edu.unq.eperdemic.persistencia.dao.EventDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class FeedServiceImpl(val eventDAO: EventDAO,
                      val vectorDAO: VectorDAO,
                      val patogenoDAO: PatogenoDAO,
                      val ubicacionDAO: UbicacionDAO): FeedService {

    override fun feedPatogeno(patogenoID: Long): List<Evento> {
        return runTrx{
            val patogeno = patogenoDAO.recuperar(patogenoID)
            eventDAO.feedPatogeno(patogeno)
        }
    }

    override fun feedVector(vectorId: Long): List<Evento> {
        return runTrx{
            val vector = vectorDAO.recuperar(vectorId)
            eventDAO.feedVector(vector)
        }
    }

    override fun feedUbicacion(ubicacionId: Long): List<Evento> {
        return runTrx{
            val ubicacion = ubicacionDAO.recuperar(ubicacionId)
            eventDAO.feedUbicacion(ubicacion)
        }
    }
}