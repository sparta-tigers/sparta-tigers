# ⚾️ 야구대학 (Baseball UNIV)

> **“야구는 혼자 보기엔 너무 아쉬운 스포츠입니다.”**

야구대학은 야구 팬들이 **경기를 중심으로 소통하고 교류하며, 기록을 남길 수 있는 플랫폼**입니다.  
혼자 직관하거나, 혼자 집에서 응원하는 팬들이 **실시간으로 연결되고**, **직관/굿즈를 교환**하며, **경기 경험을 공유할 수 있는 공간**을 제공합니다.

단순한 커뮤니티를 넘어,  
**온라인과 오프라인**, **팬과 팬** 사이의 경계를 허무는 **새로운 응원의 장**을 열어갑니다.

---

## 주요 기능

### 실시간 응원 피드 (라이브보드)

- 경기별 **실시간 채팅방 제공**
- **KBO 공식 문자 중계 연동**
    - 텍스트 중계
    - 실시간 경기 현황 시각화 (타자, 주자, 수비 위치 등)
- 비회원도 자유롭게 중계 확인 가능

---

### 위치 기반 티켓 & 굿즈 교환

- 티켓/굿즈 **교환 요청 등록**
- **내 주변 교환 물품을 지도에서 확인**
- 교환 수락 시 **1:1 실시간 채팅**으로 장소 조율

---

### 경기 일정 조회 & 예매 알림

- **팀별/일자별 경기 일정 조회**
- **선예매 / 일반예매 알림 설정**
- 관심 경기 등록 및 푸시 알림

---

### 나의 직관 기록 & 개인화 피드

- 직관 이력 자동 저장 (날짜, 좌석, 응원 기록 등)
- 좌석별 후기(시야, 승률, 만족도) 기록
- 개인화된 **추천 좌석** 및 응원 피드백 제공 예정

---

### 공통 기능

- 일반 로그인 및 **소셜 로그인 (Kakao, Google, Naver)**
- 사용자 프로필 관리

---

## 시스템 설계

### CLOUD ARCHITECTURE

![cloud 이미지](/readme/image/cloud.webp)

### CI/CD PIPELINE

![CI 이미지](/readme/image/ci.webp)

### WIRE FRAME

![wire 이미지](/readme/image/wire.webp)

### API 명세서

![명세서 이미지](/readme/image/api.webp)

### ERD

![erd 이미지](/readme/image/erd.webp)

---

## 기술 스택

### Language

- [Java 21](https://www.notion.so/Java-21-2262dc3ef51481ea9085c3522d44560d?pvs=21)

### Backend

- [Spring Boot](https://www.notion.so/Spring-boot-2262dc3ef514818abd14cf28c7de6b0d?pvs=21)
- [Spring Validation](https://www.notion.so/Spring-Validation-2262dc3ef51481929073f54e8ccc25c0?pvs=21)
- [Spring Boot Web](https://www.notion.so/Spring-Boot-Web-2262dc3ef51481f5b0f3e817a8bbf04f?pvs=21)
- [Spring Data JPA](https://www.notion.so/Spring-Data-Jpa-2262dc3ef514812490edf9443b6fade2?pvs=21)
- [QueryDSL](https://www.notion.so/QueryDSL-2262dc3ef51481388de9e932b43cc34a?pvs=21)
- [Node.js](https://www.notion.so/Node-js-2262dc3ef51481389e28df599c47248e?pvs=21)
- [Lombok](https://www.notion.so/Lombok-2262dc3ef5148147aa1bf52c49612dc2?pvs=21)

### Frontend

- [Vue.js](https://www.notion.so/Vue-js-2262dc3ef5148168a992df1abe90960e?pvs=21)
- [JavaScript](https://www.notion.so/JavaScript-2262dc3ef514819eae86e2706513125f?pvs=21)

### Database

- [MySQL](https://www.notion.so/MySQL-2262dc3ef5148173b2cec77327b7181c?pvs=21)
- [Redis](https://www.notion.so/Redis-2262dc3ef51481fbbd0ff1bf52372d25?pvs=21)

### IDE

- [IntelliJ](https://www.notion.so/IntelliJ-2262dc3ef51481ed9d9bf3f3bef7fcf3?pvs=21)

### Tools

- [Swagger](https://www.notion.so/Swagger-2262dc3ef51481979fe4cb64bb6903b7?pvs=21)
- [Postman](https://www.notion.so/Postman-2262dc3ef514812fad49ddd30ff8f9dc?pvs=21)
- [Selenium](https://www.notion.so/Selenium-2262dc3ef51481e5b1b9e545b5df8f1e?pvs=21)
- [Jsoup](https://www.notion.so/Jsoup-2262dc3ef5148104a660c47baed9a56b?pvs=21)

### Security

- [Spring Security](https://www.notion.so/Spring-Security-2262dc3ef514811dad9afb125358b222?pvs=21)
- [JWT](https://www.notion.so/JWT-2262dc3ef514811992c1e607d01cb68a?pvs=21)
- [OAuth](https://www.notion.so/Oauth-2262dc3ef51481e78d78e47ac8184d56?pvs=21)

### Messaging

- [Redis Message Queue](https://www.notion.so/Redis-Message-Queue-2262dc3ef51481c49b07ed3797adbcac?pvs=21)
- [WebSocket](https://www.notion.so/WebSocket-2262dc3ef51481b3967ad03169a6ec88?pvs=21)
- [STOMP](https://www.notion.so/Stomp-2262dc3ef51481aea902e6859fc32b4a?pvs=21)

### Cloud

- [Ubuntu Linux](https://www.notion.so/Ubuntu-Linux-2262dc3ef51481448d49c2da95f29aed?pvs=21)
- [Amazon EC2](https://www.notion.so/Amazon-EC2-2262dc3ef514816796b0f1d57afcdc89?pvs=21)
- [Amazon ECS](https://www.notion.so/Amazon-ECS-2262dc3ef51481e7aa1cc8e2a9b93255?pvs=21)
- [Amazon S3](https://www.notion.so/Amazon-S3-2262dc3ef5148159af2ed9c168d0b5fa?pvs=21)
- [Route 53](https://www.notion.so/Route-53-2262dc3ef5148159bba9dc8c48f3b932?pvs=21)
- [AWS WAF](https://www.notion.so/AWS-WAF-2262dc3ef51481cbbdfcf4b22b9e5d00?pvs=21)

### Deployment

- [Docker](https://www.notion.so/Docker-2262dc3ef51481979834d63cd333e678?pvs=21)
- [GitHub Actions](https://www.notion.so/Github-Actions-2262dc3ef5148156bbebc9b6a42ba9f8?pvs=21)

### Collaboration

- [Git](https://www.notion.so/Git-2262dc3ef5148131a734eb360e0750e0?pvs=21)
- [GitHub](https://www.notion.so/Github-2262dc3ef5148197acb6d2b903f2b954?pvs=21)
- [Slack](https://www.notion.so/Slack-2262dc3ef5148179874af7fd5ae026a0?pvs=21)
- [Notion](https://www.notion.so/Notion-2262dc3ef51481e78e95df2fa54b33cd?pvs=21)
- [Zep](https://www.notion.so/Zep-2262dc3ef51481eeb164d35fda3d71ff?pvs=21)
- [Figma](https://www.notion.so/Figma-2262dc3ef5148121b69bfd23320a8684?pvs=21)
- [ERD Cloud](https://www.notion.so/ERD-Cloud-2262dc3ef51481b999c9cc9fea7040f7?pvs=21)

