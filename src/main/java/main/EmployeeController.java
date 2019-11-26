package main;

import database.DishOrderRepo;
import database.OrderRepo;
import database.TableRepo;
import database.UserRepo;
import entities.Dish;
import entities.Order;
import entities.Table;
import entities.User;
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

    @GetMapping("/employee")
    public String getTables(Model model){
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
        return "employeePage";
    }

    @PostMapping("/assignWaiter")
    public String setWaiter(@RequestParam int waiterId,@RequestParam int orderId){
        userRepo.addWaiterToOrder(orderId, waiterId);
        return "redirect:/employee";
    }

    @PostMapping("/completeOrder")
    public String completeOrder(@RequestParam int orderId, @RequestParam int tableId){
        dishOrderRepo.deleteAllDishesFromOrder(orderId);
        orderRepo.deleteOrder(orderId);
        tableRepo.setTableFree(tableId);
        return "redirect:/employee";
    }
}
