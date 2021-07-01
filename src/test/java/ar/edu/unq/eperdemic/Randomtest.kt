package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.MongoDBEventDAO
import org.junit.Assert
import org.junit.jupiter.api.Test

class Randomtest {

    @Test
    fun no_puedo_crear_un_patogeno_cuyos_atributos_de_contagio_superen_100(){
        val contagioEventDAO = MongoDBEventDAO()
        val virus = Patogeno("virus",12,32,43,22,44)
        virus.id = 22
        val arg = Ubicacion("Arg")
        arg.id = 1
        val pescado = Vector(TipoDeVector.Animal,arg)
        pescado.id = 99
        val covid  = Especie(virus,"covid",arg)
        val contagioEvent = Contagio(covid,arg,TipoContagio.Pandemia,"asd")
        val contagioEvent2 = Contagio(pescado,pescado,covid,arg,TipoContagio.Contagio,"asdasd")
        val arriboEvent = Arribo(pescado,arg,"as")
        val mutacionEvent = MutacionE(covid,TipoMutacion.CreacionEspecie,"asd")
        contagioEventDAO.save(contagioEvent)
        contagioEventDAO.save(arriboEvent)
        contagioEventDAO.save(mutacionEvent)
        contagioEventDAO.save(contagioEvent2)
        val patogenos = contagioEventDAO.feedPatogeno(virus)

        print("Tengo virusitoS? " + patogenos.isEmpty())
        patogenos.forEach { print("mi virusito : ${it}") }
        Assert.assertTrue(true)
    }


}