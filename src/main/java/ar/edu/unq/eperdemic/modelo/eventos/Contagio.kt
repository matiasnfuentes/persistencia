package ar.edu.unq.eperdemic.modelo.eventos

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import org.bson.codecs.pojo.annotations.BsonDiscriminator

@BsonDiscriminator("contagio")
class Contagio:Evento {

    var transmisor: Vector? = null
    var infectado: Vector? = null
    var especie : Especie? = null
    var ubicacion: Ubicacion? = null
    var subtipo: TipoContagio? = null

    protected constructor()

    // Contagio de vector a vector
    constructor(transmisor: Vector,
                infectado : Vector,
                especie : Especie,
                ubicacion: Ubicacion,
                subtipo: TipoContagio,
                mensaje : String){
        this.transmisor = transmisor
        this.infectado = infectado
        this.especie = especie
        this.ubicacion = ubicacion
        this.subtipo = subtipo
        this.mensaje = mensaje
    }

    //Primer contagio o pandemia
    constructor(especie : Especie,
                infectado: Vector,
                ubicacion: Ubicacion,
                subtipo: TipoContagio,
                mensaje : String){
        this.infectado = infectado
        this.especie = especie
        this.ubicacion = ubicacion
        this.subtipo = subtipo
        this.mensaje = mensaje
    }

    //Primer contagio o pandemia
    constructor(especie : Especie,
                ubicacion: Ubicacion,
                subtipo: TipoContagio,
                mensaje : String){
        this.especie = especie
        this.ubicacion = ubicacion
        this.subtipo = subtipo
        this.mensaje = mensaje
    }


}

enum class TipoContagio {
    Pandemia,
    PrimerContagioEnUbicaion,
    Contagio
}