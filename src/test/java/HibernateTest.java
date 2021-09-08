import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HibernateTest {
    private static SessionFactory sessionFactory;
    private static Session session;
    private static User user;
    private static User user2;
    private static Post post;
    private static Post post2;
    private static PostComment postComment;

    @Before
    public void setUp() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
        session = sessionFactory.openSession();
        Date today = new Date();
        user = new User(true, today, "Sergey Belyakov", "qwerty", "serg@belykov.ru");
        user2 = new User(true, today, "Petr Cheh", "pass0000", "cheh1972@yahoo.com");
        post = new Post(true, ModerationStatus.ACCEPTED, user, today, "tittle", "some text", 2);
        post2 = new Post(true, ModerationStatus.ACCEPTED, user2, today, "tittle", "some interesting text", 10);
        List<User> moderators = new ArrayList<>();
        moderators.add(user);
        moderators.add(user2);
        post.setModerators(moderators);
        post2.setModerators(moderators);
        postComment = new PostComment(post, user, today, "I like it");
        session.save(user);
        session.save(user2);
        session.save(post);
        session.save(post2);
        session.save(postComment);
    }

    @Test
    public void testManytoManyRelationShip () {
        Query query = session.createQuery("From User Where name = :name");
        query.setParameter("name", user.getName());
        int count = ((User) query.getResultList().get(0)).getModeratedPosts().size();
        query = session.createQuery("From User Where name = :name");
        query.setParameter("name", user2.getName());
        count += ((User) query.getResultList().get(0)).getModeratedPosts().size();
        Assert.assertEquals(4, count);
    }

    @Test
    public void testOneToOneRelationShip () {
        Query query = session.createQuery("From User Where name = :name");
        query.setParameter("name", user.getName());
        User testUser = (User) query.getResultList().get(0);
        query = session.createQuery("From PostComment Where user.id = :id");
        query.setParameter("id", testUser.getId());
        PostComment postComment = (PostComment) query.getResultList().get(0);
        Assert.assertEquals("I like it", postComment.getText());
    }

    @After
    public void tearDown() {
        Transaction transaction = session.beginTransaction();
        session.delete(postComment);
        session.delete(post);
        session.delete(post2);
        session.delete(user);
        session.delete(user2);
        transaction.commit();
        sessionFactory.close();
        session.close();
    }

}
