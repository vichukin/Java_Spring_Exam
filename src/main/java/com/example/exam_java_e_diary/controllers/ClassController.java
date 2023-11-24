package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.SchoolClass;
import com.example.exam_java_e_diary.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
public class ClassController {
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "/admin/classes")
    private String index(HttpServletRequest request, Model model){
        model.addAttribute("page",4);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var classes = classRepository.findAll();
        model.addAttribute("list",classes);

        return "/AdminPanel/Class/index";
    }
    @GetMapping(value = "/admin/classes/create")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",4);var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var schools = schoolRepository.findAll();
        model.addAttribute("schools",schools);
        return "/AdminPanel/Class/create";
    }
    @PostMapping(value = "/admin/classes/create")
    private String create(@RequestParam String name, @RequestParam long school, @RequestParam(required = false) Long teacher, @RequestParam(required = false) Long[] members, HttpServletRequest request,Model model){
        model.addAttribute("page",4);var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        SchoolClass class_ = new SchoolClass();
        class_.setName(name);
        var sc = schoolRepository.findById(school).orElse(null);
        if(sc!=null)
        {
            class_.setSchool(sc);
            if(teacher!=null&&teacher!=0)
            {
                var teach = teacherRepository.findById(teacher).orElse(null);
                if(teach!=null)
                {
                    class_.setTeacher(teach);
                }
            }
            classRepository.save(class_);
            if(members!=null&&members.length>0)
            {
                for(var item: members)
                {
                    var member = memberRepository.findById(item).orElse(null);
                    if(member!=null)
                    {
                        member.setSchoolClass(class_);
                        memberRepository.save(member);
                    }
                }
            }

            return "redirect:/admin/classes";
        }
        return "/AdminPanel/Class/create";
    }
    @GetMapping(value = "/admin/classes/edit/{id}")
    private String create(@PathVariable long id, HttpServletRequest request,Model model){
        model.addAttribute("page",4);var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var class_ = classRepository.findById(id).orElse(null);
        if(class_!=null)
        {
            var schools = schoolRepository.findAll();
            model.addAttribute("schools",schools);
            model.addAttribute("cl", class_);
            return "/AdminPanel/Class/edit";
        }

        return "refirect:/admin/classes";
    }
    @PostMapping(value = "/admin/classes/edit/{id}")
    private String create(@PathVariable long id,@RequestParam String name, @RequestParam long school, @RequestParam(required = false) Long teacher, HttpServletRequest request,Model model){
        model.addAttribute("page",4);var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var class_ = classRepository.findById(id).orElse(null);

        if(class_!=null)
        {
            class_.setName(name);
            var sc = schoolRepository.findById(school).orElse(null);
            if(sc!=null)
            {
                class_.setSchool(sc);
                if(teacher!=null&&teacher!=0)
                {
                    var teach = teacherRepository.findById(teacher).orElse(null);
                    if(teach!=null)
                    {
                        class_.setTeacher(teach);
                    }
                }
                classRepository.save(class_);
                return "redirect:/admin/classes";
            }


        }

        var schools = schoolRepository.findAll();
        model.addAttribute("schools",schools);
        model.addAttribute("cl", class_);
        return "/AdminPanel/Class/edit";
    }
}
