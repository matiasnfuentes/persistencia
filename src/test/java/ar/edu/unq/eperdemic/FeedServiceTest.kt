package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Atributo
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.eventos.MutacionE
import org.junit.Assert
import org.junit.jupiter.api.Test

class FeedServiceTest : ServiceTest(){

    @Test
    fun se_notifica_cuando_se_crea_una_especie(){
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)

        val eventos = feedService.feedPatogeno(virus.id!!)
        Assert.assertTrue(true)
        Assert.assertTrue(eventos.any { (it as MutacionE).especie!!.id == covid.id!! })
    }

    @Test
    fun se_notifica_cuando_una_especie_muta(){
        var fiebre = Mutacion("Fiebre",0, mutableListOf(),Atributo.LETALIDAD,100)
        fiebre =  mutacionService.crear(fiebre)
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)

        mutacionService.mutar(covid.id!!,fiebre.id!!)

        val eventos = feedService.feedPatogeno(virus.id!!)

        Assert.assertTrue(true)
        Assert.assertTrue(eventos.any { (it as MutacionE).especie!!.id == covid.id!! })
        Assert.assertTrue(eventos.any { it.mensaje.equals("La enfermedad muto con Fiebre") })
    }

    @Test
    fun infecto_al_paciente_y_se_notifica(){
        var fiebre = Mutacion("Fiebre",0, mutableListOf(),Atributo.LETALIDAD,100)
        fiebre =  mutacionService.crear(fiebre)
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        vectorService.infectar(paciente.id!!,covid.id!!)

        mutacionService.mutar(covid.id!!,fiebre.id!!)

        val eventos = feedService.feedVector(paciente.id!!)
        Assert.assertTrue(eventos.any { it.mensaje.equals("El vector ${paciente.id} fue infectado con ${covid.nombre}") })

    }
}