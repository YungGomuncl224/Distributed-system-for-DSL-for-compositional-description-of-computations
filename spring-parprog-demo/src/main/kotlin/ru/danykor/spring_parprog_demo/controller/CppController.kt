package ru.danykor.spring_parprog_demo.controller
import org.springframework.web.bind.annotation.*
import ru.danykor.spring_parprog_demo.service.CppService
import ru.danykor.spring_parprog_demo.service.MessageService

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api/messages/cpp")
class CppController(
    private val cppService: CppService
) {

    @PostMapping
    fun sendMessage(@RequestBody message: String): String? {
        return cppService.sendMessage(message)
    }
}