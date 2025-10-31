package com.example.demo.repository;

import com.example.demo.model.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;

// Diese Schnittstelle verwaltet die Datenbank-Zugriffe für GhostNet-Objekte
public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {}

