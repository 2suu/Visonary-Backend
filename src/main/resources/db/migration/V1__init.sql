-- users
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       nickname VARCHAR(100),
                       provider ENUM('LOCAL','GOOGLE','KAKAO','NAVER') NOT NULL,
                       provider_id VARCHAR(255),
                       status ENUM('ACTIVE','INACTIVE','DELETED') NOT NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- survey_forms
CREATE TABLE survey_forms (
                              survey_form_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              code VARCHAR(100) NOT NULL,
                              version VARCHAR(50) NOT NULL,
                              ui_ver VARCHAR(50) NOT NULL,
                              schema_json JSON NOT NULL,
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- survey_sessions
CREATE TABLE survey_sessions (
                                 survey_session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 survey_form_id BIGINT NOT NULL,
                                 version VARCHAR(50) NOT NULL,
                                 started_at DATETIME NULL,
                                 completed_at DATETIME NULL,
                                 status ENUM('IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_ss_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                 CONSTRAINT fk_ss_form FOREIGN KEY (survey_form_id) REFERENCES survey_forms(survey_form_id)
);

-- survey_responses
CREATE TABLE survey_responses (
                                  survey_response_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  session_id BIGINT NOT NULL,
                                  question_code VARCHAR(128) NOT NULL,
                                  answer JSON NOT NULL,
                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_sr_session FOREIGN KEY (session_id) REFERENCES survey_sessions(survey_session_id)
);

-- jobs
CREATE TABLE jobs (
                      job_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(200) NOT NULL,
                      category VARCHAR(100),
                      description TEXT,
                      tasks TEXT,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- job_recommendations
CREATE TABLE job_recommendations (
                                     job_rec_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     user_id BIGINT NOT NULL,
                                     job_id BIGINT NOT NULL,
                                     score DECIMAL(5,2) NOT NULL,
                                     meta JSON,
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     CONSTRAINT fk_jr_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                     CONSTRAINT fk_jr_job FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);

-- roadmap_templates
CREATE TABLE roadmap_templates (
                                   roadmap_template_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   title VARCHAR(200) NOT NULL,
                                   description TEXT,
                                   job_id BIGINT NOT NULL,
                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_rt_job FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);

-- roadmap_steps
CREATE TABLE roadmap_steps (
                               roadmap_steps_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               roadmap_template_id BIGINT NOT NULL,
                               step_order INT NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               description TEXT,
                               stage JSON NOT NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_rs_tpl FOREIGN KEY (roadmap_template_id) REFERENCES roadmap_templates(roadmap_template_id)
);

-- personal_roadmaps
CREATE TABLE personal_roadmaps (
                                   personal_roadmap_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   user_id BIGINT NOT NULL,
                                   roadmap_template_id BIGINT NOT NULL,
                                   title_custom VARCHAR(200),
                                   progress_percent TINYINT NOT NULL DEFAULT 0,
                                   is_personalized TINYINT(1) NOT NULL DEFAULT 0,
                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_pr_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                   CONSTRAINT fk_pr_tpl FOREIGN KEY (roadmap_template_id) REFERENCES roadmap_templates(roadmap_template_id)
);

-- personal_roadmap_steps
CREATE TABLE personal_roadmap_steps (
                                        personal_roadmap_step_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        personal_roadmap_id BIGINT NOT NULL,
                                        step_order INT NOT NULL,
                                        title VARCHAR(200) NOT NULL,
                                        description TEXT,
                                        stage JSON,
                                        done_percent TINYINT NOT NULL DEFAULT 0,
                                        is_done TINYINT(1) NOT NULL DEFAULT 0,
                                        is_deleted TINYINT(1) NOT NULL DEFAULT 0,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_prs_pr FOREIGN KEY (personal_roadmap_id) REFERENCES personal_roadmaps(personal_roadmap_id)
);

-- quotes
CREATE TABLE quotes (
                        quotes_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        content VARCHAR(500) NOT NULL,
                        author VARCHAR(255),
                        category VARCHAR(100),
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
