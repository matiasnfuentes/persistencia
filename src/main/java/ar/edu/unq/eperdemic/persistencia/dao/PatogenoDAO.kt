package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoDAO {

    fun crear(patogeno: Patogeno): Patogeno
    fun actualizar(patogeno: Patogeno )
    fun recuperar(idDelPatogeno: Long?): Patogeno
    fun recuperarATodos() : List<Patogeno>
}