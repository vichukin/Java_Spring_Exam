package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.School;
import com.example.exam_java_e_diary.models.SchoolSubject;
import com.example.exam_java_e_diary.models.Subject;
import com.example.exam_java_e_diary.repositories.SchoolRepository;
import com.example.exam_java_e_diary.repositories.SchoolSubjectRepository;
import com.example.exam_java_e_diary.repositories.SubjectRepository;
import com.example.exam_java_e_diary.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SchoolController {
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SchoolSubjectRepository schoolSubjectRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "/admin/schools")
    private String index(HttpServletRequest request, Model model){
        model.addAttribute("page",2);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var schools = schoolRepository.findAll();
        model.addAttribute("list",schools);
        return "AdminPanel/School/index";
    }
    @GetMapping(value = "/admin/schools/create")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",2);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        Iterable<Subject> list = subjectRepository.findAll();
        ArrayList<Subject> ls = new ArrayList<>();
        list.forEach(t->{
            var buf = new Subject();
            buf.setId(t.getId());
            buf.setName(t.getName());
            ls.add(buf);
        });
        model.addAttribute("array",ls);
        return "AdminPanel/School/create";
    }
    @PostMapping(value = "/admin/schools/create")
    private String create(@RequestParam String name, @RequestParam int[] subjects, HttpServletRequest request,Model model){
        model.addAttribute("page",2);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var buflist = subjectRepository.findAll();
        ArrayList<Subject> list = new ArrayList<>();
        buflist.forEach(t->list.add(t));
        if(subjects.length>0)
        {
            School school = new School();
            school.setName(name);
            schoolRepository.save(school);
            for(var subj: subjects)
            {
                var item = list.stream().filter(t->t.getId()==subj).findFirst().get();
                SchoolSubject buf = new SchoolSubject();
                buf.setSchool(school);
                buf.setSubject(item);
                schoolSubjectRepository.save(buf);
            }
        }

        model.addAttribute("array",list);
        return "redirect:/admin/schools";
    }
}
