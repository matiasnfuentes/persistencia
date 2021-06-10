package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorDAO {

    fun crear(vector: Vector): Vector
    fun recuperar(id: Long?): Vector
    fun recuperarATodos(): List<Vector>
    fun actualizar(item: Vector)
    fun vectoresEnUbicacion(ubicacionId: Long): Int
    fun vectoresInfectadosEnUbicacion(ubicacionId: Long): Int

}
