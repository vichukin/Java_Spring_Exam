package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.Subject;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<Subject,Long> {
}
