package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.*
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class FeedServiceTest : ServiceTest(){

    @Test
    fun se_notifica_cuando_se_crea_una_especie(){
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"Covid",argentina.id!!)
        sleep(1_000)
        val eventos = feedService.feedPatogeno(virus.id!!)

        Assert.assertTrue(eventos.any { if (it is MutacionE){ it.especie!!.id == covid.id!!} else false})
    }

    @Test
    fun se_notifica_cuando_una_especie_muta(){
        var fiebre = Mutacion("Fiebre",0, mutableListOf(),Atributo.LETALIDAD,100)
        fiebre =  mutacionService.crear(fiebre)
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)

        mutacionService.mutar(covid.id!!,fiebre.id!!)
        sleep(1_000)
        val eventos = feedService.feedPatogeno(virus.id!!)

        Assert.assertTrue(eventos.any { if (it is MutacionE){ it.especie!!.id == covid.id!!} else false})
        Assert.assertTrue(eventos.any { it.mensaje.equals("La enfermedad muto con Fiebre") })
    }

    @Test
    fun infecto_al_paciente_y_se_notifica_y_se_crea_un_evento_para_argentina_y_para_el_paciente(){
        var fiebre = Mutacion("Fiebre",0, mutableListOf(),Atributo.LETALIDAD,100)
        fiebre =  mutacionService.crear(fiebre)
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        vectorService.infectar(paciente.id!!,covid.id!!)

        mutacionService.mutar(covid.id!!,fiebre.id!!)
        sleep(1_000)
        val eventosDelPaciente = feedService.feedVector(paciente.id!!)
        val eventosDeArgentina = feedService.feedUbicacion(argentina.id!!)
        Assert.assertTrue(eventosDelPaciente.any { if (it is Contagio){ it.infectado!!.id == paciente.id!!} else false})
        Assert.assertTrue(eventosDeArgentina.any { if (it is Contagio){ it.infectado!!.id == paciente.id!! &&
                it.ubicacion!!.id==argentina.id} else false})
    }

    @Test
    fun el_paciente_infecta_a_cucaracha_al_mover_y_se_notifica_el_evento_de_contagio_para_ambos(){
        // Me aseguro de que la cucaracha se va a contagiar.
        mockkObject(Randomizador)
        every { Randomizador.getPorcentajeASuperar() } returns 0
        every { Randomizador.getPorcentajeDeContagio() } returns 100

        argentina = ubicacionService.crear("Argentina")
        jamaica = ubicacionService.crear("Jamaica")
        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Terrestre)

        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        cucaracha = vectorService.crear(TipoDeVector.Insecto,jamaica.id!!)

        vectorService.infectar(paciente.id!!,covid.id!!)
        ubicacionService.mover(paciente.id!!,jamaica.id!!)
        sleep(1_000)
        val eventosDelPaciente = feedService.feedVector(paciente.id!!)
        val eventosDeLaCucaracha = feedService.feedVector(cucaracha.id!!)
        Assert.assertTrue(eventosDelPaciente.any { if (it is Contagio && it.transmisor != null){ it.transmisor!!.id == paciente.id!!} else false})
        Assert.assertTrue(eventosDeLaCucaracha.any { if (it is Contagio && it.infectado != null){ it.infectado!!.id == cucaracha.id!!} else false})
    }

    @Test
    fun muevo_al_paciente_y_se_genera_un_evento_de_arribo(){
        argentina = ubicacionService.crear("Argentina")
        jamaica = ubicacionService.crear("Jamaica")
        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Terrestre)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)

        ubicacionService.mover(paciente.id!!,jamaica.id!!)
        sleep(1_000)
        val eventosDelPaciente = feedService.feedVector(paciente.id!!)
        val eventosDeJamaica = feedService.feedUbicacion(jamaica.id!!)
        Assert.assertTrue(eventosDelPaciente.any { if (it is Arribo){ it.vector!!.id == paciente.id!!} else false})
        Assert.assertTrue(eventosDeJamaica.any { if (it is Arribo){ it.ubicacion!!.id == jamaica.id!!} else false})
    }

    @Test
    fun se_crea_un_un_evento_de_primer_contagio_en_una_ubicacion_aunque_se_infecten_2_vectores(){

        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        cucaracha = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        vectorService.infectar(paciente.id!!,covid.id!!)
        vectorService.infectar(cucaracha.id!!,covid.id!!)
        sleep(1_000)
        val eventosDeArgentina =
            feedService.feedPatogeno(virus.id!!)
                .filter { if (it is Contagio){ it.subtipo== TipoContagio.PrimerContagioEnUbicacion} else false }

        Assert.assertTrue(eventosDeArgentina
            .any { if (it is Contagio){ it.subtipo== TipoContagio.PrimerContagioEnUbicacion && it.ubicacion!!.id == argentina.id!!} else false })
    }

    @Test
    fun se_crea_un_evento_de_pandemia_cuando_la_especie_infecta_en_2_de_3_locaciones(){

        argentina = ubicacionService.crear("Argentina")
        jamaica = ubicacionService.crear("Jamaica")
        chile = ubicacionService.crear("Chile")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        cucaracha = vectorService.crear(TipoDeVector.Insecto,jamaica.id!!)
        vectorService.infectar(paciente.id!!,covid.id!!)
        vectorService.infectar(cucaracha.id!!,covid.id!!)
        sleep(1_000)
        val eventosDeArgentina =
            feedService.feedPatogeno(virus.id!!)
                .filter { if (it is Contagio){ it.subtipo== TipoContagio.Pandemia} else false }

        // Queda registrado el evento en la última ubicación en la cual infectó
        Assert.assertTrue(eventosDeArgentina
            .any { if (it is Contagio){ it.subtipo== TipoContagio.Pandemia && it.ubicacion!!.id == jamaica.id!!} else false })
    }

    @Test
    fun borrar_firebase(){
        dataDAOS.forEach { it.clear() }
    }



}
