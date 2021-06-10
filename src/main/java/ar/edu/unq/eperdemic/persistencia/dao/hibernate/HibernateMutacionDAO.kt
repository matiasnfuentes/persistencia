package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO

class HibernateMutacionDAO: HibernateDAO<Mutacion>(Mutacion::class.java), MutacionDAO {

    override fun crear(mutacion: Mutacion): Mutacion {
        this.guardar(mutacion)
        return mutacion
    }

}