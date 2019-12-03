package com.controllers;

import com.database.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private UserRepo userRepo = new UserRepo();

    private String message = null;

    @GetMapping("/registration")
    public String getRegistration(Model model){
        model.addAttribute("message", message);
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam String username,@RequestParam String surname,
                          @RequestParam String login,@RequestParam String password, Model model){
        message = null;
        if(userRepo.loadUserByUsername(login) != null) {
            message = "пользователь уже существует";
            model.addAttribute("message", message);
            return "redirect:/registration";
        }
        userRepo.addUser(username, surname, login, password, 1);
        return "redirect:/login";
    }
}
