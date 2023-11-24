package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.Lesson;
import com.example.exam_java_e_diary.models.SchoolSubject;
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
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private SchoolSubjectRepository schoolSubjectRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "/admin/lessons")
    private String index(HttpServletRequest request,Model model){
        model.addAttribute("page",5);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var list = lessonRepository.findAll();
        model.addAttribute("list",list);
        return "/AdminPanel/Lesson/index";
    }
    @GetMapping(value = "/admin/lessons/create")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",5);
var user = CookieFunctions.getAuthorisedUser(request,userRepository);
model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var schools = schoolRepository.findAll();
        model.addAttribute("schools",schools);
        return "/AdminPanel/Lesson/create";
    }
    @PostMapping(value = "/admin/lessons/create")
    private String create(@RequestParam(required = false) Long school, @RequestParam(required = false) Long class_,@RequestParam(required = false) Long teacher, @RequestParam(required = false) Long subject, HttpServletRequest request,Model model){
        model.addAttribute("page",5);
var user = CookieFunctions.getAuthorisedUser(request,userRepository);
model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var schools = schoolRepository.findAll();
        if(school!=null&&school!=0)
        {
            var sc = schoolRepository.findById(school).orElse(null);
            if(school!=null)
            {
                model.addAttribute("subjects",sc.getSubjects());
                model.addAttribute("classes",sc.getClasses());
                model.addAttribute("teachers",sc.getTeachers());
                model.addAttribute("subject",subject);
                model.addAttribute("school",school);
                model.addAttribute("teacher",teacher);
                model.addAttribute("class_",class_);
                Lesson lesson = new Lesson();
                lesson.setSchool(sc);
                if(subject!=null&&subject!=0)
                {
                    SchoolSubject subj = schoolSubjectRepository.findById(subject).orElse(null);
                    lesson.setSubject(subj);
                    if(class_!=null&&class_!=0)
                    {
                        var cl = classRepository.findById(class_).orElse(null);
                        lesson.setSchoolClass(cl);
                        if(teacher!=null&&teacher!=0)
                        {
                            var teach = teacherRepository.findById(teacher).orElse(null);
                            lesson.setTeacher(teach);
                            lessonRepository.save(lesson);
                            return "redirect:/admin/lessons";
                        }
                        else
                        {

                            model.addAttribute("errorTeach","Teacher is required!");
                        }
                    }
                    else
                    {

                        model.addAttribute("errorCl","Class is required!");
                    }
                }
                else
                {

                    model.addAttribute("errorSub","Subject is required!");
                }
            }


        }
        else
        {
            model.addAttribute("errorSc","School is required!");
        }

        model.addAttribute("schools",schools);
        return "/AdminPanel/Lesson/create";
    }
    @PostMapping(value = "/admin/lessons/edit/{id}")
    private String edit(@PathVariable long id, @RequestParam long lessonId, @RequestParam(required = false) Long school, @RequestParam(required = false) Long class_, @RequestParam(required = false) Long teacher, @RequestParam(required = false) Long subject, HttpServletRequest request, Model model){
        model.addAttribute("page",5);
var user = CookieFunctions.getAuthorisedUser(request,userRepository);
model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        if(lessonId==id) {
            var lesson = lessonRepository.findById(id).orElse(null);
            if (lesson != null) {
                if(school!=null&&school!=0)
                {
                    var sc = schoolRepository.findById(school).orElse(null);
                    if(school!=null)
                    {
                        model.addAttribute("subjects",sc.getSubjects());
                        model.addAttribute("classes",sc.getClasses());
                        model.addAttribute("teachers",sc.getTeachers());
                        model.addAttribute("subject",subject);
                        model.addAttribute("school",school);
                        model.addAttribute("teacher",teacher);
                        model.addAttribute("class_",class_);
                        model.addAttribute("id",lesson.getId());
                        lesson.setSchool(sc);
                        if(subject!=null&&subject!=0)
                        {
                            SchoolSubject subj = schoolSubjectRepository.findById(subject).orElse(null);
                            lesson.setSubject(subj);
                            if(class_!=null&&class_!=0)
                            {
                                var cl = classRepository.findById(class_).orElse(null);
                                lesson.setSchoolClass(cl);
                                if(teacher!=null&&teacher!=0)
                                {
                                    var teach = teacherRepository.findById(teacher).orElse(null);
                                    lesson.setTeacher(teach);
                                    lessonRepository.save(lesson);
                                    return "redirect:/admin/lessons";
                                }
                                else
                                {

                                    model.addAttribute("errorTeach","Teacher is required!");
                                }
                            }
                            else
                            {

                                model.addAttribute("errorCl","Class is required!");
                            }
                        }
                        else
                        {

                            model.addAttribute("errorSub","Subject is required!");
                        }
                    }


                }
                else
                {
                    model.addAttribute("errorSc","School is required!");
                }
                var schools = schoolRepository.findAll();
                model.addAttribute("schools", schools);
                return "/AdminPanel/Lesson/edit";
            }
        }

        return "redirect:/admin/lessons";
    }
    @GetMapping(value = "/admin/lessons/edit/{id}")
    private String edit(@PathVariable long id, HttpServletRequest request,Model model){
        model.addAttribute("page",5);
var user = CookieFunctions.getAuthorisedUser(request,userRepository);
model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var lesson = lessonRepository.findById(id).orElse(null);
        if(lesson!=null)
        {
            model.addAttribute("subjects",lesson.getSchool().getSubjects());
            model.addAttribute("classes",lesson.getSchool().getClasses());
            model.addAttribute("teachers",lesson.getSchool().getTeachers());
            model.addAttribute("subject",lesson.getSubject().getId());
            model.addAttribute("school",lesson.getSchool().getId());
            model.addAttribute("teacher",lesson.getTeacher().getId());
            model.addAttribute("class_",lesson.getSchoolClass().getId());
            model.addAttribute("id",lesson.getId());
            var schools = schoolRepository.findAll();
            model.addAttribute("schools",schools);
            return "/AdminPanel/Lesson/edit";
        }

        return "redirect:/admin/lessons";
    }
}
