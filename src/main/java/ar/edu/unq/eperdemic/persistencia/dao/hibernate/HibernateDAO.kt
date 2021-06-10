package ar.edu.unq.eperdemic.persistencia.dao.hibernate
import ar.edu.unq.eperdemic.services.runner.hibernate.HibernateTransaction


open class HibernateDAO<T>(private val entityType: Class<T>) {

    fun guardar(item: T) {
        val session = HibernateTransaction.currentSession
        session.save(item)
    }

    fun recuperar(id: Long?): T {
        val session = HibernateTransaction.currentSession
        return session.get(entityType,id)
    }

    fun recuperarATodos():List<T> {
        val session = HibernateTransaction.currentSession
        val builder = session.criteriaBuilder
        val  criteria = builder.createQuery(entityType)
        criteria.from(entityType)
        return session.createQuery(criteria).resultList
    }

    fun actualizar(item: T) {
        val session = HibernateTransaction.currentSession
        session.update(item)
    }

    /**
     * Si el resultado pasado por parametro es null
     * devuelve 0 , en caso contrario devuelve
     * dicho resultado.
     * @param[result] un posible resultado.
     * @return el resultado de la query en formato double.
     */
    fun fixResult(result : Long?):Double{
        var cant : Long = 0
        if (result != null){
            cant = result
        }
        return cant.toDouble()
    }
}