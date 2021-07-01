package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.services.runner.mongoDB.MongoConnection
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.conversions.Bson

    open class GenericMongoDAO<T>(entityType: Class<T>) {

        protected var connection: MongoConnection = MongoConnection()
        protected var collection:MongoCollection<T>

        init {
            collection = connection.getCollection(entityType.simpleName, entityType)
        }

    fun deleteAll() {
        collection.drop()
    }

    fun save(anObject: T) {
        save(listOf(anObject))
    }

    fun update(anObject: T, id: String?) {
        collection.replaceOne(eq("id", id), anObject)
    }

    fun save(objects: List<T>) {
        collection.insertMany(objects)
    }

    operator fun get(id: String?): T? {
        return getBy("id", id)
    }

    fun getBy(property:String, value: String?): T? {
        return collection.find(eq(property, value)).first()
    }

    fun <E> findEq(field:String, value:E ): List<T> {
        return find(eq(field, value))
    }

    fun find(filter:Bson): List<T> {
        return collection.find(filter).into(mutableListOf())
    }

    fun <T> aggregate(pipeline:List<Bson> , resultClass:Class<T>): List<T> {
        return collection.aggregate(pipeline, resultClass).into(ArrayList())
    }


}