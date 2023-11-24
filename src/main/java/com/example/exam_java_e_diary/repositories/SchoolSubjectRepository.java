package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.SchoolSubject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchoolSubjectRepository extends CrudRepository<SchoolSubject,Long> {
    List<SchoolSubject> findAllBySchoolId(long schoolId);
}
