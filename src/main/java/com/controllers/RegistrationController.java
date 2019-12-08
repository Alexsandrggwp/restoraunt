package com.controllers;

import com.database.UserRepo;
import com.entities.Role;
import com.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sound.sampled.Line;
import java.util.List;

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
    public String addUser(@RequestParam String username,
                          @RequestParam String surname,
                          @RequestParam String login,
                          @RequestParam String password,
                          Model model){
        message = null;
        if(username.isEmpty() || surname.isEmpty() || login.isEmpty() || password.isEmpty()){
            message = "введены не все данные";
            model.addAttribute("message", message);
            return "redirect:/registration";
        }
        message = null;
        if(((User)userRepo.loadUserByUsername(login)).getId() != 0) {
            message = "пользователь уже существует";
            model.addAttribute("message", message);
            return "redirect:/registration";
        }
        userRepo.addUser(username, surname, login, password, 1);
        return "redirect:/login";
    }

    @GetMapping("/employeeRegistration")
    public String getEmployeeRegistration(Model model){
        List<Role> roles = userRepo.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("message", message);
        return "employeeRegistration";
    }

    @PostMapping("/employeeRegistration")
    public String addEmployee(@RequestParam String username,
                          @RequestParam String surname,
                          @RequestParam String login,
                          @RequestParam String password,
                          @RequestParam String role,
                          Model model){
        message = null;
        if(username.isEmpty() || surname.isEmpty() || login.isEmpty() || password.isEmpty()){
            message = "введены не все данные";
            model.addAttribute("message", message);
            return "redirect:/registration";
        }
        message = null;
        if(((User)userRepo.loadUserByUsername(login)).getId() != 0) {
            message = "пользователь уже существует";
            model.addAttribute("message", message);
            return "redirect:/registration";
        }
        int roleId = userRepo.convertRoleNameToId(role);
        userRepo.addUser(username, surname, login, password, roleId);
        return "redirect:/employee";
    }
}
