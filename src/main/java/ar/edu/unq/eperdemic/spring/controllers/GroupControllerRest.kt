package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.spring.controllers.dto.PatogenoDTO
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@CrossOrigin
@ServiceREST
@RequestMapping("/group")
class GroupControllerREST(private val groupName : String) {
    @GetMapping
    fun getGroup() = GroupData(groupName, 3)

}

class GroupData(val name:String, val version: Int)