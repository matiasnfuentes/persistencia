package ar.edu.unq.eperdemic.services.runner.hibernate
import ar.edu.unq.eperdemic.services.runner.Transaction
import org.hibernate.Session

object HibernateTransaction: Transaction() {

    private var sessionThreadLocal: ThreadLocal<Session?> = ThreadLocal()
    private var transaction: ThreadLocal<org.hibernate.Transaction?> = ThreadLocal()


    val currentSession: Session
        get() {
            if(sessionThreadLocal.get() == null){
                start()
            }
            return sessionThreadLocal.get()!!
        }

    override fun start() {
        val session = HibernateSessionFactoryProvider.instance.createSession()
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

