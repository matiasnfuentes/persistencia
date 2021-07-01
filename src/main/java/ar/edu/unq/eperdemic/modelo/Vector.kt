package ar.edu.unq.eperdemic.modelo
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos
import java.io.Serializable
import javax.persistence.*

@Entity
class Vector(
    @Enumerated(value = EnumType.STRING)
    var tipo: TipoDeVector,
    @ManyToOne(fetch = FetchType.EAGER)
    var ubicacion: Ubicacion): Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var especiesPadecidas: MutableList<Especie> = mutableListOf()

    fun infectar(especie: Especie) {
        especiesPadecidas.add(especie)
        especie.aumentarInfectados()
        tipo.intentarAumentarADN(especie)
    }

    fun estaContagiadoCon(especieId: Long): Boolean{
        return especiesPadecidas.any { it.id == especieId }
    }

    fun serContagiado(vectorDeContagio: Vector) {
        tipo.serContagiado(this, vectorDeContagio)
    }

    fun puedeContagiar(): Boolean {
        return especiesPadecidas.isNotEmpty()
    }

    fun cambiarUbicacion(ubicacion: Ubicacion) {
        this.ubicacion = ubicacion
    }

    fun caminosPermitidos(): List<TipoDeCamino> {
        return tipo.caminosPermitidos()
    }

}

enum class TipoDeVector {

    Persona {
        private val caminosPermitidos = listOf(TipoDeCamino.Maritimo,TipoDeCamino.Terrestre)

        override fun condicionDeContagio(vector: Vector): Boolean {
            return true
        }

        override fun factorDeContagio(especie: Especie): Int {
            return especie.contagioHumanos()
        }

        override fun intentarAumentarADN(especie: Especie){
            especie.personasInfectadas++
            if((especie.personasInfectadas % 5) == 0){
                especie.adnDisponible++
            }
        }

        override fun caminosPermitidos(): List<TipoDeCamino>{
            return  caminosPermitidos
        }

    },
    Insecto {
        private val caminosPermitidos = listOf(TipoDeCamino.Aereo,TipoDeCamino.Terrestre)

        override fun condicionDeContagio(vector: Vector): Boolean {
            return vector.tipo != Insecto
        }

        override fun factorDeContagio(especie: Especie): Int {
            return especie.contagioInsectos()
        }

        override fun caminosPermitidos(): List<TipoDeCamino>{
            return caminosPermitidos
        }

    },

    Animal {
        private val caminosPermitidos = listOf(TipoDeCamino.Aereo,TipoDeCamino.Maritimo,TipoDeCamino.Terrestre)

        override fun condicionDeContagio(vector: Vector): Boolean {
            return vector.tipo == Insecto
        }

        override fun factorDeContagio(especie: Especie): Int {
            return especie.contagioAnimal()
        }

        override fun caminosPermitidos(): List<TipoDeCamino>{
            return caminosPermitidos
        }

    };

    @Transient
    val alarma = AlarmaDeEventos

    fun serContagiado(vectorAContagiar: Vector, vector: Vector) {
        if (condicionDeContagio(vector)) {
            vector.especiesPadecidas
                  .filter { !vectorAContagiar.estaContagiadoCon(it.id!!) }
                  .forEach { efectuarContagio(it, vectorAContagiar,vector) }
            }
        }

    fun efectuarContagio(enfermedad: Especie, vectorAContagiar: Vector, vectorDeContagio: Vector) {
        val porcentajeDeContagioExitoso = Randomizador.getPorcentajeDeContagio() + factorDeContagio(enfermedad)
        val porcentajeASuperar = Randomizador.getPorcentajeASuperar()
        if (porcentajeDeContagioExitoso >= porcentajeASuperar) {
            vectorAContagiar.infectar(enfermedad)
            alarma.notificar(enfermedad,vectorAContagiar,vectorDeContagio)
        }
    }

    open fun intentarAumentarADN(especie: Especie) {}
    abstract fun condicionDeContagio(vector: Vector): Boolean
    abstract fun factorDeContagio(especie: Especie): Int
    abstract fun caminosPermitidos(): List<TipoDeCamino>
}