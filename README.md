<img width="1672" height="941" alt="visionary" src="https://github.com/user-attachments/assets/8ee1e819-3eaf-4516-b5ff-b231ef47ad29" />

# 🌌 Visionary Backend

> 설문을 바탕으로 성향과 흥미를 살펴보고 진로 탐색을 돕는 서비스

## ✨ 서비스 소개

Visionary는 사용자의 기본 정보, 성격, 흥미, 관심 분야 등을 설문으로 수집하고 그를 바탕으로 한 분석 결과를 조회할 수 있도록 구성한 진로 탐색 앱입니다.


## 🔥 주요 기능

- 🔐 SMS 인증 및 회원가입·로그인·로그아웃
- 📝 기본 정보·경제 상황·성격·흥미·관심 분야 설문 조사
- 📊 설문 세션별 개인 분석 결과(기본 정보, Big5 성격, RIASEC 흥미 등) 제공
- 🗂️ 로드맵

## 🏠 프로젝트 구조

```text
src/main/java/esu/visionary/
├── domain/
│   ├── user/             # 회원, 인증, 토큰
│   ├── survey/           # 설문 제출, 세션, 결과
│   ├── roadmap/          # 로드맵 
│   ├── job/              # 직업·추천 관련 엔티티
│   └── content/          # 명언
├── global/
│   ├── config/           # Security, SMS, Swagger 설정
│   ├── security/         # JWT 필터, 쿠키, 인증 오류 처리
│   ├── sms/              # SMS 인증
│   ├── response/         # 공통 API 응답
│   └── exception/        # 공통 예외 처리
└── VisionaryApplication.java
```

## 🏗️ 아키텍처

## 🛠 기술 스택

### ⚙️ Backend

<p>
  <img src="https://img.shields.io/badge/Java-21-007396?style=flat&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.3-6DB33F?style=flat&logo=springboot&logoColor=white" alt="Spring Boot 3.5.3" />
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=springsecurity&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat" alt="Spring Data JPA" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Redis-FF4438?style=flat&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white" alt="Flyway" />
  <img src="https://img.shields.io/badge/Swagger%20UI-85EA2D?style=flat&logo=swagger&logoColor=black" alt="Swagger UI" />
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white" alt="Gradle" />
</p>

### 🧪 Test

<p>
  <img src="https://img.shields.io/badge/JUnit%205-25A162?style=flat&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/Testcontainers-2496ED?style=flat" alt="Testcontainers" />
  <img src="https://img.shields.io/badge/H2-09476B?style=flat" alt="H2" />
</p>


## 🚧 CI/CD

## 👥 Team

| <img width="120px" src="https://avatars.githubusercontent.com/hdsjiw" /> | <img width="120px" src="https://avatars.githubusercontent.com/studyhard03" /> |
|:---:|:---:|
| BE | BE|
| [전지우](https://github.com/hdsjiw) | [이동하](https://github.com/studyhard03) |
<br/>

## 개발 기간
#### 1차 스프린트 : 2025. 07 ~ 2025. 08
#### 2차 스프린트 : 2026. 09 ~ 
