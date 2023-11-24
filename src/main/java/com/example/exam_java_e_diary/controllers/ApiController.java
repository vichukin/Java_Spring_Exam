package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.models.SchoolClass;
import com.example.exam_java_e_diary.models.Member;
import com.example.exam_java_e_diary.models.Subject;
import com.example.exam_java_e_diary.models.Teacher;
import com.example.exam_java_e_diary.repositories.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private SchoolRepository schoolRepository;
    @GetMapping(value = "/teachers/{id}")
    private ArrayList<Teacher> getTeachersBySchoolId(@PathVariable long id, Model model)
    {
        var school = schoolRepository.findById(id).orElse(null);
        if(school!=null)
        {
            ArrayList<Teacher> list = new ArrayList<>();
            Teacher teacher = new Teacher();
//            teacher.setId(123);
//            teacher.setFullname("Dima");
//            list.add(teacher);
            school.getTeachers().forEach(t->{
                var teach = new Teacher();
                teach.setId(t.getId());
                teach.setFullname(t.getFullname());

                list.add(teach);
            });
            return list;
        }
        return null;
    }
    @GetMapping(value = "/members/{id}")
    private ArrayList<Member> getMembersBySchoolId(@PathVariable long id, Model model)
    {
        var school = schoolRepository.findById(id).orElse(null);
        if(school!=null)
        {
            ArrayList<Member> list = new ArrayList<>();
            school.getMembers().forEach(t->{
                var member = new Member();
                member.setId(t.getId());
                member.setFullname(t.getFullname());

                list.add(member);
            });
            return list;
        }
        return null;
    }
    @GetMapping(value = "/subjects/{id}")
    private ArrayList<Subject> getSubjectsBySchoolId(@PathVariable long id, Model model)
    {
        var school = schoolRepository.findById(id).orElse(null);
        if(school!=null)
        {
            ArrayList<Subject> list = new ArrayList<>();
            school.getSubjects().forEach(t->{
                var subject = new Subject();
                subject.setId(t.getId());
                subject.setName(t.getSubject().getName());

                list.add(subject);
            });
            return list;
        }
        return null;
    }
    @GetMapping(value = "/classes/{id}")
    private ArrayList<SchoolClass> getClassesBySchoolId(@PathVariable long id, Model model)
    {
        var school = schoolRepository.findById(id).orElse(null);
        if(school!=null)
        {
            ArrayList<SchoolClass> list = new ArrayList<>();
            school.getClasses().forEach(t->{
                var class_ = new SchoolClass();
                class_.setId(t.getId());
                class_.setName(t.getName());

                list.add(class_);
            });
            return list;
        }
        return null;
    }
}
