package ar.edu.unq.eperdemic.modelo
import java.io.Serializable
import javax.persistence.*

@Entity
class Vector(
    @Enumerated(value = EnumType.STRING)
    var tipo: TipoDeVector,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
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
}

enum class TipoDeVector {
    Persona {
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
    },
    Insecto {
        override fun condicionDeContagio(vector: Vector): Boolean {
            return vector.tipo != Insecto
        }

        override fun factorDeContagio(especie: Especie): Int {
            return especie.contagioInsectos()
        }
    },
    Animal {
        override fun condicionDeContagio(vector: Vector): Boolean {
            return vector.tipo == Insecto
        }

        override fun factorDeContagio(especie: Especie): Int {
            return especie.contagioAnimal()
        }

    };

    fun serContagiado(vectorAContagiar: Vector, vector: Vector) {
        if (condicionDeContagio(vector)) {
            vector.especiesPadecidas
                  .filter { !vectorAContagiar.estaContagiadoCon(it.id!!) }
                  .forEach { efectuarContagio(it, vectorAContagiar) }
            }
        }

    fun efectuarContagio(enfermedad: Especie, vectorAContagiar: Vector) {
        val porcentajeDeContagioExitoso = Randomizador.getPorcentajeDeContagio() + factorDeContagio(enfermedad)
        val porcentajeASuperar = Randomizador.getPorcentajeASuperar()
        if (porcentajeDeContagioExitoso >= porcentajeASuperar) {
            vectorAContagiar.infectar(enfermedad)
        }
    }

    open fun intentarAumentarADN(especie: Especie) {}
    abstract fun condicionDeContagio(vector: Vector): Boolean
    abstract fun factorDeContagio(especie: Especie): Int

}