package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion

interface UbicacionService {

    fun mover(vectorId: Long, ubicacionid: Long)
    fun expandir(ubicacionId: Long)

    /* Operaciones CRUD*/
    fun recuperar(id:Long): Ubicacion
    fun crear(nombreUbicacion: String): Ubicacion
    fun recuperarTodos(): List<Ubicacion>
}