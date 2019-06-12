package com.codecool.jpademo.repository;

import com.codecool.jpademo.entitity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
}
