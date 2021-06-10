package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO

class HibernatePatogenoDAO: HibernateDAO<Patogeno>(Patogeno::class.java), PatogenoDAO {

   override fun crear(patogeno: Patogeno): Patogeno {
       this.guardar(patogeno)
       return patogeno
    }

}