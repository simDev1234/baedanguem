package com.example.baedanguem.web;

import com.example.baedanguem.model.Auth;
import com.example.baedanguem.persist.entity.MemberEntity;
import com.example.baedanguem.security.TokenProvider;
import com.example.baedanguem.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        // 회원가입 api
        var result = this.memberService.register(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {

        // 아이디와 비밀번호 일치 확인
        var member = this.memberService.authenticate(request);

        // 토큰 생성 후 반환
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());

        return ResponseEntity.ok(token);
    }

}
