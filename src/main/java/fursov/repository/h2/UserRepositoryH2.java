package fursov.repository.h2;

import fursov.model.User;
import java.util.List;
import static fursov.util.JpaUtil.close;
import static fursov.util.JpaUtil.init;
import static fursov.util.JpaUtil.performReturningWithinPersistenceContext;
import static fursov.util.JpaUtil.performWithinPersistenceContext;

public class UserRepositoryH2 {
    public static void main(String[] args) {
        init("BasicEntitiesH2");

        User user = new User();
        user.setName("Sample Name");
        user.setEmail("sample@example.com");
        System.out.printf("User before save: %s%n", user);

        saveUser(user);
        System.out.printf("Stored user: %s%n", user);

        printAllUsers();

        User foundUser = findUserByEmail(user.getEmail());
        System.out.printf("User found by email (%s): %s%n", user.getEmail(), foundUser);

        user.setName("UPDATED");
        user = updateUser(user);
        System.out.printf("Updated user: %s%n", user);

        printAllUsers();

        removeUser(user);
        printAllUsers();

        close();
    }

    public static void saveUser(User user) {
        performWithinPersistenceContext(em -> em.persist(user));
    }

    public static void printAllUsers() {
        System.out.printf("All users: %s%n", findAllUsers());
    }

    public static List<User> findAllUsers() {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("SELECT u FROM User u", User.class).getResultList()
        );
    }

    public static User findUserByEmail(String email) {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                        .setParameter("email", email)
                        .getSingleResult()
        );
    }

    private static User updateUser(User user) {
        return performReturningWithinPersistenceContext(em -> em.merge(user));
    }

    private static void removeUser(User user) {
        performWithinPersistenceContext(em -> {
            User managedUser = em.merge(user);
            em.remove(managedUser);
        });
    }
}
