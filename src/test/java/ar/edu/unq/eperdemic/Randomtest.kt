package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.TipoContagio
import ar.edu.unq.eperdemic.persistencia.dao.firebase.FirebaseEventDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.MongoDBDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.MongoDBEventDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jConexionesDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction
import ar.edu.unq.eperdemic.services.runner.neo4j.Neo4jTransaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class RandomTest {
    var virus = Patogeno("Virus",57,30,30,50,30)
    var virus2 = Patogeno("Virus",57,30,30,50,32)
    val bacteria = Patogeno("Bacteria",88,20,20,30,12)
    val fiebre = Mutacion("Fiebre",1, mutableListOf(), Atributo.LETALIDAD,10)
    val vomitos = Mutacion("Vomitos",2, mutableListOf(fiebre), Atributo.LETALIDAD,10)
    val especieDAO = HibernateEspecieDAO()
    val patogenoDAO = HibernatePatogenoDAO()
    val ubicacionDAO = HibernateUbicacionDAO()
    val vectorDAO = HibernateVectorDAO()
    val mutacionDAO = HibernateMutacionDAO()
    val conexionesDAO = Neo4jConexionesDAO()
    val eventDAO = MongoDBEventDAO()
    val especieService = EspecieServiceImpl(especieDAO, ubicacionDAO)
    val vectorService = VectorServiceImpl(vectorDAO,especieDAO,ubicacionDAO,especieService, AlarmaDeEventos)
    val feedService = FeedServiceImpl(eventDAO,vectorDAO,patogenoDAO,ubicacionDAO)
    val ubicacionService = UbicacionServiceImpl(ubicacionDAO,vectorDAO,conexionesDAO,especieDAO,especieService,
        AlarmaDeEventos
    )
    val patogenoService = PatogenoServiceImpl(patogenoDAO,especieDAO,ubicacionDAO, AlarmaDeEventos)
    val mutacionService = MutacionServiceImpl(mutacionDAO, especieDAO, AlarmaDeEventos)
    val estadisticaService = EstadisticasServiceImpl(especieDAO, vectorDAO)
    val dataDAOS = listOf(Neo4jDataDAO(),HibernateDataDAO(), MongoDBDataDAO())

    @BeforeEach
    fun inizializate() {
        AlarmaDeEventos.agregar(eventDAO)
        TransactionRunner.transactions = listOf(HibernateTransaction, Neo4jTransaction)//,MongoDBTransaction)
    }

    @AfterEach
    fun eliminarTodo() {
        dataDAOS.forEach { it.clear() }
        AlarmaDeEventos.eliminarTodos()
    }

    @Test
    fun randomtest() {

        val pat = Patogeno("Virus",10,10,5,5,33)
        val argentina = ubicacionService.crear("Argentina")
        val covid = Especie(pat,"covid",argentina)
        val eventDAO = FirebaseEventDAO()
        val pescao = Vector(TipoDeVector.Animal,argentina)
        eventDAO.actualizar(covid,argentina,TipoContagio.PrimerContagioEnUbicacion)
        eventDAO.actualizar(covid)
        eventDAO.actualizar(pescao,argentina)
        eventDAO.actualizar(covid,pescao)



        // Response is a message ID string.
        /*
        val db = FirebaseConnection.dataBase




        val ubicacion = Ubicacion("pepe")
        db.collection("Eventos").document("asd").set(ubicacion)


        val future = db.collection("Eventos").get()

        val documents = future.get().documents

        print("documentos : ")
        print(documents.isEmpty())
        documents.forEach { print(it.get("nombre")) }*/
    }
}