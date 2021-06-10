package ar.edu.unq.eperdemic.spring

import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EperdemicApplication

fun main(args: Array<String>) {
	TransactionRunner.transaction = HibernateTransaction
	runApplication<EperdemicApplication>(*args)
}
