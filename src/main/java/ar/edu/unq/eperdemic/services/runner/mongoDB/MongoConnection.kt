package ar.edu.unq.eperdemic.services.runner.mongoDB

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCommandException
import com.mongodb.client.*
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

class MongoConnection {

    var client: MongoClient
    var dataBase: MongoDatabase

    init {
        val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build(),
                CodecRegistries.fromCodecs())
        )
        val uri = System.getenv().getOrDefault("MONGO_URI", "mongodb://localhost:27017")
        val database = System.getenv().getOrDefault("MONGO_DB", "epersMongo")
        val connectionString = ConnectionString(uri)
        val settings = MongoClientSettings.builder()
            .codecRegistry(codecRegistry)
            .applyConnectionString(connectionString)
            .build()
        client = MongoClients.create(settings)
        dataBase = client.getDatabase(database)
    }

    fun <T> getCollection(name:String, entityType: Class<T> ): MongoCollection<T> {
        try{
            dataBase.createCollection(name)
        } catch (exception: MongoCommandException){
            println("Ya existe la coleccion $name")
        }
        return dataBase.getCollection(name, entityType)
    }

    fun createSession(): ClientSession {
        return this.client.startSession()
    }

    companion object {

        private var INSTANCE: MongoConnection? = null

        val instance: MongoConnection
            get() {
                if (INSTANCE == null) {
                    INSTANCE = MongoConnection()
                }
                return INSTANCE!!
            }

        fun destroy() {
            if (INSTANCE != null) {
                INSTANCE!!.client.close()
            }
            INSTANCE = null
        }
    }
}