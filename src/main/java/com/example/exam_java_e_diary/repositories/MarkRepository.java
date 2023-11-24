package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.Lesson;
import com.example.exam_java_e_diary.models.Mark;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark,Long> {
    List<Mark> findAllByLessonId(long lessonId);
}
