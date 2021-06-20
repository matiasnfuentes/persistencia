package ar.edu.unq.eperdemic.modelo

import java.io.Serializable
import javax.persistence.*

@Entity
class Ubicacion(var nombre: String):Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}

enum class TipoDeCamino{
    Aereo,
    Maritimo,
    Terrestre }
