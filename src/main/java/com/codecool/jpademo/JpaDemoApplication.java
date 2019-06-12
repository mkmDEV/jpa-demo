package com.codecool.jpademo;

import com.codecool.jpademo.entitity.Address;
import com.codecool.jpademo.entitity.Location;
import com.codecool.jpademo.entitity.School;
import com.codecool.jpademo.entitity.Student;
import com.codecool.jpademo.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class JpaDemoApplication {

//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private AddressRepository addressRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaDemoApplication.class, args);
    }

    @Bean
    @Profile("production")
    public CommandLineRunner init() {
        return args -> {
//            Student john = Student.builder()
//                    .email("john@email.com")
//                    .name("John")
//                    .address(Address.builder().city("Warsaw").country("Poland").build())
//                    .phoneNumber("555-6666")
//                    .phoneNumber("555-7777")
//                    .phoneNumber("555-8888")
//                    .birthDate(LocalDate.of(1980, 3, 5))
//                    .build();
//            john.calculateAge();
//
//            studentRepository.save(john);

            Address address1 = Address.builder()
                    .address("Nagymezo street 44")
                    .city("Budapest")
                    .country("Hungary")
                    .build();

            Address address2 = Address.builder()
                    .address("Alkotmany street 20")
                    .city("Budapest")
                    .country("Hungary")
                    .build();

            Student rob = Student.builder()
                    .email("rob@codecool.com")
                    .name("Rob")
                    .birthDate(LocalDate.of(1990, 8, 22))
                    .phoneNumbers(Arrays.asList("555-6666", "5555-7777"))
                    .address(address1)
                    .build();

            Student barbara = Student.builder()
                    .email("barbara@codecool.com")
                    .name("Barbara")
                    .birthDate(LocalDate.of(1994, 7, 8))
                    .phoneNumbers(Arrays.asList("555-1111", "555-2222"))
                    .address(address2)
                    .build();

            School school = School.builder()
                    .location(Location.BUDAPEST)
                    .name("CodeCool Budapest")
                    .student(rob)
                    .student(barbara)
                    .build();

            barbara.setSchool(school);
            rob.setSchool(school);

            schoolRepository.save(school);
        };
    }
}
