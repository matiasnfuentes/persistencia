package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieDAO {

    fun crear(especie: Especie): Especie
    fun recuperar(id: Long?): Especie
    fun recuperarATodos() : List<Especie>
    fun actualizar(item: Especie)



    fun especiesDelPatogeno(patogenoID: Long): List<Especie>

    /**
     * Dada una especie particular, cuenta la cantidad de ubicaciones donde está
     * presente esa especie, es decir, la cantidad de ubicaciones donde hay
     * vectores infectados con dicha especie
     * @param[especieId] es el id de la especie
     * @return un double que indica en que cantidad de ubicaciones donde se encuentra la especie.
     */

    fun cantidadDeUbicacionesDeLaEspecie(especieId: Long): Double
    fun especieLider():Especie
    fun lideres():List<Especie>
    fun especieMasInfecciosa(ubicacionID: Long): Especie
}