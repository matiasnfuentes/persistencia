package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exceptions.AtributoFueraDeRangoNumericoException
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ModeloTest {

    val ubicacion = mockk<Ubicacion>(relaxed = true)
    val persona = Vector(TipoDeVector.Persona, ubicacion)
    val perro = Vector(TipoDeVector.Animal, ubicacion)
    val cucaracha = Vector(TipoDeVector.Insecto, ubicacion)

    @Test
    fun no_puedo_crear_un_patogeno_cuyos_atributos_de_contagio_superen_100(){
        assertThrows<AtributoFueraDeRangoNumericoException> {
            Patogeno("patogeno mal hecho",101,99,99,99,99)
        }
    }

    @Test
    fun una_persona_puede_contagiarse_de_cualquier_vector(){
        Assert.assertTrue(TipoDeVector.Persona.condicionDeContagio(persona))
        Assert.assertTrue(TipoDeVector.Persona.condicionDeContagio(perro))
        Assert.assertTrue(TipoDeVector.Persona.condicionDeContagio(cucaracha))
    }

    @Test
    fun un_insecto_no_puede_contagiarse_de_otro_insecto(){
        Assert.assertTrue(TipoDeVector.Insecto.condicionDeContagio(persona))
        Assert.assertTrue(TipoDeVector.Insecto.condicionDeContagio(perro))
        Assert.assertFalse(TipoDeVector.Insecto.condicionDeContagio(cucaracha))
    }

    @Test
    fun un_animal_solo_puede_contagiarse_de_un_insecto(){
        Assert.assertFalse(TipoDeVector.Animal.condicionDeContagio(persona))
        Assert.assertFalse(TipoDeVector.Animal.condicionDeContagio(perro))
        Assert.assertTrue(TipoDeVector.Animal.condicionDeContagio(cucaracha))
    }

}
