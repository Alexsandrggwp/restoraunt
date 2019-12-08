package com.controllers;

import com.database.DishOrderRepo;
import com.database.DishRepo;
import com.database.OrderRepo;
import com.database.TableRepo;
import com.entities.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;

@Controller
public class ClientController {

    private TableRepo tableRepo = new TableRepo();
    private OrderRepo orderRepo = new OrderRepo();
    private DishRepo dishRepo = new DishRepo();
    private DishOrderRepo dishOrderRepo = new DishOrderRepo();

    private String error1;
    private String error2;

    @GetMapping("/")
    public String redir(){
        return "redirect:/client";
    }

    @GetMapping("/client")
    public String getTables(@AuthenticationPrincipal User user,
                            Model model){
        if(user.getRole().equals(Role.chef) || user.getRole().equals(Role.cooker) || user.getRole().equals(Role.waiter)) {
            return "redirect:/employee";
        }
        List<Table> tables = tableRepo.getAllTables();
        List<Dish> dishes = dishRepo.getAllDishes();
        List<Order> orders = orderRepo.getUsersOrders(1);
        model.addAttribute("allTables", tables);
        model.addAttribute("dishesList", dishes);
        model.addAttribute("userOrders", orders);
        model.addAttribute("error1", error1);
        model.addAttribute("error2", error2);
        return "clientPage";
    }

    @PostMapping("/newOrder")
    public String makeNewOrder(@RequestParam int dishId,
                               @RequestParam int tableId){
        error1 = null;
        if(tableId == 0){
            error1 = "введите все необходимые данные";
            return "redirect:/client";
        } else {
            Random random = new Random();
            int newOrderId = random.nextInt(100000000 + 1);
            while (!orderRepo.isOrderPresent(newOrderId)) {
                newOrderId = random.nextInt(100000000 + 1);
            }
            orderRepo.reserveOrder(newOrderId, tableId, 1);
            tableRepo.reserveTable(tableId, 1);
            if (dishId != 0) {
                dishOrderRepo.addDishToOrder(newOrderId, dishId);
            }
        }
        return "redirect:/client";
    }

    @PostMapping("/addToExistingOrder")
    public String addToExistingOrder(@RequestParam int dishId,
                                     @RequestParam int orderId){
        error2 = null;
        if(dishId == 0 || orderId == 0){
            error2 = "введите все необходимые данные для заказа";
            return "redirect:/client";
        } else {
            if (dishOrderRepo.isDishPresent(orderId, dishId)) {
                dishOrderRepo.increaseRepeats(orderId, dishId);
            } else {
                dishOrderRepo.addDishToOrder(orderId, dishId); }
        }
        return "redirect:/client";
    }

    @PostMapping("/deleteDish")
    public String deleteDish(@RequestParam int dishId,
                             @RequestParam int orderId){
        if (dishOrderRepo.getRepeats(orderId, dishId) > 1){
            dishOrderRepo.decreaseRepeats(orderId, dishId);
        } else {
            dishOrderRepo.deleteDishFromOrder(orderId, dishId);
        }
        if(dishOrderRepo.getSumOfOrderDishes(orderId) == 0){
            tryToFreeTable(orderId);
        }
        return "redirect:/client";
    }

    @PostMapping("/deleteOrder")
    public String deleteOrder(@RequestParam int orderId) {
        dishOrderRepo.deleteAllDishesFromOrder(orderId);
        tryToFreeTable(orderId);
        return "redirect:/client";
    }

    private void tryToFreeTable(int orderId){
        if(tableRepo.getSumOfTableOrders(orderRepo.getOrderTableId(orderId)) > 1){
            orderRepo.deleteOrder(orderId);
        } else {
            tableRepo.setTableFree(orderRepo.getOrderTableId(orderId));
            orderRepo.deleteOrder(orderId);
        }
    }
}
