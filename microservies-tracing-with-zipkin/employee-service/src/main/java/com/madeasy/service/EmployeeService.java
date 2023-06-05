/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.madeasy.service;

import com.madeasy.entity.Department;
import com.madeasy.error.EmployeeNotFoundException;
import com.madeasy.entity.Employee;
import com.madeasy.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import com.madeasy.response.EmployeeWithDepartment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author PABITRA
 */

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;
    @Autowired
    private EmployeeWithDepartment employeeWithDepartment;
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee findByEmployeeId(Integer id) throws EmployeeNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundException("Department Not Found");
        }
        return employee.get();
    }

    public Employee findByEmployeeName(String name) {
        return employeeRepository.findByName(name);
    }

    public Employee findByEmployeeAge(Integer age) {
        return employeeRepository.findByAge(age);
    }

    public Employee findByEmployeeNameAndAge(String name, Integer age) {
        return employeeRepository.findByEmployeeNameAndAge(name, age);
    }


    public ResponseEntity<Object> getEmployeeWithDepartmentUsingRestTemplate(Integer id) {

        Department department = restTemplate.getForObject("http://localhost:8080/department/id/" + id, Department.class);
        Optional<Employee> optionalEmployee = Optional.ofNullable(employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("error")));
        Employee employee = optionalEmployee.get();
        employeeWithDepartment.setDepartment(department);
        employeeWithDepartment.setEmployee(employee);
        return ResponseEntity.ok(employeeWithDepartment);
    }

    public ResponseEntity<Object> getEmployeeWithDepartmentUsingWebClient(Integer id) {
        Department department = webClient.get()
                .uri("http://localhost:8080/department/id/" + id)
                .retrieve()
                .bodyToMono(Department.class)
                .block();
        Optional<Employee> optionalEmployee = Optional.ofNullable(employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("error")));
        employeeWithDepartment.setDepartment(department);
        employeeWithDepartment.setEmployee(optionalEmployee.get());
        return ResponseEntity.ok(employeeWithDepartment);
    }
}
