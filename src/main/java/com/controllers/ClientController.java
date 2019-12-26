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
        List<Order> orders = orderRepo.getUsersOrders(user.getId());
        model.addAttribute("allTables", tables);
        model.addAttribute("dishesList", dishes);
        model.addAttribute("userOrders", orders);
        model.addAttribute("error1", error1);
        model.addAttribute("error2", error2);
        Logger.LOGG("В систему вошел " + user.getId() + user.getName() + user.getSurname());
        return "clientPage";
    }

    @PostMapping("/newOrder")
    public String makeNewOrder(@AuthenticationPrincipal User user,
                               @RequestParam int dishId,
                               @RequestParam int tableId){
        error1 = null;
        if(tableId == 0){
            Logger.LOGG("При создании нового заказа пользватель " + user.getId() + user.getName()
                    + user.getSurname() + " ввел не все данные");
            error1 = "введите все необходимые данные";
            return "redirect:/client";
        } else {
            Random random = new Random();
            int newOrderId = random.nextInt(100000000 + 1);
            while (!orderRepo.isOrderPresent(newOrderId)) {
                newOrderId = random.nextInt(100000000 + 1);
            }
            orderRepo.reserveOrder(newOrderId, tableId, user.getId());
            tableRepo.reserveTable(tableId, user.getId());
            if (dishId != 0) {
                dishOrderRepo.addDishToOrder(newOrderId, dishId);
            }
        }
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
        "был создан новый заказ на столик" + tableId);
        return "redirect:/client";
    }

    @PostMapping("/addToExistingOrder")
    public String addToExistingOrder(@AuthenticationPrincipal User user,
                                     @RequestParam int dishId,
                                     @RequestParam int orderId){
        error2 = null;
        if(dishId == 0 || orderId == 0){
            Logger.LOGG("При добавлении позиций в существующем заказе, пользователь " + user.getId() + user.getName()
                    + user.getSurname() + " ввел не все данные");
            error2 = "введите все необходимые данные для заказа";
            return "redirect:/client";
        } else {
            if (dishOrderRepo.isDishPresent(orderId, dishId)) {
                dishOrderRepo.increaseRepeats(orderId, dishId);
            } else {
                dishOrderRepo.addDishToOrder(orderId, dishId); }
        }
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                "было добавлено в заказ № " + orderId + "блюдо " + dishId);
        return "redirect:/client";
    }

    @PostMapping("/deleteDish")
    public String deleteDish(@AuthenticationPrincipal User user,
                             @RequestParam int dishId,
                             @RequestParam int orderId){
        if (dishOrderRepo.getRepeats(orderId, dishId) > 1){
            dishOrderRepo.decreaseRepeats(orderId, dishId);
        } else {
            dishOrderRepo.deleteDishFromOrder(orderId, dishId);
        }
        if(dishOrderRepo.getSumOfOrderDishes(orderId) == 0){
            tryToFreeTable(orderId);
        }
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                "было удалено из заказа № " + orderId + " блюдо с идентификатором № " + dishId);
        return "redirect:/client";
    }

    @PostMapping("/deleteOrder")
    public String deleteOrder(@AuthenticationPrincipal User user,
                              @RequestParam int orderId) {
        dishOrderRepo.deleteAllDishesFromOrder(orderId);
        tryToFreeTable(orderId);
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
        "был удален заказ № " + orderId);
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
