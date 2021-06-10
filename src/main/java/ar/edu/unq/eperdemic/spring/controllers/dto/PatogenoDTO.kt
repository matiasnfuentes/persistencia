package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO (val id:Long?, val tipo : String, val cantidadDeEspecies:Int, val factorContagioHumano: Int,
                   val factorContagioAnimal: Int,
                   val factorContagioInsecto: Int,
                   val defensa: Int,
                   val letalidad: Int){

    companion object {
        fun desdeModelo(patogeno: Patogeno): PatogenoDTO {
            return PatogenoDTO(
                patogeno.id,
                patogeno.tipo,
                patogeno.cantidadDeEspecies,
                patogeno.contagioPersonas,
                patogeno.contagioAnimales,
                patogeno.contagioInsectos,
                patogeno.defensa,
                patogeno.letalidad)
        }
    }

    fun aModelo(): Patogeno {
        val pat = Patogeno(tipo,factorContagioAnimal,factorContagioHumano,factorContagioInsecto,defensa,letalidad)
        pat.cantidadDeEspecies = cantidadDeEspecies
        return pat
    }
}

