CREATE TABLE students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    date_of_birth DATE,
    address VARCHAR(255),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT pk_students PRIMARY KEY (id),
    CONSTRAINT uk_students_email UNIQUE (email)
);


CREATE TABLE courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    fees DECIMAL(10,2) NOT NULL,
    description VARCHAR(500),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT pk_courses PRIMARY KEY (id),
    CONSTRAINT uk_courses_course_code UNIQUE (course_code)
);


CREATE TABLE enrollments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT pk_enrollments PRIMARY KEY (id),

    CONSTRAINT uk_student_course
        UNIQUE (student_id, course_id),

    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT fk_enrollment_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
);