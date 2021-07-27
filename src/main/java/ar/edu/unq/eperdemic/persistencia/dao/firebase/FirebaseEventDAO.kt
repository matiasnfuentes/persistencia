package ar.edu.unq.eperdemic.persistencia.dao.firebase

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.eventos.*
import ar.edu.unq.eperdemic.persistencia.dao.EventDAO
import ar.edu.unq.eperdemic.services.observer.Observador
import ar.edu.unq.eperdemic.services.runner.firebase.FirebaseConnection
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification


class FirebaseEventDAO : EventDAO, Observador {

    private val db = FirebaseConnection.dataBase

    override fun feedPatogeno(patogeno: Patogeno): List<Evento> {

        val res = mutableListOf<Evento>()

        val mutacionesQuery = db.collection("Mutacion")
              .whereEqualTo("especie.patogeno.id", patogeno.id)
              .get()

        generateObjects(mutacionesQuery.get().documents,MutacionE::class.java,res)

        val contagiosQuery = db.collection("Contagio")
                   .whereEqualTo("especie.patogeno.tipo", patogeno.tipo)
                   .whereIn("subtipo", mutableListOf("Pandemia","PrimerContagioEnUbicacion"))
                   .get()

        generateObjects(contagiosQuery.get().documents,Contagio::class.java,res)

        res.sortByDescending { it.momento }
        return res
    }

    override fun feedUbicacion(ubicacion: Ubicacion): List<Evento> {
        val res = mutableListOf<Evento>()

        val arribosQuery = db.collection("Arribo")
                             .whereEqualTo("ubicacion.id", ubicacion.id)
                             .get()

        generateObjects(arribosQuery.get().documents,Arribo::class.java,res)

        val contagiosQuery = db.collection("Contagio")
                               .whereEqualTo("ubicacion.id", ubicacion.id)
                               .whereEqualTo("subtipo", "Contagio")
                               .get()

        generateObjects(contagiosQuery.get().documents,Contagio::class.java,res)

        res.sortByDescending { it.momento }
        return res
    }

    override fun feedVector(vector: Vector): List<Evento> {

        val res = mutableListOf<Evento>()

        val infeccionesQuery = db.collection("Contagio")
            .whereEqualTo("subtipo", TipoContagio.Contagio.name)
            .whereEqualTo("transmisor.id", vector.id)
            .get()

        generateObjects(infeccionesQuery.get().documents,Contagio::class.java,res)

        val contagiosQuery = db.collection("Contagio")
            .whereEqualTo("subtipo", TipoContagio.Contagio.name)
            .whereEqualTo("infectado.id", vector.id)
            .get()

        generateObjects(contagiosQuery.get().documents,Contagio::class.java,res)

        val arribosQuery = db.collection("Arribo")
            .whereEqualTo("vector.id", vector.id)
            .get()

        generateObjects(arribosQuery.get().documents,Arribo::class.java,res)

        res.sortByDescending { it.momento }
        return res
    }

    fun generateObjects(documents:List<QueryDocumentSnapshot>, clase: Class<*>, result:MutableList<Evento>){
        documents.forEach {
            val event = it.toObject(clase) as Evento
            result.add(event)
        }
    }

    override fun actualizar(enfermedad: Especie, vectorAContagiar: Vector, vectorDeContagio: Vector) {

        val message = "El vector ${vectorDeContagio.id} contagio a el vector " +
                "${vectorAContagiar.id} con ${enfermedad.nombre}"

        val contagioEvent = Contagio(vectorDeContagio,vectorAContagiar,enfermedad,
            vectorAContagiar.ubicacion,TipoContagio.Contagio,message )

        db.collection("Contagio").add(contagioEvent)

        sendNotificacion(message, "Contagio")
    }

    override fun actualizar(enfermedad: Especie, ubicacion: Ubicacion, tipoContagio: TipoContagio) {
        val message: String

        when (tipoContagio) {
            TipoContagio.PrimerContagioEnUbicacion ->
                message = "Primer contagio de ${enfermedad.nombre} en ${ubicacion.nombre}"

            else ->
                message = "La enfermedad ${enfermedad.nombre} se volvió pandemia."
        }
        val contagioEvent = Contagio(enfermedad,ubicacion,tipoContagio, message)
        db.collection("Contagio").add(contagioEvent)
        sendNotificacion(message, "Contagio")
    }


    override fun actualizar(especie: Especie, vectorContagiado: Vector) {
        val message = "El vector ${vectorContagiado.id} fue infectado con " + especie.nombre
        val contagioEvent = Contagio(especie,vectorContagiado,vectorContagiado.ubicacion,
            TipoContagio.Contagio, message)
        db.collection("Contagio").add(contagioEvent)
        sendNotificacion(message, "Contagio")
    }

    override fun actualizar(especie: Especie) {
        val message = "Se creo la especie ${especie.nombre}"
        val mutacionEvent = MutacionE(especie,TipoMutacion.CreacionEspecie, "Se creo la especie ${especie.nombre}")
        db.collection("Mutacion").add(mutacionEvent)
        sendNotificacion(message, "Mutacion")
    }

    override fun actualizar(especie: Especie, mutacion: Mutacion) {
        val message = "La enfermedad muto con ${mutacion.nombre}"
        val mutacionEvent = MutacionE(especie,TipoMutacion.Mutacion, message)
        db.collection("Mutacion").add(mutacionEvent)
        sendNotificacion(message, "Mutacion")
    }

    override fun actualizar(vector: Vector, ubicacion: Ubicacion) {
        val message = "El vector ${vector.id} arribo a ${ubicacion.nombre}"
        val arriboEvent = Arribo(vector,ubicacion, message)
        db.collection("Arribo").add(arriboEvent)
        sendNotificacion(message, "Arribo")
    }

    private fun sendNotificacion(body: String, title: String){
        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("Se produjo un nuevo evento de $title")
                    .setBody(body)
                    .build()
            )
            .setTopic("Pandemia")
            .build()

        FirebaseMessaging.getInstance().send(message)
    }



}