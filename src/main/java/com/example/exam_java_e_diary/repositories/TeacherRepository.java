package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeacherRepository extends CrudRepository<Teacher,Long> {
    List<Teacher> findAllBySchoolId(long schoolId);
    Teacher findAllByUserId(long userId);
}
