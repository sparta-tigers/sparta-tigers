// package com.sparta.spartatigers.global.config;
//
// import com.sparta.spartatigers.domain.user.dto.AuthResponseDto;
// import com.sparta.spartatigers.domain.user.dto.CodeResDto;
// import com.sparta.spartatigers.domain.user.model.entity.User;
// import com.sparta.spartatigers.domain.user.repository.UserRepository;
// import com.sparta.spartatigers.global.exception.ExceptionCode;
// import com.sparta.spartatigers.global.exception.ServerException;
// import com.sparta.spartatigers.global.util.JwtUtil;
// import lombok.RequiredArgsConstructor;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Component;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.RestTemplate;
//
// import java.util.HashMap;
// import java.util.Map;
//
// @Component
// @RequiredArgsConstructor
// public class SocialLogin {
//        private final JwtUtil jwtUtil;
//        private final UserRepository userRepository;
//
//    String redirectUri = "http://localhost:8080/auth/login/kakao";
//
//
//    public AuthResponseDto getKakaoInfo(String code) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", "73de350af1d87d13f6b9f10b6106fa83");
//        params.add("redirect_uri", redirectUri);
//        params.add("client_secret", "vtKnLmDkxbFmlE4LdHMqLJjga7xFk2AG");
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<Map> response =
// restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, Map.class);
//
//        String accessToken = (String) response.getBody().get("access_token");
//
//        // 사용자 정보 요청
//        HttpHeaders userHeaders = new HttpHeaders();
//        userHeaders.setBearerAuth(accessToken);
//
//        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
//        ResponseEntity<Map> userInfo = restTemplate.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.GET,
//                userRequest,
//                Map.class
//        );
//
//        Map<String, Object> body = userInfo.getBody();
//        Long id = Long.valueOf(String.valueOf(body.get("id")));
//        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
//        String email = (String) kakaoAccount.get("email");
//        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//        String nickname = (String) profile.get("nickname");
//        String path = (String) profile.get("thumbnail_image_url");
//
//        return AuthResponseDto.builder()
//                .id(id)
//                .email(email)
//                .nickname(nickname)
//                .path(path)
//                .build();
//    }
//
//    public CodeResDto login(String code){
//        AuthResponseDto authResponseDto = getKakaoInfo(code);
//        System.out.println(authResponseDto.toString());
//
//        User user = userRepository.findByEmail(authResponseDto.getEmail())
//                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
//
////        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles(Role.USER));
//
//        // 유저가 없으면 회원가입 있으면 로그인 시키면 됨
//
//        return null;
//    }
// }
