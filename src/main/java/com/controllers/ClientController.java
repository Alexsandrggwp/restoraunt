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

    private String error;

    @GetMapping("/")
    public String redir(){
        return "redirect:/client";
    }

    @GetMapping("/client")
    public String getTables(@AuthenticationPrincipal User user, Model model){
        if(user.getRole().equals(Role.chef) || user.getRole().equals(Role.cooker) || user.getRole().equals(Role.waiter)) {
            return "redirect:/employee";
        }
        List<Table> tables = tableRepo.getAllTables();
        List<Dish> dishes = dishRepo.getAllDishes();
        List<Order> orders = orderRepo.getUsersOrders(1);
        model.addAttribute("allTables", tables);
        model.addAttribute("dishesList", dishes);
        model.addAttribute("userOrders", orders);
        model.addAttribute("error", error);
        return "clientPage";
    }

    @PostMapping("/order")
    public String makeOrder(@RequestParam int dishId, @RequestParam int tableId, @RequestParam int orderId, Model model){
        error = null;
        if(dishId == 0 || tableId == 0){
            error = "введите все необходимые данные для заказа";
            model.addAttribute("error", error);
            return "redirect:/client";
        } else {
            if (orderId == 0) {
                Random random = new Random();
                int newOrderId = random.nextInt(100000000 + 1);
                while (!orderRepo.isOrderPresent(newOrderId)) {
                    newOrderId = random.nextInt(100000000 + 1);
                }
                orderRepo.reserveOrder(newOrderId, tableId, 1);
                tableRepo.reserveTable(tableId, 1);
                dishOrderRepo.addDishToOrder(newOrderId, dishId);
            } else {
                if (dishOrderRepo.isDishPresent(orderId, dishId)) {
                    dishOrderRepo.increaseRepeats(orderId, dishId);
                } else {
                    dishOrderRepo.addDishToOrder(orderId, dishId);
                }
            }
        }
        model.addAttribute("error", error);
        return "redirect:/client";
    }

    @PostMapping("/deleteDish")
    public String deleteDish(@RequestParam int dishId, @RequestParam int orderId){
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
