package com.example.exam_java_e_diary.repositories;
import com.example.exam_java_e_diary.models.SchoolClass;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassRepository extends CrudRepository<SchoolClass,Long> {
    List<SchoolClass> findAllBySchoolId(long schoolId);
}
