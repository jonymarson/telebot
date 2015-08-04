package org.springframework.samples.mvc.telebot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TelegramCommandController {
	
	@RequestMapping("/tdpbot")
	@ResponseBody
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
