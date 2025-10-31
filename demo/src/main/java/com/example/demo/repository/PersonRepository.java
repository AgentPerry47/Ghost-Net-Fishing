package com.example.demo.repository;

import com.example.demo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

// Diese Schnittstelle verwaltet die Datenbank-Zugriffe für Person-Objekte
public interface PersonRepository extends JpaRepository<Person, Long> {}
