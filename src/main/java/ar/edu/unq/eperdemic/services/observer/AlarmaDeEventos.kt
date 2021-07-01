package ar.edu.unq.eperdemic.services.observer

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.eventos.TipoContagio

object AlarmaDeEventos{

    val observadores = mutableListOf<Observador>()

    fun agregar(observador: Observador) {
        observadores.add(observador)
    }

    fun eliminar(observador: Observador) {
        observadores.remove(observador)
    }

    fun eliminarTodos() {
        observadores.clear()
    }

    fun notificarObservadores(f: (Observador) -> Unit){
        observadores.forEach { f(it) }
    }

    fun notificar(enfermedad : Especie,vectorAContagiar : Vector,vectorDeContagio: Vector) {
        notificarObservadores { it.actualizar(enfermedad,vectorAContagiar,vectorDeContagio) }
    }

    fun notificar(enfermedad : Especie,ubicacion: Ubicacion, tipoContagio: TipoContagio) {
        notificarObservadores { it.actualizar(enfermedad, ubicacion,tipoContagio) }
    }

    fun notificar(especie: Especie,vectorContagiado: Vector){
        notificarObservadores { it.actualizar(especie,vectorContagiado) }
    }

    fun notificar(especie: Especie){
        notificarObservadores { it.actualizar(especie) }
    }

    fun notificar(especie: Especie, mutacion: Mutacion){
        notificarObservadores { it.actualizar(especie,mutacion) }
    }

    fun notificar(vector: Vector,ubicacion: Ubicacion){
        notificarObservadores{ it.actualizar(vector,ubicacion) }
    }

}