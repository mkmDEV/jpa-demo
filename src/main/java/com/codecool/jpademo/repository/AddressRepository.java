package com.codecool.jpademo.repository;

import com.codecool.jpademo.entitity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("UPDATE Address a SET a.country = 'USA' WHERE a.id IN " +
            "(SELECT s.address.id FROM student s WHERE s.name LIKE :name)")
    @Modifying(clearAutomatically = true)
    int updateAllToUsaByStudentName(@Param("name") String name);
}
