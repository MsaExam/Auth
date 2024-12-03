package com.sparta.msa_exam.auth.service;

import com.sparta.msa_exam.auth.dto.SignInRequestDto;
import com.sparta.msa_exam.auth.dto.SignUpRequestDto;
import com.sparta.msa_exam.auth.dto.TokenResponseDto;
import com.sparta.msa_exam.auth.entity.Member;
import com.sparta.msa_exam.auth.jwt.JwtProperties;
import com.sparta.msa_exam.auth.jwt.JwtProvider;
import com.sparta.msa_exam.auth.repository.MemberRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public TokenResponseDto signIn(SignInRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return TokenResponseDto.builder()
                .accessToken(
                        jwtProvider.generateToken(
                                Duration.ofSeconds(jwtProperties.getExpiration()),
                                member.getId(),
                                member.getRole()))
                .build();

    }

    @Transactional
    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        Member member = memberRepository.save(requestDto.toEntity(passwordEncoder));
        return TokenResponseDto.builder()
                .accessToken(
                        jwtProvider.generateToken(
                                Duration.ofSeconds(jwtProperties.getExpiration()),
                                member.getId(),
                                member.getRole()))
                .build();
    }
}
