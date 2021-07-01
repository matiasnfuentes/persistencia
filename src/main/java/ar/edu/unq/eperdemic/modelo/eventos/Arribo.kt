package ar.edu.unq.eperdemic.modelo.eventos

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import org.bson.codecs.pojo.annotations.BsonDiscriminator

@BsonDiscriminator("arribo")
class Arribo:Evento {

    var vector: Vector? = null
    var ubicacion: Ubicacion? = null

    protected constructor()

    constructor(vector: Vector, ubicacion: Ubicacion, mensaje: String){
        this.vector = vector
        this.ubicacion = ubicacion
        this.mensaje = mensaje
    }

}