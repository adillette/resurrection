package marryus.studressmake.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {
    @GetMapping("/customer/chat")
    public String customerChatPage() {
        return "customer-chat";  // customer-chat.html로 연결
    }

    @GetMapping("/counselor/chat/{counselorId}")
    public String counselorChatPage() {
        return "counselor-chat";  // counselor-chat.html로 연결
    }
}
