package ar.edu.unq.eperdemic.services.observer

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.eventos.TipoContagio
import ar.edu.unq.eperdemic.modelo.eventos.TipoMutacion

interface Observador {

    fun actualizar(enfermedad : Especie, vectorAContagiar : Vector, vectorDeContagio: Vector)
    fun actualizar(enfermedad : Especie, ubicacion: Ubicacion, tipoContagio: TipoContagio)
    fun actualizar(especie: Especie,vectorContagiado: Vector)


    fun actualizar(especie: Especie)
    fun actualizar(especie: Especie, mutacion: Mutacion)

    fun actualizar(vector : Vector,ubicacion : Ubicacion)
}