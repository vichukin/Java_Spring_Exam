package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.Lesson;
import com.example.exam_java_e_diary.models.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson,Long> {
    List<Lesson> findAllBySchoolClassId(long schoolClassId);
}
