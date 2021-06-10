package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Ubicacion

class UbicacionDTO (val id:Long?,
                    val nombre : String,
                    ){


    companion object {
        fun desdeModelo(ubicacion: Ubicacion) =
                UbicacionDTO(ubicacion.id,
                    ubicacion.nombre)
    }

}


