package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.Assert
import org.junit.jupiter.api.Test


class EspecieServiceTest: ServiceTest() {

    @Test
    fun persisto_una_especie_y_puedo_recuperarla() {
        // Set up
        val argentina = ubicacionService.crear("Argentina")
        patogenoService.crear(virus)

        //Exercise
        val nuevaEspecie = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        val especieRecuperada = especieService.recuperar(nuevaEspecie.id!!)

        // Verify
        Assert.assertEquals(nuevaEspecie.id,especieRecuperada.id)
        Assert.assertEquals(nuevaEspecie.nombre,especieRecuperada.nombre)
    }

    @Test
    fun persisto_dos_especies_y_puedo_recuperarlas() {
        // Set up
        val argentina = ubicacionService.crear("Argentina")
        patogenoService.crear(virus)

        //Exercise
        patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        patogenoService.agregarEspecie(virus.id!!,"influenza",argentina.id!!)
        val especiesRecuperadas = especieService.recuperarTodos()

        // Verify
        Assert.assertEquals(2,especiesRecuperadas.size)
        Assert.assertTrue(especiesRecuperadas.any {it.nombre == "covid"})
        Assert.assertTrue(especiesRecuperadas.any {it.nombre == "influenza"})
    }

    @Test
    fun no_persisto_ninguna_especie_y_al_recuperarlas_obtengo_lista_vacia() {
        //Exercise
        val especiesRecuperadas = especieService.recuperarTodos()

        // Verify
        Assert.assertTrue(especiesRecuperadas.isEmpty())
    }

    @Test
    fun persisto_una_especie_y_su_cantidad_de_infectados_es_0() {
        // Set up
        val argentina = ubicacionService.crear("Argentina")
        patogenoService.crear(virus)
        val nuevaEspecie = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)

        //Exercise
        val cant = especieService.cantidadDeInfectados(nuevaEspecie.id!!)

        // Verify
        Assert.assertEquals(0,cant)
    }

    @Test
    fun persisto_una_especie_la_recupero_aumento_sus_infectados_y_retorna_el_valor_correcto() {
        // Set up
        val argentina = ubicacionService.crear("Argentina")
        patogenoService.crear(virus)
        val nuevaEspecie = patogenoService.agregarEspecie(virus.id!!,"covid",argentina.id!!)
        val especieRecuperada = especieService.recuperar(nuevaEspecie.id!!)

        // Fuerzo la actualización de la especie de esta manera
        // ya que Especie Service no tiene el método actualizar.

        especieRecuperada.cantidadDeInfectados = 5
        runTrx{
            especieDAO.actualizar(especieRecuperada)
        }

        //Exercise
        val cant = especieService.cantidadDeInfectados(nuevaEspecie.id!!)

        // Verify
        Assert.assertEquals(5,cant)
    }

    fun crearDatosParaTestearEsPandemia(){
        brasil = ubicacionService.crear("Brasil")
        jamaica = ubicacionService.crear("Jamaica")
        ubicacionService.crear("Rusia")
        patogenoService.crear(virus)
        covidBrasilero = patogenoService.agregarEspecie(virus.id!!,"covid",brasil.id!!)
        pezBrasilero = vectorService.crear(TipoDeVector.Animal,brasil.id!!)
        sapoJamaiquino = vectorService.crear(TipoDeVector.Animal,jamaica.id!!)
    }

    @Test
    fun la_enfermedad_no_es_pandemia_porque_no_esta_en_niguna_ubicacion(){
        crearDatosParaTestearEsPandemia()
        // Especie presente en 0 de 3 ubicaciones
        Assert.assertFalse(especieService.esPandemia(covidBrasilero.id!!))
    }

    @Test
    fun la_enfermedad_es_pandemia_porque_esta_en_mas_de_la_mitad_de_ubicaciones(){
        crearDatosParaTestearEsPandemia()
        vectorService.infectar(pezBrasilero.id!!,covidBrasilero.id!!)
        vectorService.infectar(sapoJamaiquino.id!!,covidBrasilero.id!!)
        // Especie presente en 2 de 3 ubicaciones
        Assert.assertTrue(especieService.esPandemia(covidBrasilero.id!!))
    }

    @Test
    fun la_enfermedad_no_es_pandemia_porque_esta_justo_en_la_mitad_de_ubicaciones(){
        crearDatosParaTestearEsPandemia()
        ubicacionService.crear("Nicaragua")
        vectorService.infectar(pezBrasilero.id!!,covidBrasilero.id!!)
        vectorService.infectar(sapoJamaiquino.id!!,covidBrasilero.id!!)
        // Especie presente en 2 de 4 ubicaciones
        Assert.assertFalse(especieService.esPandemia(covidBrasilero.id!!))
    }


}