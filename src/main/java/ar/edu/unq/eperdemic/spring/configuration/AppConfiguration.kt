package ar.edu.unq.eperdemic.spring.configuration

import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.MongoDBEventDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jConexionesDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        return groupName!!
    }

    @Bean
    fun conexionesDAO(): ConexionesDAO {
        return Neo4jConexionesDAO()
    }

    @Bean
    fun eventDAO(): MongoDBEventDAO {
        return MongoDBEventDAO()
    }

    @Bean
    fun patogenoDAO(): PatogenoDAO {
        return HibernatePatogenoDAO()
    }

    @Bean
    fun vectorDAO(): VectorDAO {
        return HibernateVectorDAO()
    }

    @Bean
    fun mutacionDAO(): MutacionDAO {
        return HibernateMutacionDAO()
    }

    @Bean
    fun estadisticasServices(especieDAO: EspecieDAO,vectorDAO: VectorDAO): EstadisticasService {
        return EstadisticasServiceImpl(especieDAO,vectorDAO)
    }

    @Bean
    fun mutacionService(mutacionDao: MutacionDAO, especieDAO: EspecieDAO): MutacionService {
        return MutacionServiceImpl(mutacionDao,especieDAO, AlarmaDeEventos)
    }

    @Bean
    fun ubicacionDAO(): UbicacionDAO {
        return HibernateUbicacionDAO()
    }

    @Bean
    fun especieDAO(): EspecieDAO {
        return HibernateEspecieDAO()
    }

    @Bean
    fun especieService(especieDAO: EspecieDAO, ubicacionDAO: UbicacionDAO, vectorDAO: VectorDAO): EspecieService {
        return EspecieServiceImpl(especieDAO, ubicacionDAO)
    }

    @Bean
    fun patogenoService(patogenoDAO: PatogenoDAO,
                        especieDAO: EspecieDAO,
                        ubicacionDAO: UbicacionDAO,
                        eventDAO: MongoDBEventDAO): PatogenoService {
        return PatogenoServiceImpl(patogenoDAO,especieDAO,ubicacionDAO,AlarmaDeEventos)
    }

    @Bean
    fun ubicacionService(ubicacionDAO: UbicacionDAO,vectorDAO: VectorDAO , conexionesDAO: ConexionesDAO, especieDAO: EspecieDAO, especieService: EspecieService): UbicacionService {
        return UbicacionServiceImpl(ubicacionDAO,vectorDAO,conexionesDAO,especieDAO,especieService,AlarmaDeEventos)
    }

    @Bean
    fun vectorService(vectorDAO : VectorDAO,especieDAO: EspecieDAO, ubicacionDAO: UbicacionDAO, especieService: EspecieService): VectorService {
        return VectorServiceImpl(vectorDAO,especieDAO,ubicacionDAO,especieService,AlarmaDeEventos)
    }

}
