# demo-spring-security
[SpringBoot] Spring Security 활용 로그인/회원가입 demo 프로젝트

---

<h3>- 로그인</h3>
(Http Method: POST, EndPoint: "/login")

1. Request Parameter
  : loginId(로그인 아이디), loginPassword(로그인 비밀번호)

2. Response Body
  : accessToken (String), refreshToken (String), accessTokenExpireDate (Date)
  
3. 설명
  - 로그인 아이디와 로그인 비밀번호를 서버에 전달 시, 먼저 로그인 아이디가 있는지 확인한다.
  - 이후 AuthenticationManager를 통해 회원 아이디와 비밀번호를 파라미터로 넘겨 UsernamePasswordAuthenticationToken를 생성한다.<br/>
    -> authenticate 메소드가 실행이 될 때, CustomUserDetailsService의 loadUsersByUsername가 실행됨<br/>
    -> 이때 회원 저장소에 저장된 회원 id의 비밀번호와 입력 받은 값인 비밀번호가 다른 값이면 오류를 발생한다.
  - 에러 발생 없이 authentication이 반환되었다면 SecurityContext에 저장을 한다.
  - 이후 jwt 토큰 객체(TokenDto)를 생성한다.(access token, refresh token, access token 만료 기한)
  - 생성된 refresh token 값을 refresh token 저장소인 RefreshToken 테이블에 저장한다.<br/>
    (이미 해당 회원의 refresh token이 있으면 저장된 값을 생성된 값으로 변경)
  - 토큰을 반환한다.
 
<br/> 
<h3>- 회원가입</h3>
(Http Method: POST, EndPoint: "/signup")

1. Request Parameter
  : id(로그인 아이디), password(로그인 비밀번호), name(회원 이름)

2. Response Body
  없음

3. 설명
- 전달받은 로그인 아이디를 갖는 회원이 있다면 오류 발생
- 그렇지 않다면 회원 객체를 생성하여 Member 테이블에 추가 (기본 권한: USER)

<br/> 
<h3>- 추가 설명</h3>

1. PasswordEncoder
: BCryptPasswordEncoder을 통해 단방향 해시 알고리즘으로 구현됨

2. JWT
- access token 만료 시간: 30분
- refresh token 만료 시간: 7일
- [과정]<br/>
  : access token은 모든 요청의 header 중 Authorization에 담아 "Bearer <토큰>"의 형식으로 보냄<br/>
  : access token의 유효 기간 만료 시에,,<br/>
    -> "/token/reissue"로 accessToken과 refreshToken을 담아 토큰 재생성을 요청해야 한다.<br/>
    -> 만약 refreshToken의 만료 기간 이전임과 동시에 유효한 토큰이라면 access token과 refresh token을 재발급한다.<br/>
    -> 재발급된 토큰을 이용해 이후의 요청을 수행하면 된다.
