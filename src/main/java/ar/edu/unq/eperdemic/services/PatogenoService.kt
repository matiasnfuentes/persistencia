package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoService {

    fun agregarEspecie(id: Long, nombre: String, ubicacionId : Long) : Especie
    fun especiesDePatogeno(patogenoId: Long) : List<Especie>

    fun crear(patogeno: Patogeno): Patogeno
    fun recuperar(id: Long): Patogeno
    fun recuperarTodos(): List<Patogeno>
}