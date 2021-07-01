package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.eventos.Evento

interface FeedService {

    fun feedPatogeno(patogenoID: Long) : List<Evento>
    fun feedVector(vectorId: Long) : List<Evento>
    fun feedUbicacion(ubicacionId: Long) : List<Evento>

}