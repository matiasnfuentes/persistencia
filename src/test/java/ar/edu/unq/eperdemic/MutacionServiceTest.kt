package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.exceptions.NoCumpleCondicionesDeMutacionException
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MutacionServiceTest: ServiceTest(HibernateDataDAO()) {

    @Test
    fun creo_una_mutacion_y_se_persiste_entonces_lo_puedo_recuperar() {
        mutacionService.crear(fiebre)
        val fiebreRecuperada = mutacionService.recuperar(fiebre.id!!)
        Assert.assertEquals(fiebre.id, fiebreRecuperada.id)
    }

    @Test
    fun puedo_recuperar_todas_las_mutaciones_de_una_db_con_dos_mutaciones() {
        mutacionService.crear(fiebre)
        mutacionService.crear(vomitos)
        val mutacionesRecuperadas = mutacionService.recuperarTodos()
        Assert.assertEquals(2, mutacionesRecuperadas.size)
        Assert.assertTrue(mutacionesRecuperadas.any { it.nombre == "Fiebre" })
        Assert.assertTrue(mutacionesRecuperadas.any { it.nombre == "Vomitos" })
    }

    @Test
    fun trato_de_recuperarTodos_en_una_db_vacia_y_devuelve_una_lista_vacia() {
        val mutacionesRecuperadas = mutacionService.recuperarTodos()
        Assert.assertEquals(0, mutacionesRecuperadas.size)
    }

    fun crearDatosParaTestearMutaciones(){
        patogenoService.crear(virus)
        argentina = ubicacionService.crear("Argentina")
        covid = patogenoService.agregarEspecie(virus.id!!,"Covid",argentina.id!!)
        mutacionService.crear(fiebre)
        mutacionService.crear(vomitos) // Vomitos requiere fiebre
        covid.adnDisponible = 10
        runTrx{
            especieDAO.actualizar(covid)
        }
    }

    @Test
    fun no_puedo_mutar_a_una_especie_sin_adn(){
        crearDatosParaTestearMutaciones()
        covid.adnDisponible = 0
        runTrx{
            especieDAO.actualizar(covid)
        }
        assertThrows<NoCumpleCondicionesDeMutacionException> {
            mutacionService.mutar(covid.id!!,fiebre.id!!)
        }
    }

    @Test
    fun puedo_mutar_a_una_especie_con_adn_suficiente(){
        crearDatosParaTestearMutaciones()
        mutacionService.mutar(covid.id!!,fiebre.id!!)
        val covidMutado = especieService.recuperar(covid.id!!)
        Assert.assertTrue(covidMutado.mutaciones.any { it.nombre == "Fiebre" })
    }

    @Test
    fun no_puedo_mutar_a_una_especie_si_no_cumple_con_los_requerimientos(){
        crearDatosParaTestearMutaciones()
        // vomitos requiere fiebre
        assertThrows<NoCumpleCondicionesDeMutacionException> {
            mutacionService.mutar(covid.id!!,vomitos.id!!)
        }
    }

    @Test
    fun puedo_mutar_a_una_especie_con_adn_suficiente_si_cumple_con_los_requerimientos(){
        crearDatosParaTestearMutaciones()
        mutacionService.mutar(covid.id!!,fiebre.id!!)
        mutacionService.mutar(covid.id!!,vomitos.id!!)
        val covidMutado = especieService.recuperar(covid.id!!)
        Assert.assertTrue(covidMutado.mutaciones.any { it.nombre == "Fiebre" })
        Assert.assertTrue(covidMutado.mutaciones.any { it.nombre == "Vomitos" })
    }

    @Test
    fun mutarAUnaEspecieAumentaSusAtributos(){
        crearDatosParaTestearMutaciones()
        val letalidadPreMutacion = covid.letalidad()
        // La letalidad base del covid es 30
        // Fiebre aumenta letalidad en 10
        mutacionService.mutar(covid.id!!,fiebre.id!!)
        val covidMutado = especieService.recuperar(covid.id!!)
        val letalidadPostMutacion = covidMutado.letalidad()
        Assert.assertEquals(30 , letalidadPreMutacion)
        Assert.assertEquals(40 , letalidadPostMutacion)
    }

    @Test
    fun no_puedo_mutar_a_una_especie_dos_veces_con_la_misma_mutacion(){
        crearDatosParaTestearMutaciones()
        mutacionService.mutar(covid.id!!,fiebre.id!!)
        assertThrows<NoCumpleCondicionesDeMutacionException> {
            mutacionService.mutar(covid.id!!,fiebre.id!!)
        }
    }

}