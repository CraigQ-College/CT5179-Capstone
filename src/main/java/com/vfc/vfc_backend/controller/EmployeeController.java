package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.Employee;
import com.vfc.vfc_backend.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService theEmployeeService) {

        employeeService = theEmployeeService;
    }
    //add mapping for "/list"

    @GetMapping("/list")
    public String listEmployees(Model theModel){

        // get the employees from the db
        List<Employee> theEmployees = employeeService.findAll();

        // add to the spring model
        theModel.addAttribute("employees",theEmployees);

        return "list-employees";

    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        // create model attribute to bind form data
        Employee theEmployee = new Employee();
        theModel.addAttribute("employee",theEmployee);

        return "employee-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("employeeId")int theId,Model theModel){

        // get the employee from the service

        //set employee in the model to prepopulate the form

        //send over to our form
        Employee theEmployee = employeeService.findById(theId);
        theModel.addAttribute("employee",theEmployee);

        return "employee-form";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee theEmployee){
        // save the employee
        employeeService.save(theEmployee);
        //use a redirect to prevent duplicate submiss=ions
        return "redirect:/employees/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("employeeId")int theId,Model theModel){

        //delete the employee
        employeeService.deleteById(theId);
        //redirect to the /employees/list
        return "redirect:/employees/list";
    }
}
