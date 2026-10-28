package com.example.archtests.fixtures.spring.invalid.web;

@org.springframework.stereotype.Controller public class CustomerController { com.example.archtests.fixtures.spring.invalid.persistence.CustomerRepository customers; @org.springframework.transaction.annotation.Transactional void update() {} }
