package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Atributo
import ar.edu.unq.eperdemic.modelo.Mutacion

class MutacionDTO (val id: Long?,
                   val nombre : String,
                   val adnRequeridos: Int,
                   val atributo: Atributo,
                   val cantidad: Int ){


    companion object {
        fun desdeModelo(mutacion:Mutacion): MutacionDTO {
            return MutacionDTO(mutacion.id!!,mutacion.nombre,mutacion.adnRequerido,mutacion.atributo,mutacion.valor)
        }
    }

    fun aModelo():Mutacion {
        return Mutacion(nombre,adnRequeridos, mutableListOf(),atributo,cantidad)
    }

}


