package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Mutacion(val nombre:String,
               val adnRequerido:Int,
               @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
               val requerimientos: List<Mutacion>,
               @Enumerated(value = EnumType.STRING)
               val atributo : Atributo,
               val valor : Int) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}

enum class Atributo {
    LETALIDAD,
    DEFENSA ,
    FACTOR_ANIMAL,
    FACTOR_INSECTO,
    FACTOR_HUMANO;
}