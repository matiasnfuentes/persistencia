package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.spring.controllers.dto.UbicacionDTO
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/ubicacion")
class UbicacionControllerREST(private val ubicacionService: UbicacionService, val vectorService: VectorService) {

    @GetMapping
    fun getAll() = ubicacionService.recuperarTodos().map{ UbicacionDTO.desdeModelo(it)}

    @PutMapping("/{vectorId}/{ubicacionId}")
    fun mover(@PathVariable vectorId: Long, @PathVariable ubicacionId: Long ) = ubicacionService.mover(vectorId, ubicacionId)

    @PutMapping("/expandir/{ubicacionId}")
    fun expandir(@PathVariable ubicacionId: Long) = ubicacionService.expandir(ubicacionId)

    @PutMapping("/expandir")
    fun expandir(): Number {
        val ubications = ubicacionService.recuperarTodos()
        ubications.forEach { ubicacion ->
            ubicacionService.expandir(ubicacion.id!!)
        }
        val vector = vectorService.recuperarTodos().random()
        val ubicacion = ubications.random()
        ubicacionService.mover(vector.id!!, ubicacion.id!!)
        return 200
    }

    @PostMapping
    fun crearUbicacion(@RequestBody ubicacionDTO: UbicacionDTO) = ubicacionService.crear(ubicacionDTO.nombre)

}