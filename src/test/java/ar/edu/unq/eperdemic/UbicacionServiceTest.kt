package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import io.mockk.*
import org.junit.Assert
import org.junit.jupiter.api.Test


class UbicacionServiceTest : ServiceTest(HibernateDataDAO()) {

    @Test
    fun persisto_una_ubicacion_y_puedo_recuperarla() {
        //Exercise
        val ubicacion = ubicacionService.crear("Argentina")
        val ubicacionRecuperada = ubicacionService.recuperar(ubicacion.id!!)

        // Verify
        Assert.assertEquals(ubicacion.id!!,ubicacionRecuperada.id!!)
    }

    @Test
    fun persisto_dos_ubicaciones_y_puedo_recuperarlas() {
        // Set up
        ubicacionService.crear("Argentina")
        ubicacionService.crear("USA")

        //Exercise
        val ubicacionesRecuperadas = ubicacionService.recuperarTodos()

        // Verify
        Assert.assertEquals(2, ubicacionesRecuperadas.size)
        Assert.assertTrue(ubicacionesRecuperadas.any {it.nombre == "Argentina"} )
        Assert.assertTrue(ubicacionesRecuperadas.any {it.nombre == "USA"} )
    }

    @Test
    fun no_persisto_ninguna_ubicacion_y_recupero_una_lista_vacia() {
        // Excercise
        val ubicacionesRecuperadas = ubicacionService.recuperarTodos()

        // Verify
        Assert.assertTrue(ubicacionesRecuperadas.isEmpty())
    }

    fun mockearRandomizador(){
        // Me aseguro que siempre que se intente contagiar a un vector,
        // el mismo se contagie
        mockkObject(Randomizador)
        every { Randomizador.getPorcentajeASuperar() } returns 0
        every { Randomizador.getPorcentajeDeContagio() } returns 100
    }

    fun inicializarDatosParaMover(){
        argentina = ubicacionService.crear("argentina")
        otraUbicacion = ubicacionService.crear("ningun lugar")
        pez = vectorService.crear(TipoDeVector.Animal,otraUbicacion.id!!)
        mosquito = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        flu = patogenoService.agregarEspecie(virus.id!!,"flu",argentina.id!!)
    }

    @Test
    fun muevo_un_vector_sano_y_los_vectores_de_la_ubicacion_no_se_contagian(){
        //Set up
        mockearRandomizador()
        inicializarDatosParaMover()

        // Excersice
        ubicacionService.mover(pez.id!!,argentina.id!!)

        // Verify
        Assert.assertTrue(pez.especiesPadecidas.isEmpty())
        Assert.assertTrue(mosquito.especiesPadecidas.isEmpty())
        Assert.assertTrue(paciente.especiesPadecidas.isEmpty())
    }

    @Test
    fun muevo_un_vector_enfermo_y_los_vectores_de_la_ubicacion_se_contagian(){
        //Set up
        mockearRandomizador()
        inicializarDatosParaMover()
        vectorService.infectar(pez.id!!,covid.id!!)

        // Excersice
        ubicacionService.mover(pez.id!!,argentina.id!!)

        // Verify
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente= vectorService.recuperar(paciente.id!!)
        covid = especieService.recuperar(covid.id!!)

        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertEquals(3,covid.cantidadDeInfectados)
    }

    @Test
    fun muevo_un_vector_con_dos_enfermedaes_y_los_vectores_de_la_ubicacion_se_contagian(){
        // Set up
        mockearRandomizador()
        inicializarDatosParaMover()
        vectorService.infectar(pez.id!!,covid.id!!)
        vectorService.infectar(pez.id!!,flu.id!!)

        // Excersice
        ubicacionService.mover(pez.id!!,argentina.id!!)

        // Verify
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente = vectorService.recuperar(paciente.id!!)
        covid = especieService.recuperar(covid.id!!)
        flu = especieService.recuperar(flu.id!!)
        Assert.assertEquals(3,flu.cantidadDeInfectados)
        Assert.assertEquals(3,covid.cantidadDeInfectados)
        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "covid"})
        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "flu"})
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "flu" })
    }

    @Test
    fun muevo_un_vector_enfermo_y_los_vectores_de_la_ubicacion_no_contagian(){
        // Set Up
        // Me aseguro que siempre que se intente contagiar a un vector,
        // el mismo no se contagie, porque el porcentaje a superar es alto
        // y el porcentaje de exito bajo.
        mockkObject(Randomizador)
        every { Randomizador.getPorcentajeASuperar() } returns 100
        every { Randomizador.getPorcentajeDeContagio() } returns 0
        inicializarDatosParaMover()
        vectorService.infectar(pez.id!!,covid.id!!)

        // Excersice
        ubicacionService.mover(pez.id!!,argentina.id!!)

        // Verify
        covid = especieService.recuperar(covid.id!!)
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente = vectorService.recuperar(paciente.id!!)
        Assert.assertFalse(mosquito.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertFalse(paciente.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertEquals(1,covid.cantidadDeInfectados)
    }

    fun inicializardatosParaExpandir(){
        inicializarDatosParaMover()
        pezArgentino = vectorService.crear(TipoDeVector.Animal,argentina.id!!)
        mockearRandomizador()
    }

    @Test
    fun expando_en_una_ubicacion_con_vectores_sanos_y_no_pasa_nada(){
        // Set Up

        inicializardatosParaExpandir()

        // Excersice
        ubicacionService.expandir(argentina.id!!)

        // Verify
        pezArgentino = vectorService.recuperar(pezArgentino.id!!)
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente = vectorService.recuperar(paciente.id!!)

        Assert.assertTrue(pezArgentino.especiesPadecidas.isEmpty())
        Assert.assertTrue(mosquito.especiesPadecidas.isEmpty())
        Assert.assertTrue(paciente.especiesPadecidas.isEmpty())
    }

    @Test
    fun expando_en_una_ubicacion_con_un_vector_enfermo_se_elige_y_los_otros_se_contagian(){
        // Set Up
        inicializardatosParaExpandir()
        vectorService.infectar(pezArgentino.id!!,covid.id!!)

        // Excersice
        ubicacionService.expandir(argentina.id!!)

        // Verify
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente = vectorService.recuperar(paciente.id!!)

        // Los demas vectores se contagian
        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "covid" })
    }

}