package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.TipoContagio
import ar.edu.unq.eperdemic.modelo.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.persistencia.dao.ConexionesDAO
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos

class UbicacionServiceImpl(var ubicacionDAO : UbicacionDAO,
                           var vectorDAO: VectorDAO,
                           val conexionesDAO: ConexionesDAO,
                           val especieDAO: EspecieDAO,
                           val especieService: EspecieService,
                           val alarmaEventos:AlarmaDeEventos ): UbicacionService {

    override fun mover(vectorId: Long, ubicacionid: Long) {
        runTrx{
            val ubicacion = ubicacionDAO.recuperar(ubicacionid)
            val vector = vectorDAO.recuperar(vectorId)
            val ruta = conexionesDAO.rutaAUbicacion(vector,ubicacion)
            if (ruta.isNotEmpty()){
                vector.cambiarUbicacion(ubicacion)
                alarmaEventos.notificar(vector,ubicacion)
            } else{
                throw UbicacionNoAlcanzable("Ubicacion no alcanzable")
            }
            if(vector.puedeContagiar()) {
                ruta.forEach { id -> val vectores = vectorDAO.recuperarVectoresDeUbicacion(id).filter { it.id != vectorId }
                                     contagiarVectores(vectores,vector,id)
                }
            }
        }
    }

    override fun expandir(ubicacionId: Long) {
        runTrx{
            val vectores = vectorDAO.recuperarVectoresDeUbicacion(ubicacionId)
            val vectorDeContagio = Randomizador.getRandomVectorInfectado(vectores)
            if (vectorDeContagio!=null){
                contagiarVectores(vectores,vectorDeContagio, ubicacionId)
            }
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
            conexionesDAO.conectar(origen,destino,tipoCamino)
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

    private fun contagiarVectores(vectores :List<Vector>, vector: Vector, ubicacionId: Long){
        val cantidadDeUbicacionesDelasEspecies = vector.especiesPadecidas.map { especieDAO.cantidadDeUbicacionesDeLaEspecie(it.id!!) }.toMutableList()
        val eranPandemias = vector.especiesPadecidas.map { especieService.esPandemia(it.id!!) }.toMutableList()

        vectores.forEach {
                            it.serContagiado(vector)
                            vectorDAO.actualizar(it)
        }

        notificarEventos(vector.especiesPadecidas,cantidadDeUbicacionesDelasEspecies,eranPandemias,ubicacionId)

    }

    private fun notificarEventos(especiesPadecidas: List<Especie>,
                                 cantidadDeUbicacionesDelasEspecies: MutableList<Double>,
                                 eranPandemias : MutableList<Boolean>,
                                 ubicacionId: Long){
        especiesPadecidas.forEach {
            val cantUbicacionesDespuesDeContagiar = especieDAO.cantidadDeUbicacionesDeLaEspecie(it.id!!)
            val ubicacion = ubicacionDAO.recuperar(ubicacionId)

            if(cantidadDeUbicacionesDelasEspecies.removeFirst()>cantUbicacionesDespuesDeContagiar){
                alarmaEventos.notificar(it,ubicacion,TipoContagio.PrimerContagioEnUbicacion)
            }

            if(!eranPandemias.removeFirst() && especieService.esPandemia(it.id!!)){
                alarmaEventos.notificar(it,ubicacion,TipoContagio.Pandemia)
            }
        }
    }



}
