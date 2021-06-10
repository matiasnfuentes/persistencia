package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class ServiceTest(var dataDAO : DataDAO) {

    var virus = Patogeno("Virus",57,30,30,50,30)
    val virus2 = Patogeno("Virus",57,30,30,50,32)
    val bacteria = Patogeno("Bacteria",88,20,20,30,12)
    val fiebre = Mutacion("Fiebre",1, mutableListOf(),Atributo.LETALIDAD,10)
    val vomitos = Mutacion("Vomitos",2, mutableListOf(fiebre),Atributo.LETALIDAD,10)
    val especieDAO = HibernateEspecieDAO()
    val patogenoDAO = HibernatePatogenoDAO()
    val ubicacionDAO = HibernateUbicacionDAO()
    val vectorDAO = HibernateVectorDAO()
    val mutacionDAO = HibernateMutacionDAO()
    val vectorService = VectorServiceImpl(vectorDAO,especieDAO,ubicacionDAO)
    val ubicacionService = UbicacionServiceImpl(ubicacionDAO,vectorDAO)
    val especieService = EspecieServiceImpl(especieDAO, ubicacionDAO)
    val patogenoService = PatogenoServiceImpl(patogenoDAO,especieDAO,ubicacionDAO)
    val mutacionService = MutacionServiceImpl(mutacionDAO, especieDAO)
    val estadisticaService = EstadisticasServiceImpl(especieDAO, vectorDAO)

    //Ubicaciones
    lateinit var otraUbicacion : Ubicacion
    lateinit var argentina : Ubicacion
    lateinit var jamaica : Ubicacion
    lateinit var brasil : Ubicacion

    //Especies
    lateinit var covid : Especie
    lateinit var flu : Especie
    lateinit var gripe : Especie
    lateinit var especie4 : Especie
    lateinit var especie5 : Especie
    lateinit var especie6 : Especie
    lateinit var especie7 : Especie
    lateinit var especie8 : Especie
    lateinit var especie9 : Especie
    lateinit var especie10 : Especie
    lateinit var especie11 : Especie
    lateinit var covidBrasilero : Especie

    // Personas
    lateinit var paciente: Vector
    lateinit var pepe : Vector
    lateinit var jose : Vector
    lateinit var juancho : Vector
    lateinit var jorge : Vector
    lateinit var raul : Vector

    // Insectos

    lateinit var cucaracha: Vector
    lateinit var hormiga: Vector
    lateinit var mosquito: Vector

    // Animales
    lateinit var vaca : Vector
    lateinit var rana : Vector
    lateinit var perro : Vector
    lateinit var pez : Vector
    lateinit var pezArgentino : Vector
    lateinit var pezBrasilero : Vector
    lateinit var sapoJamaiquino : Vector

    @BeforeEach
    fun inizializate() {
        TransactionRunner.transaction = HibernateTransaction
    }

    @AfterEach
    fun eliminarTodo() {
        dataDAO.clear()
    }

    fun crearDatosParaTesteosDeEstadisticas(){
        patogenoService.crear(virus)
        argentina = ubicacionService.crear("Argentina")
        jamaica = ubicacionService.crear("jamaica")
        covid = patogenoService.agregarEspecie(virus.id!!,"Covid",argentina.id!!)
        flu = patogenoService.agregarEspecie(virus.id!!,"Flu",argentina.id!!)
        gripe = patogenoService.agregarEspecie(virus.id!!,"Gripe",argentina.id!!)

        especie4 = patogenoService.agregarEspecie(virus.id!!,"especie4",argentina.id!!)
        especie5 = patogenoService.agregarEspecie(virus.id!!,"especie5",argentina.id!!)
        especie6 = patogenoService.agregarEspecie(virus.id!!,"especie6",argentina.id!!)
        especie7 = patogenoService.agregarEspecie(virus.id!!,"especie7",argentina.id!!)
        especie8 = patogenoService.agregarEspecie(virus.id!!,"especie8",argentina.id!!)
        especie9 = patogenoService.agregarEspecie(virus.id!!,"especie9",argentina.id!!)
        especie10 = patogenoService.agregarEspecie(virus.id!!,"especie10",argentina.id!!)
        especie11 = patogenoService.agregarEspecie(virus.id!!,"especie11",argentina.id!!)

        pepe = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        jose = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        juancho = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        jorge = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        raul = vectorService.crear(TipoDeVector.Persona,argentina.id!!)
        cucaracha = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        hormiga = vectorService.crear(TipoDeVector.Insecto,argentina.id!!)
        vaca = vectorService.crear(TipoDeVector.Animal,argentina.id!!)
        rana = vectorService.crear(TipoDeVector.Animal,argentina.id!!)
        perro = vectorService.crear(TipoDeVector.Animal,argentina.id!!)
    }
}