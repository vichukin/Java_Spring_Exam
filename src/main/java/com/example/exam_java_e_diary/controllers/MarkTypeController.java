package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.MarkType;
import com.example.exam_java_e_diary.repositories.MarkTypeRepository;
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
public class MarkTypeController {
    @Autowired
    private MarkTypeRepository markTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping(value = "/admin/marktypes")
    private String index(HttpServletRequest request,Model model){
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var marktypes = markTypeRepository.findAll();
        model.addAttribute("list",marktypes);
        return "AdminPanel/MarkType/index";
    }
    @GetMapping(value = "/admin/marktypes/create")
    private String create(HttpServletRequest request,Model model){
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        return "AdminPanel/MarkType/create";
    }
    @PostMapping(value = "/admin/marktypes/create")
    private String create(@RequestParam String name, HttpServletRequest request,Model model){
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        MarkType markType = new MarkType();
        markType.setName(name);
        markTypeRepository.save(markType);
        return "redirect:/admin/marktypes";
    }
    @GetMapping(value = "/admin/marktypes/edit/{id}")
    private String edit(@PathVariable(value = "id")long id, HttpServletRequest request,Model model){
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var markType = markTypeRepository.findById(id).orElse(null);
        if(markType!=null)
        {
            model.addAttribute("item", markType);
            return "AdminPanel/MarkType/edit";
        }
        else
            return "redirect:/admin/marktypes";
    }

    @PostMapping(value = "/admin/marktypes/edit/{id}")
    private String edit(@PathVariable long id, @RequestParam long itemId, @RequestParam String name, HttpServletRequest request, Model model)
    {
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        if(id==itemId)
        {
            var markType = markTypeRepository.findById(id).orElse(null);
            if(markType!=null)
            {
                markType.setName(name);
                markTypeRepository.save(markType);
            }
        }
        return "redirect:/admin/marktypes";
    }

    @GetMapping(value = "/admin/marktypes/delete/{id}")
    private String delete(@PathVariable(value = "id")long id,HttpServletRequest request,Model model){
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        var markType = markTypeRepository.findById(id).orElse(null);
        if(markType!=null)
        {
            model.addAttribute("item", markType);
            return "AdminPanel/MarkType/delete";
        }
        else
            return "redirect:/admin/marktypes";
    }
    @PostMapping(value = "/admin/marktypes/delete/{id}")
    private String delete(@PathVariable long id, @RequestParam long itemId, HttpServletRequest request,Model model)
    {
        model.addAttribute("page",6);
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        if(user==null||user.getRoles().stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()==0)
        {
            return "redirect:/";
        }
        if(id==itemId)
        {
            var markType = markTypeRepository.findById(id).orElse(null);
            if(markType!=null)
            {
                markTypeRepository.delete(markType);
            }
        }
        return "redirect:/admin/marktypes";
    }
}
