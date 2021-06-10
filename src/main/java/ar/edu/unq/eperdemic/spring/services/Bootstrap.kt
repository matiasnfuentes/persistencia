package ar.edu.unq.eperdemic.spring.services

import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.modelo.Atributo
import ar.edu.unq.eperdemic.spring.controllers.dto.MutacionDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.PatogenoDTO
import ar.edu.unq.eperdemic.spring.services.data.nombreDeUbicaciones
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import kotlin.random.Random


@Service
class Bootstrap (val ubicacionService: UbicacionService, val vectorService: VectorService, val patogenoService: PatogenoService,
                val mutacionService: MutacionService) {

    @EventListener
    fun appready(event: ApplicationReadyEvent) {
        if (noDataLoaded()) {
            loadData()
        }
    }

    private fun noDataLoaded(): Boolean {
        return ubicacionService.recuperarTodos().isEmpty()
    }

    private fun loadData() {
        nombreDeUbicaciones.shuffle()
        val subList = nombreDeUbicaciones.subList(0, 10)
        val ubicacionesAUsar = subList.map { nombreDeUbicacion ->  ubicacionService.crear(nombreDeUbicacion)}
        ubicacionesAUsar.forEach { ubicacion ->
            agregarVectores(ubicacion)
        }
        agregarPatogenos()
        agregarMutaciones()
    }

    private fun agregarVectores(ubicacion: Ubicacion) {
        for (i in 1..Random.nextInt(10, 500)) {
            vectorService.crear(tipoDeVectorRandom(), ubicacion.id!!)
        }
    }

    private fun agregarPatogenos() {
        val names = listOf("Virus Necroa", "Gripe Simia", "Hongo", "Virus", "Bacteria", "Bio-Arma", "Larva Neurax")
        names.forEach{
            val patogeno = PatogenoDTO(null, it, 0,
                Random.nextInt(0, 100),
                Random.nextInt(0, 100),
                Random.nextInt(0, 100),
                Random.nextInt(0, 100),
                Random.nextInt(0, 100))
            patogenoService.crear(patogeno.aModelo())
        }
    }

    private fun agregarMutaciones() {
        val names = listOf("Náuseas", "Tos", "Erupción", "Insomnio", "Quistes", "Anemia",
            "Convulsiones", "Locura", "Necrosis", "Parálisis", "Coma", "Inmunosupresión")
        names.forEach{
            val mutacion = MutacionDTO(null, it,
                Random.nextInt(1, 10),
                Atributo.values().random(),
                Random.nextInt(1, 20))
            mutacionService.crear(mutacion.aModelo())
        }
    }

    private val tipoDeVectores = TipoDeVector.values()
    private val tipoDeVectorSize = tipoDeVectores.size
    private fun tipoDeVectorRandom() = tipoDeVectores.get(Random.nextInt(tipoDeVectorSize))


}