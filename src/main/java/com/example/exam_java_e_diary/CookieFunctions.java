package com.example.exam_java_e_diary;

import com.example.exam_java_e_diary.models.User;
import com.example.exam_java_e_diary.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CookieFunctions {
    public static User getAuthorisedUser(HttpServletRequest request, UserRepository userRepository)
    {
        var cookies = request.getCookies();
        var usercheck = Arrays.stream(cookies).filter(t->t.getName().equals("user")).collect(Collectors.toList());
        if (usercheck.size()!=0)
        {
            var id = usercheck.get(0).getValue();
            User us = userRepository.findById(Long.parseLong(id)).orElse(null);
            return us;
        }
        return null;
    }
}
