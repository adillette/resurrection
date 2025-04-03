package marryus.studressmake.service;

import lombok.RequiredArgsConstructor;
import marryus.studressmake.entity.member.Member;
import marryus.studressmake.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member join(String membername, String email, String password){
        Member member= new Member();
        member.setMembername(membername);
        member.setEmail(email);

        member.setPassword(passwordEncoder.encode(password));
        this.memberRepository.save(member);
        return member;
    }
}
