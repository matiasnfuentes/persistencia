package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EstadisticaServiceTest: ServiceTest() {

    @Test
    fun covid_es_lider_porque_infecto_3_humanos(){
        crearDatosParaTesteosDeEstadisticas()

        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(jose.id!!,covid.id!!)
        vectorService.infectar(juancho.id!!,covid.id!!)

        vectorService.infectar(raul.id!!,gripe.id!!)
        vectorService.infectar(hormiga.id!!,gripe.id!!)
        vectorService.infectar(cucaracha.id!!,gripe.id!!)

        Assert.assertEquals(covid.id!!,estadisticaService.especieLider().id!!)
    }

    @Test
    fun gripe_es_lider_porque_infecto_3_humanos(){
        crearDatosParaTesteosDeEstadisticas()

        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(cucaracha.id!!,covid.id!!)

        vectorService.infectar(juancho.id!!,gripe.id!!)
        vectorService.infectar(jorge.id!!,gripe.id!!)
        vectorService.infectar(raul.id!!,gripe.id!!)

        Assert.assertEquals(gripe.id!!,estadisticaService.especieLider().id!!)
    }

    @Test
    fun flu_es_lider_porque_infecto_2_humanos(){
        crearDatosParaTesteosDeEstadisticas()

        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(hormiga.id!!,covid.id!!)

        vectorService.infectar(jorge.id!!,gripe.id!!)

        vectorService.infectar(raul.id!!,flu.id!!)
        vectorService.infectar(jose.id!!,flu.id!!)

        Assert.assertEquals(flu.id!!,estadisticaService.especieLider().id!!)
    }

    @Test
    fun no_hay_especie_lider(){
       assertThrows<Exception> { estadisticaService.especieLider() }
    }

    @Test
    fun no_hay_lideres(){
        Assert.assertTrue( estadisticaService.lideres().isEmpty())
    }

    @Test
    fun flu_y_covid_son_lideres(){
        crearDatosParaTesteosDeEstadisticas()
        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(rana.id!!,flu.id!!)
        val lideres = estadisticaService.lideres()
        Assert.assertTrue( lideres.any { it.nombre == "Covid" })
        Assert.assertTrue( lideres.any { it.nombre == "Flu" })
    }

    @Test
    fun especie11_no_entra_en_el_top_de_lideres_ya_que_solo_son_10_especies_lideres(){
        crearDatosParaTesteosDeEstadisticas()
        val candidatosLideres =
            mutableListOf<Especie>(covid,flu,gripe,especie4,especie5,especie6,especie7,especie8,especie9,especie10)

        candidatosLideres.forEach {
            vectorService.infectar(pepe.id!!, it.id!!)
            vectorService.infectar(rana.id!!, it.id!!)
        }
        vectorService.infectar(juancho.id!!, especie11.id!!)
        val lideres = estadisticaService.lideres()
        Assert.assertTrue(lideres.map { it.id }.containsAll(candidatosLideres.map { it.id }))
        Assert.assertFalse(lideres.any { it.id == especie11.id })
    }

    @Test
    fun cuento_vectores_y_vectores_infectados_y_es_correcto(){
        crearDatosParaTesteosDeEstadisticas()

        // Todos los vectores que infectamos son argentinos
        // Infectamos a algunos con varias especies para controlar
        // que no se esten contando dos veces.
        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(jorge.id!!, covid.id!!)
        vectorService.infectar(jorge.id!!, flu.id!!)
        vectorService.infectar(raul.id!!, covid.id!!)
        vectorService.infectar(raul.id!!, flu.id!!)
        vectorService.infectar(rana.id!!, covid.id!!)

        val cantArg = runTrx{vectorDAO.vectoresEnUbicacion(argentina.id!!) }
        val infectadosArg = runTrx{ vectorDAO.vectoresInfectadosEnUbicacion(argentina.id!!) }
        val cantJam = runTrx{ vectorDAO.vectoresEnUbicacion(jamaica.id!!) }
        val infectadosJam = runTrx{ vectorDAO.vectoresInfectadosEnUbicacion(jamaica.id!!) }

        Assert.assertEquals(4, infectadosArg)
        Assert.assertEquals(0, infectadosJam)
        Assert.assertEquals(10, cantArg)
        Assert.assertEquals(0, cantJam)
    }

    @Test
    fun covid_es_la_especie_mas_infecciosa_en_arg(){
        crearDatosParaTesteosDeEstadisticas()

        vectorService.infectar(pepe.id!!,covid.id!!)
        vectorService.infectar(jorge.id!!, covid.id!!)
        vectorService.infectar(raul.id!!, covid.id!!)
        vectorService.infectar(rana.id!!, covid.id!!)

        vectorService.infectar(vaca.id!!,gripe.id!!)

        vectorService.infectar(jorge.id!!, flu.id!!)
        vectorService.infectar(raul.id!!, flu.id!!)
        val masInfecciosa = runTrx{especieDAO.especieMasInfecciosa(argentina.id!!) }

        Assert.assertEquals("Covid", masInfecciosa.nombre)
    }

    @Test
    fun flu_es_la_especie_mas_infecciosa_en_arg_aunque_infecte_solo_insectos(){
        crearDatosParaTesteosDeEstadisticas()

        vectorService.infectar(pepe.id!!,covid.id!!)

        vectorService.infectar(vaca.id!!,gripe.id!!)

        vectorService.infectar(hormiga.id!!, flu.id!!)
        vectorService.infectar(cucaracha.id!!, flu.id!!)

        val masInfecciosa = runTrx{especieDAO.especieMasInfecciosa(argentina.id!!) }

        Assert.assertEquals("Flu", masInfecciosa.nombre)
    }

    @Test
    fun no_hay_especie_mas_infecciosa_en_jamaica_ya_que_no_hay_infectados(){
        crearDatosParaTesteosDeEstadisticas()
        assertThrows<Exception> { runTrx{especieDAO.especieMasInfecciosa(jamaica.id!!) } }
    }

}