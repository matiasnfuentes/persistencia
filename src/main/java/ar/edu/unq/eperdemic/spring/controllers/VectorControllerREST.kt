package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.spring.controllers.dto.VectorDTO
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/vector")
class VectorControllerREST(private val vectorService: VectorService,
                           val especieService: EspecieService) {

    @PutMapping("/infectar/{vectorId}/{especieId}")
    fun infectar(@PathVariable vectorId: Long, @PathVariable especieId: Long ){
        vectorService.infectar(vectorId, especieId)
    }

    @GetMapping("/enfermedades/{vectorId}")
    fun enfermedades(@PathVariable vectorId: Long ) = vectorService.enfermedades(vectorId)

    @PostMapping
    fun crearVector( @RequestBody vectorFrontendDTO: VectorDTO) = vectorService.crear(vectorFrontendDTO.tipo, vectorFrontendDTO.ubicacionId)

    @GetMapping("/{id}")
    fun recuperarVector(@PathVariable vectorId: Long) = vectorService.recuperar(vectorId)

}