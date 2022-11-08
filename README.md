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
