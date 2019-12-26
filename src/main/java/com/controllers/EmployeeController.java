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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class EmployeeController {

    private TableRepo tableRepo = new TableRepo();
    private OrderRepo orderRepo = new OrderRepo();
    private DishOrderRepo dishOrderRepo = new DishOrderRepo();
    private UserRepo userRepo = new UserRepo();
    private DishRepo dishRepo = new DishRepo();

    private String error;
    private String error1;
    private String error2;
    private String error3;

    private List<Order> orders;

    @GetMapping("/employee")
    public String getEmployeePage(@AuthenticationPrincipal User user,
                                  Model model){

        if (user.getRole().equals(Role.client)) {
            return "redirect:/client";
        }

        List<Dish> allOrderedDishes = dishOrderRepo.getAllOrderedDishes();
        List<User> waitersList = userRepo.getAllWaiters();
        List<Dish> dishes = dishRepo.getAllDishes();

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
        model.addAttribute("dishesList", dishes);
        model.addAttribute("allTables", allTables);
        model.addAttribute("waitersList", waitersList);
        model.addAttribute("allOrderedDishes", allOrderedDishes);
        model.addAttribute("AllInactiveEmployeeOrders", orders);
        model.addAttribute("error", error);
        model.addAttribute("error1", error1);
        model.addAttribute("error1", error2);
        model.addAttribute("error3", error3);
        Logger.LOGG("В систему вошел " + user.getId() + user.getName() + user.getSurname());
        return "employeePage";
    }

    @PostMapping("/assignWaiter")
    public String setWaiter(@AuthenticationPrincipal User user,
                            @RequestParam int waiterId,
                            @RequestParam int orderId){
        if (waiterId == 0){
            error = "Вы не указали официанта!!!";
            Logger.LOGG("При попытке привязки официанта к заказу, пользователь " + user.getId() + user.getName() + user.getSurname() +
            " не указал официанта");
            return "redirect:/employee";
        }
        error = null;
        userRepo.addWaiterToOrder(waiterId, orderId);
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
        " был привязан официант с идентификатором " + waiterId + " к заказу № " + orderId);
        return "redirect:/employee";
    }

    @PostMapping("/completeOrder")
    public String completeOrder(@AuthenticationPrincipal User user,
                                @RequestParam int orderId,
                                @RequestParam int tableId){
        orderRepo.deleteOrder(orderId);
        tableRepo.setTableFree(tableId);
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                " был завершен заказа № " + orderId);
        return "redirect:/employee";
    }

    @PostMapping("/addDish")
    public String addDish(@AuthenticationPrincipal User user,
                          @RequestParam String dishName,
                          @RequestParam int dishCost){
        if (dishName.isEmpty() || dishCost == 0) {
            error3 = "вы указали не все данные";
            return "redirect:/employee";
        } else if(dishCost < 1) {
            error3 = "вы указали отрицательную цену";
            return "redirect:/employee";
        }
        error3 = null;
        dishRepo.addDish(dishName, dishCost);
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                "было добавлено новое блюдо под названием " + dishName);
        return "redirect:/employee";
    }

    @PostMapping("/makeImmediateOrder")
    public String makeOrder(@AuthenticationPrincipal User user,
                            @RequestParam int dishId,
                            @RequestParam int tableId){
        error1 = null;
        if(tableId == 0){
            error1 = "введите все необходимые данные";
            return "redirect:/employee";
        } else {
            Random random = new Random();
            int newOrderId = random.nextInt(100000000 + 1);
            while (!orderRepo.isOrderPresent(newOrderId)) {
                newOrderId = random.nextInt(100000000 + 1);
            }
            orderRepo.reserveOrder(newOrderId, tableId, 10);
            tableRepo.reserveTable(tableId, 10);
            if (dishId != 0) {
                dishOrderRepo.addDishToOrder(newOrderId, dishId);
            }
        }
        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                "был принят заказ на столик № " + tableId);
        return "redirect:/employee";
    }

    @PostMapping("/getAllInactiveEmployeeOrders")
    public String getAllInactiveEmployeeOrders(@AuthenticationPrincipal User user,
                                               @RequestParam int employeeId){
        error2 = null;

        if (employeeId == 0) {
            error2 = "вы не выбрали официанта";
            return "redirect:/employee";
        }

        orders = orderRepo.getAllInactiveEmployeeOrders(employeeId);

        Logger.LOGG("Пользователем " + user.getId() + user.getName() + user.getSurname() +
                " была просмотрена история выполненых заказов официанта с идентификатором № " + employeeId);

        return "redirect:/employee";
    }
}
