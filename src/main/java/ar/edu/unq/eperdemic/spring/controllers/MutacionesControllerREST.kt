package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.MutacionDTO
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/mutaciones")
class MutacionesControllerREST(private val mutacionService: MutacionService) {

    @GetMapping
    fun getAll() = mutacionService.recuperarTodos().map{ MutacionDTO.desdeModelo(it)}

    @PostMapping("/mutar")
    fun mutar(@RequestBody request: MutarRequest ) = mutacionService.mutar(request.especieId, request.mutacionId)

    @PostMapping
    fun crear(@RequestBody mutacionDTO: MutacionDTO) = mutacionService.crear(mutacionDTO.aModelo())

}

class MutarRequest(val mutacionId:Long, val especieId:Long)