## Entrega 2 - ORM HIBERNATE

## Cambios desde el TP anterior

Se identificaron una serie de cambios necesarios a hacerse sobre la prueba de concepto anterior:
La capa de persistencia deberá cambiarse para utilizar Hibernate/JPA en lugar de JDBC.
**Nota:** No es necesario que mantengan los test y funcionalidad utilizando JDBC.

## Funcionalidad

Nuevamente nos reunimos con el equipo de científicos, los cuales nos informan de varias características que serán necesarias representar en nuestra simulación.

<p align="center">
  <img src="virus.png" />
</p>

Muy profesionalmente, nos llevan a una sala de proyectores en la cual a través de unos slides, profundizan contándonos más sobre los patogenos, las cuales nos interesan por los atributos que las caracterizan.

### Atributos

- Capacidad de contagio, que puede ser por persona, animales o insectos.
- Defensa contra otros micro-organismos
- Letalidad, qué tan peligroso es para el infectado.

Cada uno de estas atributos representa con un valor numérico del 1 al 100.

### Vectores

<p align="center">
  <img src="contagio.png" />
</p>

Luego de las proyecciones y un café de por medio, son dirigidos a un laboratorio.
Aquí, les enseñan que un patógeno se esparce a través de vectores biológicos, que son los agentes que transportan y transmiten un patógeno a otro organismo vivo. Estos pueden ser Humanos, Animales, o Insectos.

### Ubicación

Los vectores pueden moverse de ubicación en ubicación.
Toda ubicación tendrá un nombre que deberá ser único.

### Contagio

Un vector podrá poner en riesgo de contagio a otro vector respetando las siguientes normas:

- Un humano puede ser contagiado por otro humano, un insecto o un animal.
- Un animal solo puede ser contagiado por un insecto.
- Un insecto solo puede ser contagiado por un humano o un animal.

La probabilidad que un contagio de un vector a otro sea exitoso se resolverá de la siguiente forma.
Tiene como base un número entre el 1 y el 10
A Este número se le suma el atributo de contagio de la especie relacionado al vector que se está intentando infectar  
Esto se traduce a:

`porcentajeDeContagioExitoso = (random(1, 10)) + factorContagio`

Con este porcentaje, deberá determinarse si el contagio fue exitoso o no. Si lo fue, el nuevo vector pasa de estar Sano a estar Infectado.

**Nota:** Un vector puede estar infectado por varias especies de patógenos.

## Servicios

Se pide que implementen los siguientes servicios los cuales serán consumidos por el frontend de la aplicación.


### VectorService

- `Crear, Recuperar y Recuperar todos`

- `infectar(vectorId: Long, especieId: Long)`  Se infecta al vector con la especie

- `enfermedades(vectorId: Long): List<Especie> `  Dado un vector retorna todas las especies que esta padeciendo.


### UbicacionService

- `Crear, Recuperar y Recuperar todos`

- `mover(vectorId: Long, ubicacionId: Long)` Mueve un vector de la ubicación en la que se encontraba a una nueva. Si el vector está infectado, intentara contagiar a todos los vectores presentes en la nueva locación. **Nota:** Leer VectorService.

- `expandir( ubicacionId: Long)` Dada una ubicación, deberá tomar un vector contagiado **elegido al azar**. Ese vector debe intentar contagiar a todos los otros vectores presentes en el mismo lugar. De no haber ningún vector contagiado en el lugar, no hace nada. **Nota:** Leer VectorService


### PatogenoService

- `Crear, Recuperar y Recuperar todos`

- `agregarEspecie(id: Long, nombreEspecie: String, ubicacionId: Long) : Especie` - Deberá lograr que se genere una nueva Especie del Patogeno.

- `especiesDePatogeno(patogenoId: Long ): List<Especie>` devuelve las especies del patogeno

### EspecieService

- `Recuperar y Recuperar todos`

- `cantidadDeInfectados(especieId: Long ): Int` devuelve la cantidad de vectores infectados por la especie


### Se pide:

- Que provean implementaciónes para las interfaces descriptas anteriormente.

- Que modifiquen el mecanismo de persistencia de Patógenode forma de que todo el modelo persistente utilice Hibernate.

- Asignen propiamente las responsabilidades a todos los objetos intervinientes, discriminando entre servicios, DAOs y objetos de negocio.

- Provean la implementación al mensaje "desdeModelo" y "aModelo" de `EspecieDTO`, `PatogenoDTO` y `UbicacionDTO`

- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.

- Que los tests sean determinísticos. Hay mucha lógica que depende del resultado de un valor aleatorio. Se aconseja no utilizar directamente generadores de valores aleatorios (random) sino introducir una interfaz en el medio para la cual puedan proveer una implementación mock determinística en los tests.

### Recuerden que:
- No pueden modificar las interfaces ya provistas en el TP, solo implementarlas.
- Pueden agregar nuevos métodos y atributos a los objetos ya provistos, pero no eliminar o renombrar atributos / métodos ya provistos.

### Consejos útiles:

- Finalicen los métodos de los services de uno en uno. Que quiere decir esto? Elijan un service, tomen el método más sencillo que vean en ese service, y encárguense de desarrollar la capa de modelo, de servicios y persistencia solo para ese único método. Una vez finalizado (esto también significa testeado), pasen al próximo método y repitan.

- Cuando tengan que persistir con hibernate, analicen:
  Qué objetos deben ser persistentes y cuáles no?
  Cuál es la cardinalidad de cada una de las relaciones? Como mapearlas?