package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction
import org.hibernate.query.Query

class HibernateEspecieDAO: HibernateDAO<Especie>(Especie::class.java), EspecieDAO {

    override fun crear(especie: Especie): Especie {
        this.guardar(especie)
        return especie
    }

    override fun especiesDelPatogeno(patogenoID: Long): List<Especie> {
        val session = HibernateTransaction.currentSession
        val hql = """from Especie e
                     where e.patogeno.id =:patogenoID"""
        val query = session.createQuery(hql,Especie::class.java)
        query.setParameter("patogenoID", patogenoID)
        return query.resultList
    }

    override fun cantidadDeUbicacionesDeLaEspecie(especieId: Long): Double {
        val session = HibernateTransaction.currentSession
        val hql = """select count (distinct v.ubicacion) 
                     from Vector v
                        join v.especiesPadecidas ep
                     where ep.id=:especieId"""
        val query = session.createQuery(hql,Long::class.javaObjectType)
        query.setParameter("especieId", especieId)
        return fixResult(query.uniqueResult())
    }

    private fun seleccionarEspeciesInfecciosasSegunCondicion(condicion : String): Query<Especie> {
        val session = HibernateTransaction.currentSession
        val hql =  "select e " +
                   "from Vector v " +
                   "join v.especiesPadecidas e " +
                    condicion +
                   "group by e " +
                   "order by count (v) desc"
        return session.createQuery(hql,Especie::class.java)
    }

    override fun especieLider():Especie{
        val query = seleccionarEspeciesInfecciosasSegunCondicion("where v.tipo = :tipo ")
        query.maxResults = 1
        query.setParameter("tipo", TipoDeVector.Persona)
        return query.uniqueResult()
    }

    override fun lideres():List<Especie>{
        val query = seleccionarEspeciesInfecciosasSegunCondicion("where v.tipo = :personas or v.tipo = :animales ")
        query.maxResults = 10
        query.setParameter("personas", TipoDeVector.Persona)
        query.setParameter("animales", TipoDeVector.Animal)
        return query.resultList
    }

    override fun especieMasInfecciosa(ubicacionID: Long): Especie {
        val query = seleccionarEspeciesInfecciosasSegunCondicion("where v.ubicacion.id = :ubicacionID ")
        query.setParameter("ubicacionID", ubicacionID)
        query.maxResults = 1
        return query.uniqueResult()
    }


}