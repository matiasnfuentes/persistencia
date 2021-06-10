package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion

interface MutacionService {
    fun mutar (especieId: Long, mutacionId: Long): Especie
    /* Operaciones CRUD */
    fun crear(mutacion: Mutacion): Mutacion
    fun recuperar(mutacionId: Long): Mutacion
    fun recuperarTodos(): List<Mutacion>
}