package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.runner.neo4j.Neo4jTransaction

class Neo4jDataDAO:DataDAO {

    override fun clear() {
        runTrx{
            val transaction = Neo4jTransaction.currentTransaction
            transaction.run("MATCH (n) DETACH DELETE n")
        }
    }

}