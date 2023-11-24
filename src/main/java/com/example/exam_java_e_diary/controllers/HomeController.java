package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.*;
import com.example.exam_java_e_diary.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private SchoolSubjectRepository schoolSubjectRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private MarkTypeRepository markTypeRepository;
    @GetMapping(value = "/")
    private String Index( HttpServletRequest request, Model model){
        model.addAttribute("page",1);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);

        return "Home/index";
    }
    @GetMapping(value = "/journal")
    private String journal(HttpServletRequest request,@RequestParam(required = false) Integer choose,@RequestParam(required = false) Long school,@RequestParam(required = false) Long class_,@RequestParam(required = false) Long teacher, Model model){
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("choose",choose);
        if(user==null)
        {
            return "redirect:/";
        }
        getMenuItem(choose,model,user);
        return "UserPanel/index";
    }
    @GetMapping(value = "/journal/school/{school}")
    private String getSchool(HttpServletRequest request,@PathVariable Long school,@RequestParam(required = false) Integer choose,@RequestParam(required = false) Long class_,@RequestParam(required = false) Long teacher,@RequestParam(required = false)Long member,@RequestParam(required = false) Long subject, Model model){
        var item = schoolRepository.findById(school).orElse(null);
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("item",item);
        model.addAttribute("choose",choose);
        if(user==null)
        {
            return "redirect:/";
        }
        getMenuItem(choose,model,user);
        if((teacher==null&&class_==null&&subject==null&&member==null)||class_!=null)
        {
            var classes = classRepository.findAllBySchoolId(school);
            model.addAttribute("class_", class_);
            model.addAttribute("classes", classes);
        }
        if(teacher!=null)
        {
            var teachers = teacherRepository.findAllBySchoolId(school);
            model.addAttribute("teacher", teacher);
            model.addAttribute("teachers", teachers);
        }
        if(subject!=null)
        {
            var subjects = schoolSubjectRepository.findAllBySchoolId(school);
            model.addAttribute("subjects",subjects);
            model.addAttribute("subject",subject);
        }
        if(member!=null)
        {
            var members = memberRepository.findAllBySchoolId(school);
            model.addAttribute("members", members);
            model.addAttribute("member", member);

        }


        return "UserPanel/school";
    }
    @GetMapping(value = "/journal/class/{class_}")
    private String getClass(HttpServletRequest request,@PathVariable Long class_,@RequestParam(required = false) Integer choose,@RequestParam(required = false)Long member,@RequestParam(required = false) Long lesson, Model model){
        var item = classRepository.findById(class_).orElse(null);
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("item",item);
        model.addAttribute("choose",choose);
        if(user==null)
        {
            return "redirect:/";
        }
        getMenuItem(choose,model,user);
        if(lesson!=null)
        {
            var lessons = lessonRepository.findAllBySchoolClassId(class_);
            model.addAttribute("lessons",lessons);
            model.addAttribute("lesson",lesson);
        }
        else
        {
            var members = memberRepository.findAllBySchoolClassId(class_);
            model.addAttribute("members", members);
            model.addAttribute("member", member);

        }
        return "UserPanel/class";
    }
    @GetMapping(value = "/journal/lesson/{lesson}")
    private String getLesson(HttpServletRequest request,@PathVariable Long lesson,@RequestParam(required = false) Integer choose,@RequestParam(required = false)Long member,@RequestParam(required = false) Long mark, Model model){
        var item = lessonRepository.findById(lesson).orElse(null);
        var markTypes = markTypeRepository.findAll();
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("item",item);
        model.addAttribute("choose",choose);
        model.addAttribute("markTypes",markTypes);
        var members = memberRepository.findAllBySchoolClassId(item.getSchoolClass().getId());
        model.addAttribute("members", members);
        model.addAttribute("member", member);
        if(member==null)
        {
            var marks = markRepository.findAllByLessonId(lesson);
            model.addAttribute("marks",marks);
            model.addAttribute("mark",mark);
            var max = members.stream().max(Comparator.comparing(t->t.getMarks().size())).orElse(null);
            if(max!=null)
                model.addAttribute("max",(max.getMarks().size()));

        }

        if(choose==null||choose==1)
        {
            var schools = schoolRepository.findAll();
            model.addAttribute("schools",schools);
        }
        else if(choose==2)
        {
            var classes = classRepository.findAll();
            model.addAttribute("menuClasses",classes);
        }
        else if(choose==3)
        {
            var lessons = lessonRepository.findAll();
            model.addAttribute("menuLessons",lessons);
        }

        return "UserPanel/lesson";
    }
    @PostMapping(value = "/journal/lesson/addmark")
    public String addMark(HttpServletRequest request,@ModelAttribute Mark mark, Model model)
    {
        markRepository.save(mark);
        return "redirect:/journal/lesson/"+mark.getLesson().getId();
    }
    private void getMenuItem(Integer choose, Model model, User user){
        if(user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()>0)
        {
            if(choose==null||choose==1)
            {
                var schools = schoolRepository.findAll();
                model.addAttribute("schools",schools);
            }
            else if(choose==2)
            {
                var classes = classRepository.findAll();
                model.addAttribute("menuClasses",classes);
            }
            else if(choose==3)
            {
                var lessons = lessonRepository.findAll();
                model.addAttribute("menuLessons",lessons);
            }
        }
        else if(user.getTeachers().size()>0)
        {

            if(choose==null||choose==1)
            {
                List<School> schools = new ArrayList<>();
                for(var teach: user.getTeachers())
                {
                    schools.add(teach.getSchool());
                }
                model.addAttribute("schools",schools);
            }
            else if(choose==2)
            {
                List<SchoolClass> classes = new ArrayList<>();
                for(var teach: user.getTeachers())
                {
                    if(teach.isDirector())
                    {
                        classes.addAll(teach.getSchool().getClasses());
                    }
                    else
                    {
                        classes.addAll(teach.getClasses());
                    }
                }
                model.addAttribute("menuClasses",classes);
            }
            else if(choose==3)
            {
                List<Lesson> lessons = new ArrayList<>();
                for(var teach: user.getTeachers())
                {
                    if(teach.isDirector())
                    {
                        lessons.addAll(teach.getSchool().getLessons());
                    }
                    else
                    {
                        lessons.addAll(teach.getLessons());
                    }
                }
                model.addAttribute("menuLessons",lessons);
            }
        }
    }
}
