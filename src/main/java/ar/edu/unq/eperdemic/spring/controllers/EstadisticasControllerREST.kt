package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieLiderDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.ReporteDeContagiosDTO
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/estadisticas")
class EstadisticasControllerREST(val groupName : String,
                                 val estadisticasService: EstadisticasService,
                                 val ubicacionService: UbicacionService,
                                 val especieService: EspecieService,
                                val patogenoService: PatogenoService ) {

    @GetMapping("/especieLider")
    fun especieLider() = estadisticasService.especieLider()

    @GetMapping("/lideres")
    fun lideres(): List<EspecieLiderDTO> {
        val lideres = estadisticasService.lideres()
        return lideres.map { lider ->
            EspecieLiderDTO.desdeModelo(
                lider.nombre,
                lider.patogeno.tipo,
                especieService.cantidadDeInfectados(lider.id!!),
                especieService.esPandemia(lider.id!!),
                groupName
            )
        }
    }

    @GetMapping("/reporteDeContagios")
    fun reporteDeContagios(): List<ReporteDeContagiosDTO> {
        val ubicaciones = ubicacionService.recuperarTodos()
        return ubicaciones.map{ ReporteDeContagiosDTO.desdeModelo(estadisticasService.reporteDeContagios(it.id!!),
            it.nombre, groupName)}
    }

}