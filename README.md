# 📚 Edu-platform - 강의 관리 시스템

## 📌 프로젝트 개요
"Edu-platform"은 **교수님의 강의 관리 시스템**입니다.  
이 시스템을 통해 교수님과 학생들이 보다 **원활하게 소통**할 수 있으며, 강의 자료 공유, 질문 게시판, 팀 프로젝트 관리 등 다양한 기능을 제공합니다.  

> **현재 개발 중인 기능:**  
> 🔔 **알람 기능** - 새로운 게시글 및 댓글에 대한 실시간 알림  
> 👤 **개인 프로필 기능** - 사용자별 프로필 페이지 (닉네임, 활동 내역 등)

---

## 🛠 사용 기술
- **Backend:** Java (Spring Boot)
- **Frontend:** HTML, CSS, JavaScript (현재 프론트엔드 미완성)
- **Database:** PostgreSQL
- **Authentication:** Spring Security (이메일 & 비밀번호 기반 로그인)
- **Deployment:** AWS EC2 (예정)

---

## 📂 주요 기능

### **1️⃣ 로그인 & 사용자 관리**
- Spring Security를 활용한 **이메일 & 비밀번호 기반 로그인**
- 사용자 권한에 따라 **교수, 조교, 학생, 반장**으로 구분
- `stdId`를 기반으로 **자동 권한 부여 (Trigger 사용)**

### **2️⃣ 강의 목록**
- 교수는 **새로운 강의 등록 가능**
- 학생은 **본인이 수강하는 강의 선택 가능**
- 모든 강의 관련 데이터는 `LectureId`를 외래키로 사용하여 **강의별 데이터 관리**

### **3️⃣ 수업자료 공유 게시판**
📌 **세부 기능**
1. **수업 내용 공유**: 필기 공유 및 질문 가능 (모든 사용자가 글 작성 가능)
2. **족보 게시판**: 교수/조교만 글 작성 가능
3. **강의 요청 게시판**: 학생들이 원하는 강의 주제를 요청 가능, `Like` 기능 포함

### **4️⃣ 팀플 게시판**
- **팀장**이 직접 팀을 구성하고 조교의 승인을 받아야 함
- 승인된 팀은 **팀별 게시판**이 자동 생성됨 (`Trigger` 활용)
- **팀별 보고서 업로드 및 댓글 기능 제공**
- **팀장**: 게시판 수정 및 삭제 권한 보유
- **팀원**: 게시글 작성 및 피드백 가능

### **5️⃣ 만남 게시판**
- **교수만 게시글 작성 가능** (예: 수업 외 식사 모임, 스터디)
- 학생들은 `Like` 버튼을 눌러 참여 신청 가능
- 일정 인원이 초과되면 **신청 마감 (Trigger 사용)**

### **6️⃣ 공지 게시판**
- **교수 & 조교만 공지사항 작성 가능**
- **수업 관련 중요한 공지 등록**

### **7️⃣ 질문 게시판**
- 모든 사용자가 질문 가능
- 질문에 대한 `Like` & `신고` 기능 제공
- **반장**은 일정 수 이상의 신고가 접수되면 **게시글 관리 권한 획득**

### **8️⃣ 알람 기능 (구현 중)**
- 교수/조교/반장/학생의 역할에 따라 **다른 유형의 알람 제공**
- 예: 교수는 강의 요청 게시글 생성 시 알림을 받음
- `Alram` 테이블을 활용하여 **읽음/안읽음 상태 관리**

### **9️⃣ 개인 프로필 기능 (구현 중)**
- 학생들은 자신의 **닉네임 및 활동 내역 확인 가능**
- **본인이 작성한 게시글 및 댓글 조회**
- 추후 **프로필 커스터마이징 기능 추가 예정**

---

## 📜 데이터베이스 스키마 (주요 테이블)
```sql
CREATE TABLE User (
    stdId VARCHAR(10) PRIMARY KEY,
    name CHAR(30),
    email CHAR(30) UNIQUE CHECK (email LIKE '%@pusan.ac.kr'),
    password CHAR(64),
    grade CHAR(12) CHECK (grade IN ('student', 'prof', 'studentleader', 'ra')),
    nickname CHAR(20) UNIQUE
);

CREATE TABLE LectureList (
    lectureId VARCHAR(10) PRIMARY KEY,
    name CHAR(50),
    profId VARCHAR(10) REFERENCES User(stdId)
);

CREATE TABLE LectureUser (
    lectureId VARCHAR(10),
    userId VARCHAR(10),
    PRIMARY KEY (lectureId, userId),
    FOREIGN KEY (lectureId) REFERENCES LectureList(lectureId),
    FOREIGN KEY (userId) REFERENCES User(stdId)
);

CREATE TABLE QuestionBoard (
    questionId SERIAL PRIMARY KEY,
    author VARCHAR(10) REFERENCES User(stdId),
    lectureId VARCHAR(10) REFERENCES LectureList(lectureId),
    title CHAR(100),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
> 🔎 **더 자세한 데이터베이스 스키마는 프로젝트 내 [`schema.sql`](schema.sql) 파일에서 확인 가능**

---

## 👨‍💻 팀원 역할 분배
| 이름   | 역할 |
|--------|-------------------------------|
| **김채현** | 데이터베이스 설계, 팀플 게시판 제외 게시판 구현, 좋아요 & 신고 기능 개발 |
| **김예슬** | 데이터베이스 설계, 회원가입 및 로그인 구현, 팀플 게시판 개발 |


