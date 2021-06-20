package ar.edu.unq.eperdemic.services.runner

abstract class Transaction {
    abstract fun start()
    abstract fun commit()
    abstract fun rollback()
    abstract fun isRunning():Boolean
}

object TransactionRunner {

    lateinit var transactions: List<Transaction>

    fun <T> runTrx(bloque: ()->T): T {
        try {
            val result = bloque()
            forEachRunningTransaction { t -> t.commit() }
            return result
        }catch (e: Throwable){
            forEachRunningTransaction { t -> t.rollback() }
            throw e
        }
    }

    fun forEachRunningTransaction(f : (Transaction) -> Unit){
        transactions.filter { it.isRunning() }.forEach{ f(it) }
    }
}