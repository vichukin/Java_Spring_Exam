package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.*;
import com.example.exam_java_e_diary.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    @Autowired
    private SubjectRepository subjectRepository;
    @GetMapping(value = "/")
    private String Index( HttpServletRequest request, Model model){
        model.addAttribute("page",1);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));

        return "Home/index";
    }
    @GetMapping(value = "/journal")
    private String journal(HttpServletRequest request,@RequestParam(required = false) Integer choose,@RequestParam(required = false) Long school,@RequestParam(required = false) Long class_,@RequestParam(required = false) Long teacher, Model model){
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        model.addAttribute("choose",choose);
        if(user==null)
        {
            return "redirect:/";
        }
        var index = getMenuItem(choose,model,user);
        if(index==1)
        {
            return "redirect:/journal/member/"+user.getId();
        }
        else if(index==0)
            return "redirect:/journal/error";
        return "UserPanel/index";
    }
    @GetMapping(value = "/journal/error")
    private String getError(HttpServletRequest request,Model model)
    {
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        return "UserPanel/error";
    }
    @GetMapping(value = "/journal/member/{id}")
    private String getMember(@PathVariable Long id, @RequestParam(required = false) Long school, HttpServletRequest request,Model model)
    {
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null||(user.getId()!=id&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        model.addAttribute("memberPanel",true);
        List<School> schools = new ArrayList<>();
        for(var member: user.getMembers())
        {
            schools.add(member.getSchool());
        }
        model.addAttribute("schools",schools);
        if(school!=null)
        {
            var memb = user.getMembers().stream().filter(t->t.getSchool().getId()==school).findFirst().orElse(null);
            HashMap<String, ArrayList<Mark>> marks = new HashMap<>();
            for(var mark: memb.getMarks())
            {
                String subjName = mark.getLesson().getSubject().getSubject().getName();
                if(marks.keySet().contains(subjName))
                    marks.get(subjName).add(mark);
                else
                {
                    marks.put(subjName,new ArrayList<>());
                    marks.get(subjName).add(mark);
                }
            }
            var bufMax = marks.keySet().stream().max(Comparator.comparing(t->marks.get(t).size())).orElse(null);

            if(bufMax!=null)
            {
                var max = marks.get(bufMax).size();
                model.addAttribute("max",max);
            }

            model.addAttribute("marks",marks);
            return "UserPanel/marks";
        }
        return "UserPanel/member";
    }
    @GetMapping(value = "/journal/school/{school}")
    private String getSchool(HttpServletRequest request,@PathVariable Long school,@RequestParam(required = false) Integer choose,@RequestParam(required = false) Long class_,@RequestParam(required = false) Long teacher,@RequestParam(required = false)Long member,@RequestParam(required = false) Long subject, Model model){
        var item = schoolRepository.findById(school).orElse(null);
        model.addAttribute("page",7);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        model.addAttribute("item",item);
        if(User.isAdmin(user))
            model.addAttribute("isDirector",true);
        else
            model.addAttribute("isDirector",User.isDirector(user,item));
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
            var teachers = teacherRepository.findAllBySchoolId(school);
            model.addAttribute("schoolTeachers", teachers);
            var buf = memberRepository.findAllBySchoolId(school).stream().filter(t->t.getSchoolClass()==null).collect(Collectors.toList());
            ArrayList<Member> members = new ArrayList<>();
            buf.forEach(t->{
                var memb = new Member();
                memb.setId(t.getId());
                memb.setFullname(t.getFullname());
                members.add(memb);
            });
            model.addAttribute("availableMembers",members);
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
            var buf = subjectRepository.findAll();
            List<Subject> availableSubjects = new ArrayList<>();
            for (Subject t1 : buf) {
                Subject subj = new Subject();
                subj.setName(t1.getName());
                subj.setId(t1.getId());
                availableSubjects.add(subj);
            }
            var currentSubjects = new ArrayList<Long>();
            item.getSubjects().forEach(t->{

                currentSubjects.add(t.getSubject().getId());
            });
            availableSubjects = availableSubjects.stream().filter(t->!currentSubjects.contains(t.getId())).collect(Collectors.toList());
            model.addAttribute("availableSubjects",availableSubjects);
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
        model.addAttribute("isAdmin", User.isAdmin(user));
        model.addAttribute("item",item);
        model.addAttribute("choose",choose);
        if(User.isAdmin(user))
            model.addAttribute("isDirector",true);
        else
            model.addAttribute("isDirector",User.isDirector(user,item.getSchool()));
        if(user==null||((item.getTeacher()==null||user.getId()!=item.getTeacher().getId())&&!User.isDirector(user,item.getSchool())&&!User.isAdmin(user)))
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
            var buf = memberRepository.findAllBySchoolId(item.getSchool().getId()).stream().filter(t->t.getSchoolClass()==null).collect(Collectors.toList());
            ArrayList<Member> memBuf = new ArrayList<>();
            buf.forEach(t->{
                var memb = new Member();
                memb.setId(t.getId());
                memb.setFullname(t.getFullname());
                memBuf.add(memb);
            });
            model.addAttribute("availableMembers",memBuf);

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
        model.addAttribute("isAdmin", User.isAdmin(user));
        model.addAttribute("item",item);
        model.addAttribute("choose",choose);
        model.addAttribute("markTypes",markTypes);
        var members = memberRepository.findAllBySchoolClassId(item.getSchoolClass().getId());
        if(User.isAdmin(user))
            model.addAttribute("isDirector",true);
        else
            model.addAttribute("isDirector",User.isDirector(user,item.getSchool()));
        if(user==null||(user.getId()!=item.getTeacher().getId()&&!User.isDirector(user,item.getSchool())&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        getMenuItem(choose,model,user);
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

        return "UserPanel/lesson";
    }
    @PostMapping(value = "/journal/lesson/addmark")
    public String addMark(HttpServletRequest request,@ModelAttribute Mark mark, Model model)
    {
        markRepository.save(mark);
        return "redirect:/journal/lesson/"+mark.getLesson().getId();
    }
    @PostMapping(value = "/journal/school/addmember")
    public String addMember(HttpServletRequest request, @RequestParam Long school,@RequestParam String fullname,@RequestParam String login, Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        var sc = schoolRepository.findById(school).orElse(null);
        var directors = sc.getTeachers().stream().filter(t->t.isDirector()).collect(Collectors.toList());
        var teacher = teacherRepository.findAllByUserId(user.getId());

        if(user==null||(!directors.contains(teacher)&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        var newUs = userRepository.findByLogin(login);
        if(newUs!=null)
        {
            Member member = new Member();
            member.setFullname(fullname);
            member.setSchool(sc);
            member.setUser(newUs);
            memberRepository.save(member);
        }
        return "redirect:/journal/school/"+school+"?member=0";
    }
    @PostMapping(value = "/journal/school/addteacher")
    public String addTeacher(HttpServletRequest request, @RequestParam(required = false) Boolean isDirector, @RequestParam Long school,@RequestParam String fullname,@RequestParam String login, Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        var sc = schoolRepository.findById(school).orElse(null);
//        var directors = sc.getTeachers().stream().filter(t->t.isDirector()).collect(Collectors.toList());
//        var teacher = teacherRepository.findAllByUserId(user.getId());

        if(user==null||(!User.isDirector(user,sc)&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        var newUs = userRepository.findByLogin(login);
        if(newUs!=null)
        {
            Teacher teach = new Teacher();
            teach.setFullname(fullname);
            teach.setSchool(sc);
            teach.setUser(newUs);
            teach.setDirector(isDirector==null?false:true);
            teacherRepository.save(teach);
        }
        return "redirect:/journal/school/"+school+"?teacher=0";
    }
    @PostMapping(value = "/journal/school/addsubject")
    public String addSubject(HttpServletRequest request,@RequestParam Long school,@RequestParam(required = false) Long[] subjects, Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        var sc = schoolRepository.findById(school).orElse(null);
//        var directors = sc.getTeachers().stream().filter(t->t.isDirector()).collect(Collectors.toList());
//        var teacher = teacherRepository.findAllByUserId(user.getId());

        if(user==null||(!User.isDirector(user,sc)&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        if(subjects!=null)
            for (var id: subjects)
            {
                var subject = subjectRepository.findById(id).orElse(null);
                SchoolSubject schoolSubject =new SchoolSubject();
                schoolSubject.setSubject(subject);
                schoolSubject.setSchool(sc);
                schoolSubjectRepository.save(schoolSubject);
            }
        return "redirect:/journal/school/"+school+"?subject=0";
    }
    @PostMapping(value = "/journal/school/createclass")
    public String createClass(HttpServletRequest request, @RequestParam Long school,@RequestParam String name,@RequestParam(required = false) Long teacher, @RequestParam(required = false) Long[] members, Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        var sc = schoolRepository.findById(school).orElse(null);
        var directors = sc.getTeachers().stream().filter(t->t.isDirector()).collect(Collectors.toList());
        var teach = teacherRepository.findAllByUserId(user.getId());

        if(user==null||(!directors.contains(teach)&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        SchoolClass class_ = new SchoolClass();
        class_.setName(name);
        if(sc!=null)
        {
            class_.setSchool(sc);
            if(teacher!=null&&teacher!=0)
            {
                teach = teacherRepository.findById(teacher).orElse(null);
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

            return "redirect:/journal/school/"+school+"?class_=0";
        }
        return "redirect:/journal/school/"+school+"?class_=0";
    }
    @PostMapping(value = "/journal/class/addmember")
    public String addMemberInClass(HttpServletRequest request, @RequestParam Long class_,@RequestParam(required = false) Long[] members, Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        var item = classRepository.findById(class_).orElse(null);
        var sc = schoolRepository.findById(item.getId()).orElse(null);
        var teach = teacherRepository.findAllByUserId(user.getId());

        if(user==null||(user.getId()!=item.getTeacher().getId()&&!User.isDirector(user,item.getSchool())&&!User.isAdmin(user)))
        {
            return "redirect:/";
        }
        if(members!=null)
        {
            for (var id: members)
            {
               var member= memberRepository.findById(id).orElse(null);
               member.setSchoolClass(item);
               memberRepository.save(member);
            }
        }
        return "redirect:/journal/class/"+class_+"?member=0";
    }
    private int getMenuItem(Integer choose, Model model, User user){
        if(User.isAdmin(user))
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
        else if(user.getMembers().size()>0)
        {
            return 1;
        }
        else
        {
            return 0;
        }
        return 2;
    }
    @GetMapping(value = "/createschool")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",8);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null)
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
        return "Home/createSchool";
    }
    @PostMapping(value = "/createschool")
    private String create(@RequestParam String name,@RequestParam String fullname, @RequestParam Long[] subjects, HttpServletRequest request,Model model){
        model.addAttribute("page",8);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null)
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
            Teacher teacher = new Teacher();
            teacher.setFullname(fullname);
            teacher.setUser(user);
            teacher.setDirector(true);
            teacher.setSchool(school);
            teacherRepository.save(teacher);
            if(subjects!=null)
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
        return "redirect:/";
    }

}
