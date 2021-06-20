package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoDeCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.ConexionesDAO
import ar.edu.unq.eperdemic.services.runner.neo4j.Neo4jTransaction
import org.neo4j.driver.Record
import org.neo4j.driver.Values
import org.neo4j.driver.exceptions.NoSuchRecordException

class Neo4jConexionesDAO : ConexionesDAO{


    override fun crearUbicacion(ubicacion : Ubicacion){
        val transaction = Neo4jTransaction.currentTransaction
        val query = "MERGE (n:Ubicacion {hibernateID: ${'$'}id, nombre: ${'$'}nombre})"
        transaction.run(query, Values.parameters("nombre", ubicacion.nombre,
                                                              "id", ubicacion.id))
    }

    override fun recuperarUbicacion(hibernateID: Long) : Ubicacion {
        val transaction = Neo4jTransaction.currentTransaction
        val query = """
                MATCH (u:Ubicacion {hibernateID: ${'$'}id}) 
                RETURN u
            """
        val result = transaction.run(query, Values.parameters("id", hibernateID))
        return crearUbicacionDesdeRegistro(result.single())
    }

    override fun capacidadDeExpansion(vector: Vector, movimientos: Int): Int {
        val transaction = Neo4jTransaction.currentTransaction
        val ubicacionID = vector.ubicacion.id
        val tiposCaminos = vector.caminosPermitidos().map { it.name }
        val query = """
            CALL{
                MATCH (a:Ubicacion{hibernateID:${'$'}id})
                MATCH (a)-[:conectadaCon*1..""" + movimientos + """]->(ubicacionVecina)
                WHERE ubicacionVecina.hibernateID <> ${'$'}id
                RETURN DISTINCT ubicacionVecina.hibernateID as ubicacionVecinaID
            }
            MATCH p= ALLSHORTESTPATHS((a:Ubicacion{hibernateID:${'$'}id})-[:conectadaCon*]->(b:Ubicacion{hibernateID:ubicacionVecinaID}))
            WITH ALL (r IN relationships(p) WHERE r.tipoCamino in ${'$'}tiposDeCamino) as esAlcanzable,
            b as ubicacionCandidata
            WHERE esAlcanzable = true
            return count(DISTINCT ubicacionCandidata)"""
        val result = transaction.run(
            query, Values.parameters("id", ubicacionID,
                                                  "tiposDeCamino",tiposCaminos))
        return result.single()[0].asInt()
    }

    // Dado un vector y el id de una ubicacion devuelve una lista con los ids
    // de las ubicaciones que deberia transitar el vector hasta llegar a su
    // ubicacion final, con esta incluida. En caso de que el vector no pueda
    // transitar la ruta, o que la ruta no exista, devuelve una lista vacia.
    // Siempre elige el camino mas corto para llegar al destino

    override fun rutaAUbicacion(vector: Vector, ubicacionid: Long): List<Long> {
        val transaction = Neo4jTransaction.currentTransaction
        val ubicacion = vector.ubicacion.id
        val tiposCaminos = vector.tipo.caminosPermitidos().map { it.name }
        val query =
            """
                MATCH p=allshortestpaths ((a:Ubicacion{hibernateID:${'$'}ubicacion})-[:conectadaCon*]->(b:Ubicacion{hibernateID:${'$'}destino}))
                WITH ALL (r IN relationships(p) WHERE r.tipoCamino in ${'$'}tiposDeCamino) as esAlcanzable,
                REDUCE(ciudades = [], n IN nodes(p) | ciudades + n.hibernateID) AS ids
                ORDER BY esAlcanzable DESC
                LIMIT 1
                RETURN CASE
                      WHEN esAlcanzable THEN ids
                      ELSE []
                END AS result"""
        val result = transaction.run(
            query, Values.parameters("ubicacion", ubicacion,
                "destino",ubicacionid,
                "tiposDeCamino",tiposCaminos))
        return try {
            result.single()[0].asList().map { id -> id as Long }.filter { id -> id!= ubicacion }
        }
        catch(e: NoSuchRecordException){
            listOf()
        }
    }

    override fun conectar(ubicacion1:Long, ubicacion2:Long, tipoCamino: TipoDeCamino){
        val transaction = Neo4jTransaction.currentTransaction
        val query = """
                MATCH (ubicacion1:Ubicacion {hibernateID: ${'$'}ubicacion1ID})
                MATCH (ubicacion2:Ubicacion {hibernateID: ${'$'}ubicacion2ID})
                MERGE (ubicacion1)-[:conectadaCon{tipoCamino:${'$'}tipoCamino}]->(ubicacion2)
            """
        transaction.run(
            query, Values.parameters(
                "ubicacion1ID", ubicacion1,
                "ubicacion2ID", ubicacion2,
                "tipoCamino",tipoCamino.name
            )
        )
    }

    override fun conectados(ubicacionId:Long): List<Ubicacion>{
        val transaction = Neo4jTransaction.currentTransaction
        val query = """
                MATCH (ubicacion:Ubicacion {hibernateID: ${'$'}id }) 
                MATCH (ubicacion)-[:conectadaCon]->(ubicacionConectada)
                RETURN ubicacionConectada
            """
        val result = transaction.run(query, Values.parameters("id", ubicacionId))
        return result.list { record: Record -> crearUbicacionDesdeRegistro(record) }
    }

    private fun crearUbicacionDesdeRegistro(record: Record):Ubicacion{
        val ubicacion = record[0]
        val id = ubicacion["hibernateID"].asLong()
        val nombre = ubicacion["nombre"].asString()
        val ubicacionObtenida = Ubicacion(nombre)
        ubicacionObtenida.id = id
        return ubicacionObtenida
    }

}