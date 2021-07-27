package ar.edu.unq.eperdemic.spring

import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction
import ar.edu.unq.eperdemic.services.runner.neo4j.Neo4jTransaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EperdemicApplication

fun main(args: Array<String>) {
	TransactionRunner.transactions = listOf(HibernateTransaction , Neo4jTransaction)
	runApplication<EperdemicApplication>(*args)
}
