package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.Member;
import com.example.exam_java_e_diary.models.SchoolSubject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberRepository extends CrudRepository<Member,Long> {
    List<Member> findAllBySchoolId(long schoolId);
    List<Member> findAllBySchoolClassId(long schoolClassId);
}
