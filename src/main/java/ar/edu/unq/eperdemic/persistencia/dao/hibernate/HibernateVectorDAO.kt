package ar.edu.unq.eperdemic.persistencia.dao.hibernate
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction

class HibernateVectorDAO: HibernateDAO<Vector>(Vector::class.java), VectorDAO {

    override fun crear(vector: Vector): Vector{
        this.guardar(vector)
        return vector
    }

    override fun recuperarVectoresDeUbicacion(ubicacionId: Long): List<Vector>{
        val session = HibernateTransaction.currentSession
        val hql = "select v " +
                "from Vector v " +
                "where v.ubicacion.id = :ubicacionId"
        val query = session.createQuery(hql,Vector::class.java)
        query.setParameter("ubicacionId",ubicacionId)
        return query.resultList
    }

    // Precondición: La condicion de cuenta debe tener un parametro llamado ubicacionId"

    private fun contarVectoresSegunCondicion(ubicacionId: Long, condicionDeCuenta : String): Int{
        val session = HibernateTransaction.currentSession
        val hql = "select count (v)" +
                  "from Vector v " +
                   condicionDeCuenta
        val query = session.createQuery(hql,Long::class.javaObjectType)
        query.setParameter("ubicacionId",ubicacionId)
        return fixResult(query.uniqueResult()).toInt()
    }

    override fun vectoresEnUbicacion(ubicacionId: Long): Int{
        return contarVectoresSegunCondicion(ubicacionId, "where v.ubicacion.id=:ubicacionId")
    }

    override fun vectoresInfectadosEnUbicacion(ubicacionId: Long): Int{
        return contarVectoresSegunCondicion(ubicacionId,
       "where v.ubicacion.id=:ubicacionId and v.especiesPadecidas is not empty ")
    }

}