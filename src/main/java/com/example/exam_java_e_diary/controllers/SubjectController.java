package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.Subject;
import com.example.exam_java_e_diary.repositories.SubjectRepository;
import com.example.exam_java_e_diary.repositories.UserRepository;
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
public class SubjectController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "/admin/subjects")
    private String index(HttpServletRequest request, Model model){
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var subjects = subjectRepository.findAll();
        model.addAttribute("list",subjects);
        return "AdminPanel/Subject/index";
    }
    @GetMapping(value = "/admin/subjects/create")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        return "AdminPanel/Subject/create";
    }
    @PostMapping(value = "/admin/subjects/create")
    private String create(@RequestParam String name, HttpServletRequest request,Model model){
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        Subject subject = new Subject();
        subject.setName(name);
        subjectRepository.save(subject);
        return "redirect:/admin/subjects";
    }
    @GetMapping(value = "/admin/subjects/edit/{id}")
    private String edit(@PathVariable(value = "id")long id,HttpServletRequest request,Model model){
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var subj = subjectRepository.findById(id).orElse(null);
        if(subj!=null)
        {
            model.addAttribute("item", subj);
            return "AdminPanel/Subject/edit";
        }
        else
            return "redirect:/admin/subjects";
    }

    @PostMapping(value = "/admin/subjects/edit/{id}")
    private String edit(@PathVariable long id, @RequestParam long itemId, @RequestParam String name, HttpServletRequest request,Model model)
    {
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        if(id==itemId)
        {
            var subj = subjectRepository.findById(id).orElse(null);
            if(subj!=null)
            {
                subj.setName(name);
                subjectRepository.save(subj);
            }
        }
        return "redirect:/admin/subjects";
    }

    @GetMapping(value = "/admin/subjects/delete/{id}")
    private String delete(@PathVariable(value = "id")long id,HttpServletRequest request,Model model){
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var subj = subjectRepository.findById(id).orElse(null);
        if(subj!=null)
        {
            model.addAttribute("item", subj);
            return "AdminPanel/Subject/delete";
        }
        else
            return "redirect:/admin/subjects";
    }
    @PostMapping(value = "/admin/subjects/delete/{id}")
    private String delete(@PathVariable long id, @RequestParam long itemId, HttpServletRequest request,Model model)
    {
        model.addAttribute("page",3);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        if(id==itemId)
        {
            var subj = subjectRepository.findById(id).orElse(null);
            if(subj!=null)
            {
                subjectRepository.delete(subj);
            }
        }
        return "redirect:/admin/subjects";
    }
}
