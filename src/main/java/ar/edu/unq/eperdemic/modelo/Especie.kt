package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exceptions.NoCumpleCondicionesDeMutacionException
import java.io.Serializable
import javax.persistence.*

@Entity
class Especie(@ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
              var patogeno: Patogeno,
              var nombre: String,
              @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
              var paisDeOrigen: Ubicacion) : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val mutaciones = mutableListOf<Mutacion>()
    var adnDisponible = 0
    var cantidadDeInfectados = 0
    var personasInfectadas = 0

    fun aumentarInfectados(){
        cantidadDeInfectados++
    }

    fun contagioAnimal() : Int{
        return patogeno.contagioAnimales + atributoMutaciones(Atributo.FACTOR_ANIMAL)
    }

    fun contagioHumanos(): Int{
        return patogeno.contagioPersonas + atributoMutaciones(Atributo.FACTOR_HUMANO)
    }

    fun contagioInsectos(): Int{
        return patogeno.contagioInsectos + atributoMutaciones(Atributo.FACTOR_INSECTO)
    }

    fun letalidad(): Int{
        return patogeno.letalidad + atributoMutaciones(Atributo.LETALIDAD)
    }

    fun defensa(): Int{
        return patogeno.defensa + atributoMutaciones(Atributo.DEFENSA)
    }

    private fun atributoMutaciones(atributo: Atributo):Int{
        return mutaciones.filter { it.atributo==atributo }.sumBy { it.valor }
    }

    /**
     * Retorna true si cumple con las condiciones para mutar:
     * 1 - Cumple con todos los pre-requisitos para la mutación.
     * 2 - Tiene el ADN suficiente para mutar.
     * 3 - No mutó anteriormente con la mutación dada.
     * @param[mutacion] Una mutación con la que se busca mutar a la especie
     * @return retorna un Boolean indicando si puede mutar o no.
     * totales registradas en el DAO.
     */

    private fun puedoMutar(mutacion: Mutacion): Boolean {
        return mutaciones.map{ it.id }.containsAll(mutacion.requerimientos.map{it.id}) &&
               adnDisponible >= mutacion.adnRequerido &&
               !mutaciones.any { it.id == mutacion.id }
    }

    /**
     * Intenta mutar a la especie con la mutación pasada como parametro
     * y de ser posible resta el ADN necesario para hacerlo
     * @param[mutacion] Una mutación con la que se busca mutar a la especie
     * @return retorna un Boolean indicando si puede mutar o no.
     * totales registradas en el DAO.
     * @throws NoCumpleCondicionesDeMutacionException Arroja una excepción en el caso
     * de que la especie no cumpla con las condiciones para mutar
     */

    fun mutar(mutacion: Mutacion){
        if (puedoMutar(mutacion)){
            adnDisponible-= mutacion.adnRequerido
            mutaciones.add(mutacion)
        }
        else{
            throw NoCumpleCondicionesDeMutacionException("La especie no puede mutar porque " +
                    "no cumple con las condiciones para hacerlo.")
        }
    }


}