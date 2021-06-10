package ar.edu.unq.eperdemic.services.runner

abstract class Transaction {
    abstract fun start()
    abstract fun commit()
    abstract fun rollback()
    abstract fun isRunning():Boolean
}

object TransactionRunner {

    lateinit var transaction: Transaction

    fun <T> runTrx(bloque: ()->T): T {
        if(transaction.isRunning()){
            return bloque()
        }
        transaction.start()
        try {
            val result = bloque()
            transaction.commit()
            return result
        }catch (e: Throwable){
            transaction.rollback()
            throw e
        }
    }
}