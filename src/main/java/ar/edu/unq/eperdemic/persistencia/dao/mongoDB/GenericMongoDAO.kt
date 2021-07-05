package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.services.runner.mongoDB.MongoDBTransaction
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.conversions.Bson

open class GenericMongoDAO<T>(entityType: Class<T>) {

    protected var collection:MongoCollection<T>

    init {
        collection = MongoDBTransaction.getCollection(entityType.simpleName, entityType)
    }

    fun deleteAll() {
        val session = MongoDBTransaction.currentSession
        collection.drop(session)
    }

    fun save(anObject: T) {
        save(listOf(anObject))
    }

    fun update(anObject: T, id: String?) {
        val session = MongoDBTransaction.currentSession
        collection.replaceOne(session,eq("id", id), anObject)
    }

    fun save(objects: List<T>) {
        val session = MongoDBTransaction.currentSession
        collection.insertMany(session,objects)
    }

    operator fun get(id: String?): T? {
        return getBy("id", id)
    }

    fun getBy(property:String, value: String?): T? {
        val session = MongoDBTransaction.currentSession
        return collection.find(session,eq(property, value)).first()
    }

    fun <E> findEq(field:String, value:E ): List<T> {
        return find(eq(field, value))
    }

    fun find(filter:Bson): List<T> {
        val session = MongoDBTransaction.currentSession
        return collection.find(session,filter).into(mutableListOf())
    }

    fun <T> aggregate(pipeline:List<Bson> , resultClass:Class<T>): List<T> {
        val session = MongoDBTransaction.currentSession
        return collection.aggregate(session,pipeline, resultClass).into(ArrayList())
    }


}