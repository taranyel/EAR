package joinMe.environment;

import joinMe.db.dao.AddressDao;
import joinMe.db.entity.*;
import joinMe.service.UserService;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.*;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String randomString() {
        Random random = new Random();
        int length = 10;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }

        return sb.toString();
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        final Flat flat = new Flat();
        flat.setCity(randomString());
        flat.setStreet(randomString());
        flat.setNumber(randomString());
        flat.setPostIndex(randomString());
        flat.setCountry(randomString());

        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
//        user.setUsername("username" + randomInt() + "@kbss.felk.cvut.cz");
        user.setUsername(randomString());
        user.setPassword(Integer.toString(randomInt()));
        user.setAddress(flat);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmail(randomString());
        user.setBirthdate(new Date());
        user.setRating(0);
        flat.addResident(user);
        return user;
    }

    public static List<User> generateUsers(int value, TestEntityManager em, AddressDao dao, UserService service) {
        List<User> buffer = new ArrayList<>();
        for (int i = 0; i < value; i++) {
            buffer.add(generateUser());
            User user = buffer.get(i);
            em.persist(user.getAddress());
            em.persist(user);
            dao.persist(user.getAddress());
            service.persist(user);
        }
        return buffer;
    }

    public static Trip generateTrip(User author) {
        final Trip t = new Trip();
        t.setAuthor(author);
        t.setCountry(randomString());
        t.setTitle(randomString());
        t.setDescription(randomString());
        t.setImagePath(randomString());
        t.setCapacity(randomInt());
        t.setStartDate(new Date());
        t.setEndDate(new Date());
        t.setCreated(LocalDateTime.now());
        return t;
    }
}
