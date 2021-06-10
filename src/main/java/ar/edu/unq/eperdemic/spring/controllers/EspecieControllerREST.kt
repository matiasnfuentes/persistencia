package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/especie")
class EspecieControllerREST(private val especieService: EspecieService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) = EspecieDTO.desdeModelo(especieService.recuperar(id))

    @GetMapping("/infectados/{id}")
    fun getCantidadInfectados(@PathVariable id: Long) = especieService.cantidadDeInfectados(id)

    @GetMapping("/esPandemia/{id}")
    fun esPandemia(@PathVariable id: Long) = especieService.esPandemia(id)

}