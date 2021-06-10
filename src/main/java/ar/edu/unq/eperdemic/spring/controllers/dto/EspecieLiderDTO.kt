package ar.edu.unq.eperdemic.spring.controllers.dto


class EspecieLiderDTO(val especie: String?, val patogeno: String?, val cantidadInfectados: Int?, val esPandemia: Boolean?, val nombreDelEquipo: String) {


    companion object {
        fun desdeModelo(especieNombre: String?,
                        patogenoNombre: String?,
                        cantidadDeInfectados: Int?,
                        esPandemia: Boolean?,
                        nombreDelEquipo: String) =
            EspecieLiderDTO(
                especieNombre,
                patogenoNombre,
                cantidadDeInfectados,
                esPandemia,
                nombreDelEquipo
            )
    }

}
