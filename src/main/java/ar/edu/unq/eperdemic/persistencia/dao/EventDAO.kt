package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.eventos.Evento

interface EventDAO {
    fun feedPatogeno(patogeno: Patogeno) : List<Evento>
    fun feedUbicacion(ubicacion: Ubicacion) : List<Evento>
    fun feedVector(vector: Vector) : List<Evento>
}