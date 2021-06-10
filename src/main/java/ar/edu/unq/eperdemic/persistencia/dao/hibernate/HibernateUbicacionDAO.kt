package ar.edu.unq.eperdemic.persistencia.dao.hibernate
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction

class HibernateUbicacionDAO: HibernateDAO<Ubicacion>(Ubicacion::class.java), UbicacionDAO{

    override fun crear(nombreUbicacion: String): Ubicacion{
        val ubicacion = Ubicacion(nombreUbicacion)
        this.guardar(ubicacion)
        return ubicacion
    }

    override fun cantidadDeUbicaciones(): Double {
        val session = HibernateTransaction.currentSession
        val hql = """select count(u) 
                     from Ubicacion u"""

        val query = session.createQuery(hql,Long::class.javaObjectType)
        return fixResult(query.uniqueResult())
    }

}
