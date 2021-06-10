package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector

class VectorDTO(var id:Long?, var tipo: TipoDeVector, var ubicacionId: Long){

    companion object {
        fun desdeModelo(vector: Vector) =
            VectorDTO(vector.id, vector.tipo, vector.ubicacion.id!!)
    }
}