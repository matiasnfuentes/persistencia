package ar.edu.unq.eperdemic.modelo

import kotlin.random.Random

 object Randomizador{

        fun getRandomVectorInfectado(vectores: List<Vector>): Vector?{
            return vectores.filter { it.puedeContagiar() }.randomOrNull()
        }

        fun getPorcentajeDeContagio():Int{
            return Random.nextInt(1, 10)
        }

        fun getPorcentajeASuperar():Int{
            return Random.nextInt(1, 100)
        }
}