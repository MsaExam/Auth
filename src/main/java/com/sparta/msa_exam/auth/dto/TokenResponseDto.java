package com.sparta.msa_exam.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponseDto {

    private String accessToken;

    @Builder
    private TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
