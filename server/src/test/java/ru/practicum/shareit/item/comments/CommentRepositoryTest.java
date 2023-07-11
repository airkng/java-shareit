package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testFindByItemId() {
        // Create test data
        User user = User.builder().name("TestUser").email("test@mail.com").build();
        entityManager.persist(user);

        User user1 = User.builder().name("TestUser1").email("test1@mail.com").build();
        entityManager.persist(user1);

        Item item1 = Item.builder().name("t1").description("t2").available(true).owner(user).build();
        entityManager.persist(item1);

        Item item2 = Item.builder()
                .name("testing")
                .description("testttting")
                .available(true)
                .owner(user).build();
        entityManager.persist(item2);


        Comment comment1 = Comment.builder()
                .text("Comment 1")
                .item(item1)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment 2")
                .item(item2)
                .created(LocalDateTime.now())
                .build();

        Comment comment3 = Comment.builder()
                .text("Comment 3")
                .item(item1)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.flush();

        List<Comment> comments = commentRepository.findByItemId(item1.getId());

        assertEquals(2, comments.size());
        assertEquals("Comment 1", comments.get(0).getText());
        assertEquals("Comment 3", comments.get(1).getText());
    }
}
