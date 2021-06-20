package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Randomizador
import ar.edu.unq.eperdemic.modelo.TipoDeCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.persistencia.dao.ConexionesDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class UbicacionServiceImpl(var ubicacionDAO : UbicacionDAO,
                           var vectorDAO: VectorDAO,
                           val conexionesDAO: ConexionesDAO): UbicacionService {

    override fun mover(vectorId: Long, ubicacionid: Long) {
        runTrx{
            val ubicacion = ubicacionDAO.recuperar(ubicacionid)
            val vector = vectorDAO.recuperar(vectorId)
            val ruta = conexionesDAO.rutaAUbicacion(vector,ubicacionid)
            if (ruta.isNotEmpty()){
                vector.cambiarUbicacion(ubicacion)
            } else{
                throw UbicacionNoAlcanzable("Ubicacion no alcanzable")
            }
            if(vector.puedeContagiar()) {
                ruta.forEach { id -> val vectores = vectorDAO.recuperarVectoresDeUbicacion(id)
                                    contagiarVectores(vectores,vector)
                }
            }
        }
    }

    override fun expandir(ubicacionId: Long) {
        runTrx{
            val vectores = vectorDAO.recuperarVectoresDeUbicacion(ubicacionId)
            val vectorDeContagio = Randomizador.getRandomVectorInfectado(vectores)
            if (vectorDeContagio!=null){
                contagiarVectores(vectores,vectorDeContagio)
            }
        }
    }

    private fun contagiarVectores(vectores :List<Vector>, vector: Vector){
        vectores.forEach {
                            it.serContagiado(vector)
                            vectorDAO.actualizar(it)
        }
    }

    override fun crear(nombreUbicacion: String): Ubicacion {
        return runTrx{
                    val ubicacion = ubicacionDAO.crear(nombreUbicacion)
                    conexionesDAO.crearUbicacion(ubicacion)
                    ubicacion
        }
    }

    override fun recuperarTodos(): List<Ubicacion> {
        return runTrx{ ubicacionDAO.recuperarATodos() }
    }

    override fun recuperar(id: Long): Ubicacion {
        return runTrx{ ubicacionDAO.recuperar(id) }
    }

    override fun conectar(ubicacion1: Long, ubicacion2: Long, tipoCamino: TipoDeCamino) {
        runTrx {
                val origen = ubicacionDAO.recuperar(ubicacion1)
                val destino = ubicacionDAO.recuperar(ubicacion2)
                conexionesDAO.conectar(origen.id!!,destino.id!!,tipoCamino)
        }
    }

    override fun conectados(ubicacionId: Long): List<Ubicacion> {
        return runTrx {
                        val ubicacion = ubicacionDAO.recuperar(ubicacionId)
                        conexionesDAO.conectados(ubicacion.id!!)
        }
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        return runTrx {
                    val vector = vectorDAO.recuperar(vectorId)
                    conexionesDAO.capacidadDeExpansion(vector,movimientos)
        }
    }

}
