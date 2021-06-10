package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorService {

    fun infectar(vectorId: Long, especieId: Long)
    fun enfermedades(vectorId: Long): List<Especie>

    /* Operaciones CRUD */
    fun crear(tipo: TipoDeVector, ubicacionId: Long): Vector
    fun recuperar(vectorId: Long): Vector
    fun recuperarTodos(): List<Vector>

}