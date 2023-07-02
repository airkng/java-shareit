package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;


    private  Booking b1;
    private  Booking b2;
    private  Booking b3;

    private  User u1;
    private  User u2;

    private  Item i1;
    private  Item i2;

    @BeforeEach
    void setUp(){
        u1 = User.builder()
                .name("TestUser")
                .email("test@mail.com")
                .build();

        u2 = User.builder()
                .name("TestUser2")
                .email("test2@mail.com")
                .build();

        i1 = Item.builder()
                .owner(u1)
                .description("test desc")
                .name("name")
                .available(true)
                .build();

        i2 = Item.builder()
                .owner(u2)
                .description("test2 desc2")
                .name("name2")
                .available(true)
                .build();

        b1 = Booking.builder()
                .booker(u2)
                .item(i1)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        b2 = Booking.builder()
                .booker(u1)
                .item(i2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        b3 = Booking.builder()
                .booker(u1)
                .item(i2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(2).plusHours(5))
                .build();

        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.persist(i1);
        entityManager.persist(i2);
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);
    }

     void loadDataToDb() {
        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.persist(i1);
        entityManager.persist(i2);
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);
    }


    @Test
    public void testFindAllByBookerId_CorrectValues_ShouldReturnList() {
       // loadDataToDb();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerId(u1.getId(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    public void testFindAllByBookerIdAndStatus_CorrectValues_ShouldReturnList() {
        //loadDataToDb();
        BookingStatus status = BookingStatus.WAITING;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndStatus(u1.getId(), status, pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    void testFindAllByBookerId_NonExistsUser_ShouldReturnEmptyList() {
        //loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerId(-10L, pageable);

        assertThat(resultPage).isEmpty();
        assertThat(resultPage.getContent()).hasSize(0);
    }

    @Test
    public void testFindAllByBookerIdAndEndBefore_CorrectValues_ShouldReturnList() {
        //loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndEndBefore(u1.getId(), LocalDateTime.now().plusDays(7), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    public void testFindAllByBookerIdAndEndBefore_CorrectValues_ShouldReturnOnlyOneBooking() {
        //loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndEndBefore(u1.getId(), LocalDateTime.now().plusDays(2).plusHours(1), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b2);
    }

    @Test
    public void testFindAllByBookerIdAndStartAfter_CorrectValues_ShouldReturnList() {
       // loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndStartAfter(u1.getId(), LocalDateTime.now(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    public void testFindAllByBookerIdAndStartAfter_CorrectValues_ShouldReturnOneBooking() {
       // loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndStartAfter(u1.getId(), LocalDateTime.now().plusDays(1).plusHours(3), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b3);
    }

    @Test
    public void testFindAllByBookerIdAndStartAfter_CorrectValues_ShouldReturnEmptyList() {
       // loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndStartAfter(u1.getId(), LocalDateTime.now().plusDays(7), pageable);

        assertThat(resultPage).isEmpty();
        assertThat(resultPage.getContent()).hasSize(0);
    }

    @Test
    public void testFindAllByBookerIdAndStartBeforeAndEndAfter_CorrectData_ShouldReturnOneBooking() {
        //loadDataToDb();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(u1.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b3);
    }

    @Test
    public void testFindAllByItemOwnerIdAndStatus_CorrectData_ShouldReturnOneBooking() {
        //loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndStatus(i1.getOwner().getId(), BookingStatus.WAITING, pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b1);
    }

    @Test
    public void testFindAllByItemOwnerIdAndEndBefore_ShouldReturnList() {
       // loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndEndBefore(i2.getOwner().getId(), LocalDateTime.now().plusDays(7), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    public void testFindAllByItemOwnerIdAndEndBefore_ShouldReturnOnlyOne() {
       // loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndEndBefore(i2.getOwner().getId(), LocalDateTime.now().plusDays(2).plusHours(1), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b2);
    }

    @Test
    public void testFindAllByItemOwnerIdAndEndBefore_ShouldReturnEmptyList() {
        //loadDataToDb();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndEndBefore(i2.getOwner().getId(), LocalDateTime.now(), pageable);

        assertThat(resultPage).isEmpty();
        assertThat(resultPage.getContent()).hasSize(0);
    }

    @Test
    public void testFindAllByItemOwnerIdAndStartAfter_ShouldReturnList() {
        //loadDataToDb();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndStartAfter(i2.getOwner().getId(), LocalDateTime.now(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(b2, b3);
    }

    @Test
    public void testFindAllByItemOwnerIdAndStartAfter_ShouldReturnOnlyOne() {
        //loadDataToDb();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndStartAfter(i2.getOwner().getId(), LocalDateTime.now().plusDays(1).plusHours(10), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b3);
    }

    @Test
    public void testFindAllByItemOwnerIdAndStartBeforeAndEndAfter_ShouldReturnOnlyOne() {
        //loadDataToDb();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> resultPage = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(i2.getOwner().getId(), LocalDateTime.now().plusDays(1).plusHours(1), LocalDateTime.now(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(b2);
    }

    @Test
    void findByItemId_CorrectValues_ShouldReturnList() {
        //loadDataToDb();
        List<Booking> bookings = bookingRepository.findByItemId(i2.getId(), Sort.by(Sort.Direction.ASC, "start"));
        assertThat(bookings).hasSize(2);
        assertThat(bookings).contains(b2,b3);
    }

    @Test
    void findByItemId_CorrectValues_ShouldReturnOnlyOne() {
        //loadDataToDb();
        List<Booking> bookings = bookingRepository.findByItemId(i1.getId(), Sort.by(Sort.Direction.ASC, "start"));
        assertThat(bookings).hasSize(1);
        assertThat(bookings).contains(b1);
    }

    @Test
    void existsByBookerIdAndEndBeforeAndStatus_ShouldExist() {
        //loadDataToDb();
        Boolean exist = bookingRepository.existsByBookerIdAndEndBeforeAndStatus(u1.getId(), LocalDateTime.now().plusDays(7), BookingStatus.WAITING);
        assertThat(exist).isTrue();
    }

    @Test
    void existsByBookerIdAndEndBeforeAndStatus_ShouldNotExist() {
        //loadDataToDb();
        Boolean exist = bookingRepository.existsByBookerIdAndEndBeforeAndStatus(-1L, LocalDateTime.now(), BookingStatus.WAITING);
        assertThat(exist).isFalse();
    }

}
