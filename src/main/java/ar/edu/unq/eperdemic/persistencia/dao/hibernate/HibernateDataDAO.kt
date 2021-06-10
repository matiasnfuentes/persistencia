package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction

open class HibernateDataDAO : DataDAO {

    override fun clear() {
        runTrx {
            val session = HibernateTransaction.currentSession
            val nombreDeTablas = session.createNativeQuery("show tables").resultList

            session.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()

            nombreDeTablas.forEach { result ->
                var tabla = ""

                when(result){
                    is String -> tabla = result
                    is Array<*> -> tabla= result[0].toString()
                }
                session.createNativeQuery("truncate table $tabla").executeUpdate()
            }
            session.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
        }
    }

}