package com.controllers;

import com.database.*;
import com.entities.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmployeeController {

    private TableRepo tableRepo = new TableRepo();
    private OrderRepo orderRepo = new OrderRepo();
    private DishOrderRepo dishOrderRepo = new DishOrderRepo();
    private UserRepo userRepo = new UserRepo();
    private DishRepo dishRepo = new DishRepo();

    private String error;

    @GetMapping("/employee")
    public String getEmployeePage(@AuthenticationPrincipal User user, Model model){

        if (user.getRole().equals(Role.client)) {
            return "redirect:/client";
        }

        List<Dish> allOrderedDishes = dishOrderRepo.getAllOrderedDishes();
        List<User> waitersList = userRepo.getAllWaiters();

        List<Table> allTables = tableRepo.getAllTables();
        for (Table table : allTables){
            List<Order> tablesOrders = orderRepo.getAllTableOrders(table.getId());

            for (Order order : tablesOrders) {
                order.setDishes(dishOrderRepo.getDishesOfOrder(order.getId()));
            }

            for (Order order: tablesOrders) {
                order.setClient(userRepo.getClientOfOrder(order.getId()));
            }

            for (Order order: tablesOrders) {
                table.addOrder(order);
            }

            for (Order order: tablesOrders) {
                order.setWaiter(userRepo.getWaiterOfOrder(order.getId()));
            }
        }
        model.addAttribute("allTables", allTables);
        model.addAttribute("waitersList", waitersList);
        model.addAttribute("allOrderedDishes", allOrderedDishes);
        model.addAttribute("error", error);
        return "employeePage";
    }

    @PostMapping("/assignWaiter")
    public String setWaiter(@RequestParam int waiterId,@RequestParam int orderId){
        if (waiterId == 0){
            error = "Вы не указали официанта!!!";
            return "redirect:/employee";
        }
        userRepo.addWaiterToOrder(waiterId, orderId);
        return "redirect:/employee";
    }

    @PostMapping("/completeOrder")
    public String completeOrder(@RequestParam int orderId, @RequestParam int tableId){
        dishOrderRepo.deleteAllDishesFromOrder(orderId);
        orderRepo.deleteOrder(orderId);
        tableRepo.setTableFree(tableId);
        return "redirect:/employee";
    }

    @PostMapping("/addDish")
    public String addDish(@RequestParam String dishName, @RequestParam int dishCost){
        if (dishName.isEmpty() || dishCost == 0) {
            error = "вы указали не все данные";
            return "redirect:/employee";
        }
        dishRepo.addDish(dishName, dishCost);
        return "redirect:/employee";
    }
}
