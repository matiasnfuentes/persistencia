package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception


class PatogenoServiceTest: ServiceTest(HibernateDataDAO()) {

    @Test
    fun creo_un_patogeno_y_se_persiste_entonces_lo_puedo_recuperar() {
        patogenoService.crear(virus)
        val patogenoRecuperado = patogenoService.recuperar(virus.id!!)
        Assert.assertEquals(virus.id, patogenoRecuperado.id)
    }

    @Test
    fun no_puedo_agregar_patogenos_con_tipos_repetidos() {
        assertThrows<Exception> {
            patogenoService.crear(virus) // tipo = "virus"
            patogenoService.crear(virus2) // tipo = "virus"
        }
    }

    @Test
    fun no_puedo_recuperar_un_patogeno_si_el_id_no_existe_en_una_db_con_solo_dos_patogenos(){
        patogenoService.crear(virus)
        patogenoService.crear(bacteria)
        Assert.assertNull(patogenoService.recuperar(bacteria.id!!+1))
    }

    @Test
    fun puedo_recuperar_todos_los_elementos_de_una_db_con_dos_patogenos() {
        patogenoService.crear(virus)
        patogenoService.crear(bacteria)
        val patogenosRecuperados = patogenoService.recuperarTodos()
        Assert.assertEquals(2, patogenosRecuperados.size)
        Assert.assertEquals("Virus", patogenosRecuperados[0].tipo)
        Assert.assertEquals("Bacteria", patogenosRecuperados[1].tipo)
    }

    @Test
    fun trato_de_recuperarTodos_en_una_db_vacia_y_devuelve_una_lista_vacia() {
        val patogenosRecuperados = patogenoService.recuperarTodos()
        Assert.assertEquals(0, patogenosRecuperados.size)
    }
    @Test
    fun creo_el_patogeno_virus_lo_guardo_y_luego_aumento_la_cant_de_especies_y_actualizo() {
        val argentina = ubicacionService.crear("Argentina")

        patogenoService.crear(virus)
        patogenoService.agregarEspecie(virus.id!!,"Covid",argentina.id!!)
        patogenoService.agregarEspecie(virus.id!!,"Covid Brasilero",argentina.id!!)
        patogenoService.agregarEspecie(virus.id!!,"Covid Hawaiano",argentina.id!!)

        val patogenoRecuperado = patogenoService.recuperar(virus.id!!)
        Assert.assertEquals(virus.tipo, patogenoRecuperado.tipo)
        Assert.assertEquals(3, patogenoRecuperado.cantidadDeEspecies)

    }

    @Test
    fun trato_de_crear_una_especie_de_un_patogeno_que_no_existe_y_no_se_actualiza() {
        val argentina = ubicacionService.crear("Argentina")
        assertThrows<Exception> {
            patogenoService.agregarEspecie(1,"Covid",argentina.id!!)
        }
    }

    @Test
    fun creo_el_patogeno_virus_le_agrego_mas_especies_y_las_recupero() {
        val argentina = ubicacionService.crear("Argentina")

        patogenoService.crear(virus)
        patogenoService.crear(bacteria)
        patogenoService.agregarEspecie(virus.id!!,"Covid",argentina.id!!)
        patogenoService.agregarEspecie(virus.id!!,"Covid Brasilero",argentina.id!!)
        patogenoService.agregarEspecie(virus.id!!,"Covid Hawaiano",argentina.id!!)
        patogenoService.agregarEspecie(bacteria.id!!,"Covid de virus 2",argentina.id!!)

        val especiesRecuperadas = patogenoService.especiesDePatogeno(virus.id!!)
        Assert.assertEquals(3, especiesRecuperadas.size)
    }

}