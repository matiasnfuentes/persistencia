package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.*
import ar.edu.unq.eperdemic.persistencia.dao.EventDAO
import ar.edu.unq.eperdemic.services.observer.AlarmaDeEventos
import ar.edu.unq.eperdemic.services.observer.Observador
import com.mongodb.client.model.Filters

class MongoDBEventDAO:GenericMongoDAO<Evento>(Evento::class.java), EventDAO, Observador {

    init {
        AlarmaDeEventos.agregar(this)
    }

    override fun feedPatogeno(patogeno: Patogeno): List<Evento> {
        val res = mutableListOf<Evento>()

        res.addAll(find(Filters.and(Filters.eq("_t", "mutacion"),
                                    Filters.eq("especie.patogeno.tipo", patogeno.tipo))))


        res.addAll(find(Filters.and(Filters.eq("_t", "contagio"),
                                    Filters.eq("especie.patogeno.tipo", patogeno.tipo),
                                    Filters.or(
                                        Filters.eq("subtipo", TipoContagio.Pandemia.name),
                                        Filters.eq("subtipo", TipoContagio.PrimerContagioEnUbicaion.name)
                                    ))))
        res.sortByDescending { it.momento }
        return res
    }

    override fun feedUbicacion(ubicacion: Ubicacion): List<Evento> {
        val res = mutableListOf<Evento>()

        res.addAll(find(Filters.and(Filters.eq("_t", "arribo"),
                                    Filters.eq("ubicacion.id", ubicacion.id))))

        res.addAll(find(Filters.and(Filters.eq("_t", "contagio"),
                                    Filters.eq("ubicacionDeContagio.id", ubicacion.id),
                                    Filters.eq("subtipo", TipoContagio.Contagio.name)
                                    )))

        res.sortByDescending { it.momento }
        return res
    }

    override fun feedVector(vector: Vector): List<Evento> {
        val res = mutableListOf<Evento>()
        res.addAll(find(Filters.and(Filters.eq("_t", "contagio"),
                                    Filters.eq("subtipo", TipoContagio.Contagio.name),
                                    Filters.or(
                                        Filters.eq("transmisor._id", vector.id),
                                        Filters.eq("infectado._id", vector.id)
                                    ))))

        res.addAll(find(Filters.and(Filters.eq("_t", "arribo"),
                                    Filters.eq("vector.id", vector.id))))

        res.sortByDescending { it.momento }
        return res
    }

    override fun actualizar(enfermedad: Especie, vectorAContagiar: Vector, vectorDeContagio: Vector) {
        val contagioEvent = Contagio(vectorDeContagio,vectorAContagiar,enfermedad,
                                     vectorAContagiar.ubicacion,TipoContagio.Contagio,
                                    "El vector ${vectorDeContagio.id} contagio a el vector " +
                                            "${vectorAContagiar.id} con ${enfermedad.nombre}")
        save(contagioEvent)
    }

    override fun actualizar(enfermedad: Especie, ubicacion: Ubicacion, tipoContagio: TipoContagio) {
        var mensaje = ""
        when (tipoContagio) {
            TipoContagio.PrimerContagioEnUbicaion ->
                mensaje = "Primer contagio de ${enfermedad.nombre} en ${ubicacion.nombre}"

            TipoContagio.Pandemia ->
                mensaje = "La enfermedad ${enfermedad.nombre} se volvió pandemia."
        }
        val contagioEvent = Contagio(enfermedad,ubicacion,tipoContagio, mensaje)
        save(contagioEvent)
    }

    override fun actualizar(especie: Especie, vectorContagiado: Vector) {
        val contagioEvent = Contagio(especie,vectorContagiado,vectorContagiado.ubicacion,
            TipoContagio.Contagio, "El vector ${vectorContagiado.id} fue infectado con " +
                    "${especie.nombre}")
        save(contagioEvent)
    }

    override fun actualizar(vector: Vector, ubicacion: Ubicacion) {
        val arriboEvent = Arribo(vector,ubicacion, "El vector ${vector.id} arribo a ${ubicacion.nombre}")
        save(arriboEvent)
    }

    override fun actualizar(especie: Especie) {
        val mutacionEvent = MutacionE(especie,TipoMutacion.CreacionEspecie, "Se creo la especie ${especie.nombre}")
        save(mutacionEvent)
    }

    override fun actualizar(especie: Especie,mutacion: Mutacion) {
        val mutacionEvent = MutacionE(especie,TipoMutacion.Mutacion, "La enfermedad muto con ${mutacion.nombre}")
        save(mutacionEvent)
    }

}