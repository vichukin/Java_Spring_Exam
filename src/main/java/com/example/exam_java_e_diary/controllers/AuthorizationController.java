package com.example.exam_java_e_diary.controllers;

import com.example.exam_java_e_diary.CookieFunctions;
import com.example.exam_java_e_diary.models.User;
import com.example.exam_java_e_diary.repositories.RoleRepository;
import com.example.exam_java_e_diary.repositories.UserRepository;
import com.example.exam_java_e_diary.repositories.UserRoleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @GetMapping(value = "/login")
    private String getLoginForm(HttpServletRequest request,HttpServletResponse response,Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(user!=null)
        {
            return "redirect:/";
        }
        return "Authorization/login";
    }
    @PostMapping(value = "/login")
    private String getLoginForm(@ModelAttribute User user,HttpServletRequest request,HttpServletResponse response,Model model){
        var us = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(us!=null)
        {
            return "redirect:/";
        }
        if(user!=null)
        {
            var u = userRepository.findByLogin(user.getLogin());
            if(u!=null)
            {
                if(verifyPassword(user.getPassword(), u.getPassword()))
                {
                    addCookie(response,"user",String.valueOf(u.getId()),3600);
                    return "redirect:/";
                }
                else
                    model.addAttribute("passError",true);
            }
            else
            {
                model.addAttribute("logError",true);
                model.addAttribute("login",user.getLogin());
            }

        }
        return "Authorization/login";
    }
    @GetMapping(value = "/registration")
    private String getRegistrationForm(HttpServletRequest request,HttpServletResponse response, Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(user!=null)
        {
            return "redirect:/";
        }
        return "Authorization/registration";
    }

    @PostMapping(value = "/registration")
    private String Registration(@ModelAttribute User user, @RequestParam String confirmPassword, HttpServletRequest request,HttpServletResponse response,Model model)
    {
        var us = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(us!=null)
        {
            return "redirect:/";
        }
        if(user.getPassword().equals(confirmPassword))
        {
            String pas = hashPassword(user.getPassword());
            user.setPassword(pas);
            try {
                userRepository.save(user);
                addCookie(response,"user",String.valueOf(user.getId()),3600);
                return "redirect:/";
            }
            catch (Exception ex)
            {
                model.addAttribute("logError",true);
            }


        }
        else
        {

            model.addAttribute("passError",true);
        }
        model.addAttribute("login",user.getLogin());
        return "Authorization/registration";
    }
    @GetMapping(value = "/logout")
    private String logout(HttpServletRequest request,HttpServletResponse response, Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        if(user!=null)
        {
            addCookie(response,"user",null,0);
        }
        return "redirect:/";
    }
    private String hashPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    private boolean verifyPassword(String plainPassword, String storeHash){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(plainPassword, storeHash);
    }
    public void addCookie(HttpServletResponse response, String title, String value, Integer age){
        // Create a cookie with the same name and set its max age to 0 to delete it
        Cookie cookie = new Cookie(title, value);
        cookie.setPath("/"); // Make sure to set the path exactly as it was set when the cookie was created
        cookie.setHttpOnly(true); // Optional: If you have set HttpOnly when creating the cookie, you should also set it here
        cookie.setMaxAge(age); // Set the cookie's age to 0 to delete it

        // Add the cookie to the response to send it back to the browser
        response.addCookie(cookie);
    }
}
