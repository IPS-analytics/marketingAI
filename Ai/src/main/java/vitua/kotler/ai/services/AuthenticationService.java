package vitua.kotler.ai.services;

import vitua.kotler.ai.dtos.JwtAuthenticationResponse;
import vitua.kotler.ai.dtos.RefreshRequestDto;
import vitua.kotler.ai.dtos.SignInRequestDto;
import vitua.kotler.ai.dtos.SignUpRequestDto;

public interface AuthenticationService {
    JwtAuthenticationResponse signUp(SignUpRequestDto request);

    JwtAuthenticationResponse signIn(SignInRequestDto request);

    JwtAuthenticationResponse refresh(RefreshRequestDto request);
}
