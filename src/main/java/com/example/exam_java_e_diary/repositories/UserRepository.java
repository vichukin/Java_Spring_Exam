package com.example.exam_java_e_diary.repositories;

import com.example.exam_java_e_diary.models.SchoolClass;
import com.example.exam_java_e_diary.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByLogin(String login);
}
