package io.template.app.api.controller.v1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1")
class ApiController {

    @GetMapping("endpoint/{id}")
    fun getEndpoint(@PathVariable id: Long): String {
        return "Successful"
    }
}
