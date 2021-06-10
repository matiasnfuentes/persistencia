package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieService {

    fun cantidadDeInfectados (especieId: Long) : Int
    fun recuperar(id: Long): Especie
    fun recuperarTodos(): List<Especie>

    fun esPandemia (especieId: Long) : Boolean
}