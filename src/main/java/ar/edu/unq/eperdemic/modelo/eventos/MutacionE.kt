package ar.edu.unq.eperdemic.modelo.eventos

import ar.edu.unq.eperdemic.modelo.Especie
import org.bson.codecs.pojo.annotations.BsonDiscriminator


@BsonDiscriminator("mutacion")
class MutacionE:Evento {

    var especie: Especie? = null
    var subtipo: TipoMutacion? = null

    protected constructor()

    constructor(especie: Especie,
                subtipo: TipoMutacion,
                mensaje : String){
        this.especie = especie
        this.subtipo = subtipo
        this.mensaje = mensaje

    }
}

enum class TipoMutacion{
    Mutacion,
    CreacionEspecie
}

