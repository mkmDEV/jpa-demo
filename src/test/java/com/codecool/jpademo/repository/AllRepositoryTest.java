package com.codecool.jpademo.repository;

import com.codecool.jpademo.entitity.Address;
import com.codecool.jpademo.entitity.Location;
import com.codecool.jpademo.entitity.School;
import com.codecool.jpademo.entitity.Student;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@ActiveProfiles("test")
class AllRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void saveOneSimple() {
        Student john = Student.builder()
                .email("John@codecool.com")
                .name("John")
                .build();

        studentRepository.save(john);

        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(1);
    }

    @Test()
    public void saveUniqueFieldTwice() throws DataIntegrityViolationException {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Student student = Student.builder()
                    .email("john@codecool.com")
                    .name("John")
                    .build();

            studentRepository.save(student);

            Student student2 = Student.builder()
                    .email("john@codecool.com")
                    .name("John")
                    .build();

            studentRepository.saveAndFlush(student2);
        });
    }

    @Test
    public void emailShouldBeNotNull() throws DataIntegrityViolationException {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Student student = Student.builder()
                    .name("John")
                    .build();

            studentRepository.save(student);
        });
    }

    @Test
    public void transientIsNotSaved() {
        Student student = Student.builder()
                .birthDate(LocalDate.of(1987, 2, 12))
                .email("john@codecool.com")
                .name("John")
                .build();
        student.calculateAge();
        assertThat(student.getAge() >= 31);

        studentRepository.save(student);
        entityManager.clear();

        List<Student> students = studentRepository.findAll();
        assertThat(students).allMatch(student1 -> student1.getAge() == 0L);
    }

    @Test
    public void addressIsPersistentWithStudent() {
        Address address = Address.builder()
                .country("Hungary")
                .city("Budapest")
                .address("Nagymezo street 44")
                .zipCode(1065)
                .build();

        Student student = Student.builder()
                .email("temp@codecool.com")
                .address(address)
                .build();

        studentRepository.save(student);

        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses)
                .hasSize(1)
                .allMatch(address1 -> address1.getId() > 0L);
    }

    @Test
    public void studentsArePersistentAndDeletedWithSchool() {
        Set<Student> students = IntStream.range(1, 10)
                .boxed()
                .map(integer -> Student.builder().email("student" + integer + "@codecool.com").build())
                .collect(Collectors.toSet());

        School school = School.builder()
                .students(students)
                .location(Location.BUDAPEST)
                .build();

        schoolRepository.save(school);

        assertThat(studentRepository.findAll())
                .hasSize(9)
                .anyMatch(student -> student.getEmail().equals("student9@codecool.com"));

        schoolRepository.deleteAll();

        assertThat(studentRepository.findAll())
                .hasSize(0);
    }

    @Test
    public void findByNameStartingWithOrBirthDateBetween() {
        Student john = Student.builder()
                .email("John@codecool.com")
                .name("John")
                .build();

        Student jane = Student.builder()
                .email("jane@codecool.com")
                .name("Jane")
                .build();

        Student martha = Student.builder()
                .email("martha@codecool.com")
                .name("Martha")
                .build();

        Student peter = Student.builder()
                .email("peter@codecool.com")
                .birthDate(LocalDate.of(2010, 10, 3))
                .build();

        Student steve = Student.builder()
                .email("steve@codecool.com")
                .birthDate(LocalDate.of(2011, 12, 5))
                .build();

        studentRepository.saveAll(Lists.newArrayList(john, jane, martha, peter, steve));

        List<Student> filteredStudents = studentRepository.findByNameStartingWithOrBirthDateBetween("J",
                LocalDate.of(2009, 1, 1),
                LocalDate.of(2011, 1, 1));

        assertThat(filteredStudents)
                .containsExactlyInAnyOrder(john, jane, peter);
    }

    @Test
    public void findAllCountry() {

        Student first = Student.builder()
                .email("first@codecool.com")
                .address(Address.builder().country("Hungary").build())
                .build();

        Student second = Student.builder()
                .email("second@codecool.com")
                .address(Address.builder().country("Poland").build())
                .build();

        Student third = Student.builder()
                .email("third@codecool.com")
                .address(Address.builder().country("Poland").build())
                .build();

        Student fourth = Student.builder()
                .email("fourth@codecool.com")
                .address(Address.builder().country("Hungary").build())
                .build();

        studentRepository.saveAll(Lists.newArrayList(first, second, third, fourth));

        List<String> allCountry = studentRepository.findAllCountry();

        assertThat(allCountry)
                .hasSize(2)
                .containsOnlyOnce("Hungary", "Poland");

    }

    @Test
    public void updateAllToUsaByStudentName() {
        Address address1 = Address.builder()
                .country("Hungary")
                .build();

        Address address2 = Address.builder()
                .country("Poland")
                .build();

        Address address3 = Address.builder()
                .country("Germany")
                .build();

        Student student = Student.builder()
                .name("temp")
                .email("temp@temp.hu")
                .address(address1)
                .build();

        studentRepository.save(student);
        addressRepository.save(address2);
        addressRepository.save(address3);

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .noneMatch(address -> address.getCountry().equals("USA"));

        int updatedRows = addressRepository.updateAllToUsaByStudentName("temp");
        assertThat(updatedRows).isEqualTo(1);

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .anyMatch(address -> address.getCountry().equals("USA"));
    }
}