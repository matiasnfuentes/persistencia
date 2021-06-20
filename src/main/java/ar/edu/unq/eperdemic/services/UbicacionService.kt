package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.TipoDeCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion

interface UbicacionService {

    fun mover(vectorId: Long, ubicacionid: Long)
    fun expandir(ubicacionId: Long)
    fun conectar(ubicacion1:Long, ubicacion2:Long, tipoCamino:TipoDeCamino)
    fun conectados(ubicacionId:Long): List<Ubicacion>
    fun capacidadDeExpansion(vectorId: Long, movimientos:Int): Int

    /* Operaciones CRUD*/
    fun recuperar(id:Long): Ubicacion
    fun crear(nombreUbicacion: String): Ubicacion
    fun recuperarTodos(): List<Ubicacion>
}