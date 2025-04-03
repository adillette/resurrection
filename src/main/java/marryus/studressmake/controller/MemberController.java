package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import marryus.studressmake.entity.member.JoinForm;
import marryus.studressmake.service.MemberService;
import org.hibernate.mapping.Join;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(JoinForm joinForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid JoinForm joinForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "signup_form";
        }
        if(!joinForm.getPassword1().equals(joinForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordIncorrect",
                    "2개의 비밀번호가 일치 하지않습니다.");
            return "signup_form";
        }
        memberService.join(joinForm.getMembername(), joinForm.getEmail(), joinForm.getPassword1());
        return "redirect:/";

    }
    @GetMapping("/login")
    public String login(){
        return "login_form";
    }
}
