package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion

interface UbicacionDAO {

    fun recuperar(id:Long?): Ubicacion
    fun crear(nombreUbicacion: String): Ubicacion
    fun recuperarATodos(): List<Ubicacion>

    /**
     * @return retorna la cantidad de ubicaciones
     * totales registradas en el DAO.
     */
    fun cantidadDeUbicaciones():Double
}
