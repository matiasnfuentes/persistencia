package ar.edu.unq.eperdemic.modelo

import kotlin.random.Random

 object Randomizador{

        fun getRandomVectorInfectado(vectores: List<Vector>, ubicacionId: Long): Vector?{
            return vectores
                        .filter { (it.ubicacion).id == ubicacionId && it.puedeContagiar() }
                        .randomOrNull()
        }

        fun getPorcentajeDeContagio():Int{
            return Random.nextInt(1, 10)
        }

        fun getPorcentajeASuperar():Int{
            return Random.nextInt(1, 100)
        }
}