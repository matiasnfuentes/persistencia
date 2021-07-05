package ar.edu.unq.eperdemic.services.runner.mongoDB

import ar.edu.unq.eperdemic.services.runner.Transaction
import com.mongodb.client.ClientSession
import com.mongodb.client.MongoCollection

object MongoDBTransaction: Transaction() {

    private var sessionThreadLocal: ThreadLocal<ClientSession?> = ThreadLocal()

    val currentSession: ClientSession
        get() {
            if(sessionThreadLocal.get() == null){
                start()
            }
            return sessionThreadLocal.get()!!
    }

    fun <T> getCollection(name:String, entityType: Class<T> ): MongoCollection<T> {
        return MongoConnection.instance.getCollection(name, entityType)
    }

    //Queda comentado ya que no lo pudimos terminar de hacer funcionar con transacciones
    override fun start() {
        val session = MongoConnection.instance.createSession()
        sessionThreadLocal.set(session)
        //sessionThreadLocal.get()?.startTransaction(TransactionOptions.builder().readConcern(ReadConcern.LOCAL).writeConcern(
        //    WriteConcern.W1).build())
    }

    override fun commit() {
        sessionThreadLocal.get()?.commitTransaction()
    }

    override fun rollback() {
        sessionThreadLocal.get()?.abortTransaction()
    }

    override fun isRunning(): Boolean {
        return  sessionThreadLocal.get() != null
    }
}
