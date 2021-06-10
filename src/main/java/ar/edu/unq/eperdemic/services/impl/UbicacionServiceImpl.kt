package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Randomizador
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class UbicacionServiceImpl(var ubicacionDAO : UbicacionDAO,
                           var vectorDAO: VectorDAO ): UbicacionService {

    override fun mover(vectorId: Long, ubicacionid: Long) {
        runTrx{
            val ubicacion = ubicacionDAO.recuperar(ubicacionid)
            val vector = vectorDAO.recuperar(vectorId)
            vector.cambiarUbicacion(ubicacion)
            if(vector.puedeContagiar()) {
                val vectores = vectorDAO.recuperarATodos()
                contagiarVectores(vectores,vector,ubicacionid)
            }
        }
    }

    override fun expandir(ubicacionId: Long) {
        runTrx{
            val vectores = vectorDAO.recuperarATodos()
            val vectorDeContagio = Randomizador.getRandomVectorInfectado(vectores,ubicacionId)
            if (vectorDeContagio!=null){
                contagiarVectores(vectores,vectorDeContagio,ubicacionId)
            }
        }
    }

    private fun contagiarVectores(vectores :List<Vector>, vector: Vector, ubicacionId: Long){
        val vectoresAContagiar = vectores
                                    .filter { (it.ubicacion).id == ubicacionId && it.id!! != vector.id!!}
                                    .toMutableList()
        vectoresAContagiar.forEach {
                                        it.serContagiado(vector)
                                        vectorDAO.actualizar(it)
        }
    }

    override fun crear(nombreUbicacion: String): Ubicacion {
        return runTrx{ ubicacionDAO.crear(nombreUbicacion) }
    }

    override fun recuperarTodos(): List<Ubicacion> {
        return runTrx{ ubicacionDAO.recuperarATodos() }
    }

    override fun recuperar(id: Long): Ubicacion {
        return runTrx{ ubicacionDAO.recuperar(id) }
    }

}
