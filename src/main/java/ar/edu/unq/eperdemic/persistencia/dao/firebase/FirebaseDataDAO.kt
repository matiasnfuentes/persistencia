package ar.edu.unq.eperdemic.persistencia.dao.firebase

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.firebase.FirebaseConnection
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.QuerySnapshot


class FirebaseDataDAO:DataDAO {

    private val db = FirebaseConnection.dataBase

    override fun clear() {
        val mutacionRef = db.collection("Mutacion")
        val arriboRef = db.collection("Contagio")
        val contagioRef = db.collection("Arribo")
        val referencias = listOf(mutacionRef,arriboRef,contagioRef)
        referencias.forEach { deleteCollection(it, 999) }
    }

    fun deleteCollection(collection: CollectionReference, batchSize: Int) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            val future: ApiFuture<QuerySnapshot> = collection.limit(batchSize).get()
            var deleted = 0
            // future.get() blocks on document retrieval
            val documents = future.get().documents
            for (document in documents) {
                document.reference.delete()
                ++deleted
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize)
            }
        } catch (e: Exception) {
            System.err.println("Error deleting collection : " + e.message)
        }
    }
}