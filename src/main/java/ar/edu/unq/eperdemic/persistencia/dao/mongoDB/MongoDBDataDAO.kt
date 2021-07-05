package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.eventos.Evento
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.mongoDB.MongoDBTransaction

class MongoDBDataDAO:DataDAO {

    override fun clear() {
        TransactionRunner.runTrx {
            val collection = MongoDBTransaction.getCollection("Evento", Evento::class.java)
            val session = MongoDBTransaction.currentSession
            collection.drop(session)
        }
    }
}