package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Randomizador
import ar.edu.unq.eperdemic.modelo.TipoDeCamino
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.exceptions.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class UbicacionServiceTest : ServiceTest() {

    @Test
    fun persisto_una_ubicacion_y_puedo_recuperarla() {
        //Exercise
        val ubicacion = ubicacionService.crear("Argentina")
        val ubicacionRecuperadaHibernate = ubicacionService.recuperar(ubicacion.id!!)
        val ubicacionRecuperadaNeo4J = runTrx{ conexionesDAO.recuperarUbicacion(ubicacion.id!!)}

        // Verify
        Assert.assertEquals(ubicacion.id!!,ubicacionRecuperadaHibernate.id!!)
        // Verifico que la ubicacion se persista tambien en Neo4j
        Assert.assertEquals(ubicacion.id!!,ubicacionRecuperadaNeo4J.id!!)
    }

    @Test
    fun persisto_dos_ubicaciones_y_puedo_recuperarlas() {
        // Set up
        val arg = ubicacionService.crear("Argentina")
        val eeuu = ubicacionService.crear("USA")

        //Exercise
        val ubicacionesRecuperadas = ubicacionService.recuperarTodos()

        // Verify
        Assert.assertEquals(2, ubicacionesRecuperadas.size)
        Assert.assertTrue(ubicacionesRecuperadas.any {it.nombre == "Argentina"} )
        Assert.assertTrue(ubicacionesRecuperadas.any {it.nombre == "USA"} )
        // Verifico que las ubicaciones se persistan tambien en Neo4j
        Assert.assertEquals(arg.id , conexionesDAO.recuperarUbicacion(arg.id!!).id)
        Assert.assertEquals(eeuu.id , conexionesDAO.recuperarUbicacion(eeuu.id!!).id)
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
        inicializarUbicaciones()
        pez = vectorService.crear(TipoDeVector.Animal,otraUbicacion.id!!)
        mosquito = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        hormiga = vectorService.crear(TipoDeVector.Insecto,jamaica.id!!)
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        flu = patogenoService.agregarEspecie(virus.id!!,"flu",argentina.id!!)

        ubicacionService.conectar(otraUbicacion.id!!,jamaica.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(jamaica.id!!,argentina.id!!,TipoDeCamino.Terrestre)
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
        // También infecta a la hormiga que está en una ubicacion de "paso"
        //Set up
        mockearRandomizador()
        inicializarDatosParaMover()
        vectorService.infectar(pez.id!!,covid.id!!)

        // Excersice
        ubicacionService.mover(pez.id!!,argentina.id!!)

        // Verify
        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente= vectorService.recuperar(paciente.id!!)
        hormiga = vectorService.recuperar(hormiga.id!!)
        covid = especieService.recuperar(covid.id!!)

        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(hormiga.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertEquals(4,covid.cantidadDeInfectados)
    }

    @Test
    fun muevo_un_vector_con_dos_enfermedades_y_los_vectores_de_la_ubicacion_se_contagian(){
        // También infecta a la hormiga que está en una ubicacion de "paso"
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
        hormiga = vectorService.recuperar(hormiga.id!!)
        covid = especieService.recuperar(covid.id!!)
        flu = especieService.recuperar(flu.id!!)
        Assert.assertEquals(4,flu.cantidadDeInfectados)
        Assert.assertEquals(4,covid.cantidadDeInfectados)
        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "covid"})
        Assert.assertTrue(mosquito.especiesPadecidas.any { it.nombre == "flu"})
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "covid" })
        Assert.assertTrue(paciente.especiesPadecidas.any { it.nombre == "flu" })
        Assert.assertTrue(hormiga.especiesPadecidas.any { it.nombre == "covid"})
        Assert.assertTrue(hormiga.especiesPadecidas.any { it.nombre == "flu"})
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

    // Caminos terrestres : Que pueden ser atravesados por todos los tipos de vectores
    // Caminos marítimos: Que pueden ser atravesados por humanos y animales
    // Caminos aéreos: Que solo pueden ser atravesados por insectos y animales.

    @Test
    fun intento_mover_a_un_insecto_por_un_camino_incorrecto_y_se_lanza_una_excepcion(){
        inicializarUbicaciones()
        mosquito = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Maritimo)
        assertThrows<UbicacionNoAlcanzable> { ubicacionService.mover(mosquito.id!!,jamaica.id!!) }
    }

    @Test
    fun intento_mover_a_un_humano_por_un_camino_incorrecto_y_se_lanza_una_excepcion(){
        inicializarUbicaciones()
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Aereo)
        assertThrows<UbicacionNoAlcanzable> { ubicacionService.mover(paciente.id!!,jamaica.id!!) }
    }

    @Test
    fun intento_mover_a_un_vector_a_una_ubicacion_donde_no_hay_camino_y_se_lanza_una_excepcion(){
        inicializarUbicaciones()
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        assertThrows<UbicacionNoAlcanzable> { ubicacionService.mover(paciente.id!!,jamaica.id!!) }
    }

    @Test
    fun intento_mover_vectores_por_caminos_correctos_y_lo_logro(){
        inicializarUbicaciones()
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        mosquito = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        pez = vectorService.crear(TipoDeVector.Animal,argentina.id!!)

        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,venezuela.id!!,TipoDeCamino.Maritimo)

        ubicacionService.mover(mosquito.id!!,jamaica.id!!)
        ubicacionService.mover(paciente.id!!,brasil.id!!)
        ubicacionService.mover(pez.id!!,venezuela.id!!)

        mosquito = vectorService.recuperar(mosquito.id!!)
        paciente = vectorService.recuperar(paciente.id!!)
        pez = vectorService.recuperar(pez.id!!)

        Assert.assertTrue(mosquito.ubicacion.id == jamaica.id!!)
        Assert.assertTrue(paciente.ubicacion.id == brasil.id!!)
        Assert.assertTrue(pez.ubicacion.id == venezuela.id!!)
    }

    @Test
    fun intento_mover_a_un_humano_y_no_lo_logro_porque_toma_el_camino_mas_corto_y_no_lo_puede_transitar(){
        inicializarUbicaciones()
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        ubicacionService.conectar(argentina.id!!,jamaica.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(argentina.id!!,venezuela.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(venezuela.id!!,jamaica.id!!,TipoDeCamino.Terrestre)

        assertThrows<UbicacionNoAlcanzable> { ubicacionService.mover(paciente.id!!,jamaica.id!!) }
    }

    @Test
    fun intento_mover_a_un_humano_con_dos_caminos_uno_transitable_y_otro_no_y_lo_logro(){
        inicializarUbicaciones()
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(brasil.id!!,jamaica.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(argentina.id!!,venezuela.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(venezuela.id!!,jamaica.id!!,TipoDeCamino.Terrestre)
        ubicacionService.mover(paciente.id!!, jamaica.id!!)

        paciente = vectorService.recuperar(paciente.id!!)
        Assert.assertTrue(paciente.ubicacion.id == jamaica.id!!)
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

    @Test
    fun intento_conectar_ubicaciones_inexistentes_y_arroja_una_excepcion(){
       assertThrows<java.lang.Exception> { ubicacionService.conectar(999,998,TipoDeCamino.Aereo) }
    }

    @Test
    fun conecto_a_argentina_con_brasil_y_otra_ubicacion_y_aparecen_en_la_lista(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Maritimo)

        val conexionesDeArg = ubicacionService.conectados(argentina.id!!)
        conexionesDeArg.forEach { print("esta es una ubicacion ${it.nombre}") }
        Assert.assertTrue(conexionesDeArg.any { it.id == brasil.id!! })
        Assert.assertTrue(conexionesDeArg.any { it.id == otraUbicacion.id!! })
        Assert.assertFalse(conexionesDeArg.any { it.id == jamaica.id!! })
    }

    @Test
    fun no_conecto_a_argentina_y_su_lista_de_conexiones_esta_vacia(){
        inicializarUbicaciones()
        val conexionesDeArg = ubicacionService.conectados(argentina.id!!)
        Assert.assertTrue(conexionesDeArg.isEmpty())
    }

    @Test
    fun pido_los_conectados_de_una_ubicacion_que_no_existe_y_arroja_una_excepcion(){
        assertThrows<java.lang.Exception> { ubicacionService.conectados(999) }
    }

    @Test
    fun pido_la_capacidad_de_expansion_de_un_vector_que_no_existe_y_arroja_una_excepcion(){
        assertThrows<Exception>{ ubicacionService.capacidadDeExpansion(99,2)
        }
    }

    @Test
    fun pez_puede_expandirse_a_3_ubicaciones_aunque_haya_dos_caminos_mas_cortos_a_la_misma_ubicacion(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(otraUbicacion.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(brasil.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        pez = vectorService.crear(TipoDeVector.Animal,argentina.id!!)

        Assert.assertEquals(3, ubicacionService.capacidadDeExpansion(pez.id!!,2))
    }

    @Test
    fun paciente_puede_expandirse_a_3_ubicaciones_aunque_no_pueda_pasar_por_uno_de_los_caminos_mas_cortos_a_venezuela(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(otraUbicacion.id!!,venezuela.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(brasil.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        pez = vectorService.crear(TipoDeVector.Animal,argentina.id!!)

        Assert.assertEquals(3, ubicacionService.capacidadDeExpansion(pez.id!!,2))
    }

    @Test
    fun hormiga_puede_expandirse_a_1_ubicacion_porque_no_puede_transitar_por_mar(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(otraUbicacion.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(brasil.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        hormiga = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)

        Assert.assertEquals(1, ubicacionService.capacidadDeExpansion(hormiga.id!!,2))
    }

    @Test
    fun paciente_puede_expandirse_a_2_ubicaciones_porque_no_puede_transitar_por_Aire(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Aereo)
        ubicacionService.conectar(otraUbicacion.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(brasil.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)

        Assert.assertEquals(2, ubicacionService.capacidadDeExpansion(paciente.id!!,2))
    }

    @Test
    fun hormiga_puede_expandirse_a_0_ubicaciones_porque_no_hay_caminos(){
        inicializarUbicaciones()
        hormiga = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)

        Assert.assertEquals(0, ubicacionService.capacidadDeExpansion(hormiga.id!!,2))
    }

    @Test
    fun paciente_puede_expandirse_solo_a_2_ubicaciones_porque_el_camino_mas_corto_a_venezuela_es_por_aire(){
        inicializarUbicaciones()
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,brasil.id!!,TipoDeCamino.Terrestre)
        ubicacionService.conectar(argentina.id!!,otraUbicacion.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(otraUbicacion.id!!,venezuela.id!!,TipoDeCamino.Maritimo)
        ubicacionService.conectar(argentina.id!!,venezuela.id!!,TipoDeCamino.Aereo)
        paciente = vectorService.crear(TipoDeVector.Persona,argentina.id!!)

        Assert.assertEquals(2, ubicacionService.capacidadDeExpansion(paciente.id!!,2))
    }

}