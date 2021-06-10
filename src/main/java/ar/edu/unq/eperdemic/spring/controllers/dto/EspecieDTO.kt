package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Ubicacion

class EspecieDTO (val id: Long?,
                  val nombre : String,
                  val patogeno: PatogenoDTO,
                  val paisDeOrigen: UbicacionDTO,
                  val adn:Int,
                  val mutaciones: List<Mutacion> ){



    companion object {
        fun desdeModelo(especie:Especie): EspecieDTO {
            return EspecieDTO(
                especie.id,
                especie.nombre,
                PatogenoDTO.desdeModelo(especie.patogeno),
                UbicacionDTO.desdeModelo(especie.paisDeOrigen),
                especie.adnDisponible,
                especie.mutaciones)
        }
    }

    fun aModelo(): Especie {
        val especie = Especie(patogeno.aModelo(),nombre, Ubicacion(paisDeOrigen.nombre))
        especie.adnDisponible = adn
        especie.mutaciones.addAll(mutaciones)
        return especie
    }



}


