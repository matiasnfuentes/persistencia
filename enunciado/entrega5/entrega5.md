## Entrega 5 - MongoDB

En un esfuerzo por entender más fácilmente cómo afecta la simulación a sus distintos componentes, los del laboratorio les piden como nuevo requerimiento introducir un log de eventos recientes, en el cuál se podrá ver y analizar el comportamiento en orden cronológico, tarea que facilitara el análisis de datos.


<p align="center">
  <img src="eventos.jpg" />
</p>

# Eventos

Por ahora se han identificado los siguientes eventos:

- Arribo: un vector arriba a una ubicación.
- Contagio: Un vector es contagiado.
- mutación: Un Patógeno muta.

## Servicios

Se debera implementar un nuevo servicio llamado `FeedService` que implemente los siguientes mensajes:

Nota: Todas estas listas deberán contener primero a los eventos más recientes.

- El mensaje `feedPatogeno(tipoDePatogeno)` que devolverá la lista de eventos que involucren al patógeno previsto.

    - Cada vez que se crea una nueva especie para el patógeno (Mutacion)
    - Cada vez que se muta una especie del patógeno dado (Mutacion)
    - Si una especie del patógeno llega a convertirse en pandemia (Contagio)
    - Cada vez que una especie del patógeno  contagia un vector en una locación en la cual esa especie no estaba previamente presente (Contagio)


- El mensaje `feedVector(vectorId: Long)` que devolverá la lista de
  eventos que involucren al vector provisto. Esa lista incluirá eventos
  relacionados a
    - Todos los viajes que haya hecho y a que locación (Arribo)
    - Las enfermedades que padece (Contagio)
    - Contagios a otros vectores (Contagio)


- El mensaje `feedUbicacion(ubicacionId: Long)` que devolverá la lista de eventos que involucren la ubicacion prevista.
  El mismo deberá incluir todas los eventos de la ubicación provista y todos los eventos de las ubicaciones que estén conectadas con ella.
    - Todos los viajes que se hayan hecho a la locación (Arribo)
    - Cada vez que alguien fue contagiado en la locación (Contagio)


## Se pide:
- El objetivo de esta entrega es implementar los requerimientos utilizando una
  base de datos documental.
- Creen test unitarios para cada unidad de código entregada que prueben todas las
  funcionalidades pedidas, con casos favorables y desfavorables.

### Consejos utiles:
- Traten de no tocar los objetos de modelo previamente definidos, solo van a
  necesitar agregar las llamadas para crear nuevos eventos en los servicios del
  TP2.