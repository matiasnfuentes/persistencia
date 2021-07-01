package ar.edu.unq.eperdemic.modelo.eventos

import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.time.LocalDateTime

@BsonDiscriminator
abstract class Evento {

    @BsonProperty("id")
    var id: String? = null
    var momento: LocalDateTime = LocalDateTime.now()
    var mensaje : String = ""

    protected constructor()
}

