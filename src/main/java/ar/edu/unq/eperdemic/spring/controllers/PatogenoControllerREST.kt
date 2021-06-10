package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.PatogenoDTO
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/patogeno")
class PatogenoControllerREST(private val patogenoService: PatogenoService) {

  @PostMapping
  fun create(@RequestBody patogeno: Patogeno): PatogenoDTO {
    val patogeno = patogenoService.crear(patogeno)
    return PatogenoDTO.desdeModelo(patogeno)
  }

  @PostMapping("/{id}/especie")
  fun agregarEspecie(@PathVariable id: Long, @RequestBody agregarEspecieRequest: AgregarEspecieRequest): EspecieDTO {
    val especie = patogenoService.agregarEspecie(id, agregarEspecieRequest.nombre, agregarEspecieRequest.paisDeOrigen)
    return EspecieDTO.desdeModelo(especie)
  }

  @GetMapping("/{patogenoId}/especies")
  fun especies(@PathVariable patogenoId: Int) = patogenoService.especiesDePatogeno(patogenoId.toLong()).map { EspecieDTO.desdeModelo(it) }


  @GetMapping("/{id}")
  fun findById(@PathVariable id: Long) = PatogenoDTO.desdeModelo(patogenoService.recuperar(id))

  @GetMapping
  fun getAll() = patogenoService.recuperarTodos().map{ PatogenoDTO.desdeModelo(it)}

}

class AgregarEspecieRequest (val nombre : String,
                  val patogeno: Long,
                  val paisDeOrigen: Long
)