package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.TipoDeCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface ConexionesDAO {

    fun crearUbicacion(ubicacion : Ubicacion)
    fun recuperarUbicacion(hibernateID : Long) : Ubicacion
    fun conectar(origen: Ubicacion, destino: Ubicacion, tipoCamino: TipoDeCamino)
    fun conectados(ubicacionId:Long): List<Ubicacion>
    fun rutaAUbicacion(vector: Vector, destino:Ubicacion): List<Long>
    fun capacidadDeExpansion(vector: Vector, movimientos: Int): Int
}