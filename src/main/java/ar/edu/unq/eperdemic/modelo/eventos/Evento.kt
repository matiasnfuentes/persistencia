package ar.edu.unq.eperdemic.modelo.eventos

import com.fasterxml.jackson.annotation.JsonFormat
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


@BsonDiscriminator
abstract class Evento {

    @BsonProperty("id")
    var id: String? = null
    // format
    var momento: String = getDate()
    var mensaje : String = ""

    protected constructor()

    companion object {

        var ISO_8601_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'")

        fun getDate():String{
            return ISO_8601_FORMAT.format(Date())
        }

    }
}

