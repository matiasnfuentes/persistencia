package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exceptions.AtributoFueraDeRangoNumericoException
import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno (@Column(unique = true) var tipo: String ,
                               val contagioAnimales : Int,
                               val contagioPersonas : Int,
                               val contagioInsectos : Int,
                               val defensa : Int,
                               val letalidad:Int): Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    var cantidadDeEspecies: Int = 0

    init {
        val lista =
           listOf(contagioAnimales,contagioPersonas,contagioInsectos,defensa,letalidad)
        if(lista.any{(it<0) or (it > 100)}) {
            throw AtributoFueraDeRangoNumericoException("No se puede instanciar el patogeno con los parámetros dados.")
        }
    }

    override fun toString(): String {
        return tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: Ubicacion) : Especie {
        cantidadDeEspecies++
        return Especie(this, nombreEspecie, paisDeOrigen)
    }

}