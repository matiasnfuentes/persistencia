package ar.edu.unq.eperdemic.services.runner.neo4j

import ar.edu.unq.eperdemic.services.runner.Transaction
import org.neo4j.driver.*


object Neo4jTransaction: Transaction() {

    private var sessionThreadLocal: ThreadLocal<Session?> = ThreadLocal()
    var transaction: ThreadLocal<org.neo4j.driver.Transaction?> = ThreadLocal()


    val currentTransaction: org.neo4j.driver.Transaction
        get() {
            if(transaction.get() == null){
                start()
            }
            return transaction.get()!!
        }

    override fun start() {
        val session = Neo4jSessionFactoryProvider.instance.createSession()
        sessionThreadLocal.set(session)
        transaction.set(session.beginTransaction())
    }

    override fun commit() {
        transaction.get()?.commit()
        close()
    }

    override fun rollback() {
        transaction.get()?.rollback()
        close()
    }

    fun close() {
        sessionThreadLocal.get()?.close()
        sessionThreadLocal.set(null)
        transaction.set(null)
    }

    override fun isRunning(): Boolean {
        return transaction.get() != null
    }
}