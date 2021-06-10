package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import org.junit.Assert
import org.junit.jupiter.api.Test


class VectorServiceTest :ServiceTest(HibernateDataDAO()) {

    val personas = mutableListOf<Vector>()
    var adnAntesDeInfectar = 0

    @Test
    fun persisto_un_vector_y_puedo_recuperarlo() {
        val argentina = ubicacionService.crear("Argentina")
        //Exercise
        val mosquito = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        val mosquitoRecuperado = vectorService.recuperar(mosquito.id!!)

        // Verify
        Assert.assertEquals(mosquito.id!!,mosquitoRecuperado.id!!)
    }

    @Test
    fun persisto_un_tres_vectores_y_puedo_recuperarlos() {
        val argentina = ubicacionService.crear("Argentina")
        //Exercise
        val mosquito = vectorService.crear(TipoDeVector.Insecto, argentina.id!!)
        val pejeLagarto = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        val pacienteCovid = vectorService.crear(TipoDeVector.Persona, argentina.id!!)

        val vectoresRecuperados = vectorService.recuperarTodos()

        // Verify
        Assert.assertEquals(3, vectoresRecuperados.size)
        Assert.assertTrue(vectoresRecuperados.any {it.id == mosquito.id!!} )
        Assert.assertTrue(vectoresRecuperados.any {it.id == pejeLagarto.id!!} )
        Assert.assertTrue(vectoresRecuperados.any {it.id == pacienteCovid.id!!} )
    }

    @Test
    fun no_persisto_ningun_vector_y_recupero_una_lista_vacia() {
        // Excercise
        val vectoresRecuperados = vectorService.recuperarTodos()

        // Verify
        Assert.assertTrue(vectoresRecuperados.isEmpty())
    }

    @Test
    fun consulto_las_enfermedades_de_un_vector_enfermo_y_las_obtengo() {
        val argentina = ubicacionService.crear("Argentina")
        // Set up
        val virusCreado = patogenoService.crear(virus)
        val covidArgentino = patogenoService.agregarEspecie(virusCreado.id!!, "covid",argentina.id!!)
        val fluArgentino = patogenoService.agregarEspecie(virusCreado.id!!, "flu",argentina.id!!)

        //Exercise
        val mosquito = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        vectorService.infectar(mosquito.id!!, covidArgentino.id!!)
        vectorService.infectar(mosquito.id!!, fluArgentino.id!!)

        //Verify
        Assert.assertTrue(vectorService.enfermedades(mosquito.id!!).any{it.nombre == "covid"})
        Assert.assertTrue(vectorService.enfermedades(mosquito.id!!).any{it.nombre == "flu"})
        Assert.assertEquals(2, vectorService.enfermedades(mosquito.id!!).size)
    }

    fun crearDatosParaTestearAumentoDeADN(){
        argentina = ubicacionService.crear("Argentina")
        virus = patogenoService.crear(virus)
        covid = patogenoService.agregarEspecie(virus.id!!, "covid",argentina.id!!)
        adnAntesDeInfectar = covid.adnDisponible
    }

    fun infectarPersonas(cantidad: Int, especieID: Long){
        for (num in 1..cantidad){
            personas.add(vectorService.crear(TipoDeVector.Persona,argentina.id!!))
        }
        personas.forEach {
            vectorService.infectar(it.id!!,especieID)
        }
    }

    @Test
    fun infecto_5_humanos_con_covid_y_la_especie_gana_1_de_adn(){
        crearDatosParaTestearAumentoDeADN()
        infectarPersonas(5,covid.id!!)
        val covidDespuesDeInfectar = especieService.recuperar(covid.id!!)
        Assert.assertEquals(0,adnAntesDeInfectar)
        Assert.assertEquals(1,covidDespuesDeInfectar.adnDisponible)
    }

    @Test
    fun infecto_10_humanos_con_covid_y_la_especie_gana_2_de_adn(){
        crearDatosParaTestearAumentoDeADN()
        infectarPersonas(10,covid.id!!)
        val covidDespuesDeInfectar = especieService.recuperar(covid.id!!)
        Assert.assertEquals(0,adnAntesDeInfectar)
        Assert.assertEquals(2,covidDespuesDeInfectar.adnDisponible)
    }

    @Test
    fun infecto_9_humanos_con_covid_y_la_especie_gana_1_de_adn(){
        crearDatosParaTestearAumentoDeADN()
        infectarPersonas(9,covid.id!!)
        val covidDespuesDeInfectar = especieService.recuperar(covid.id!!)
        Assert.assertEquals(0,adnAntesDeInfectar)
        Assert.assertEquals(1,covidDespuesDeInfectar.adnDisponible)
    }

    @Test
    fun infecto_4_humanos_con_covid_y_la_especie_no_gana_adn(){
        crearDatosParaTestearAumentoDeADN()
        infectarPersonas(4,covid.id!!)
        val covidDespuesDeInfectar = especieService.recuperar(covid.id!!)
        Assert.assertEquals(covidDespuesDeInfectar.adnDisponible,adnAntesDeInfectar)
    }



}