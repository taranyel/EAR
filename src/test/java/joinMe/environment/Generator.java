package joinMe.environment;

import joinMe.db.entity.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
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

    public static Address generateAddress() {
        final Flat flat = new Flat();
        flat.setCity(randomString());
        flat.setStreet(randomString());
        flat.setNumber(randomString());
        flat.setPostIndex(randomString());
        flat.setCountry(randomString());

        return flat;
    }

    public static User generateUserWithoutAddress(TestEntityManager em) {
        final User user = new User();

        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername(randomString());
        user.setPassword(Integer.toString(randomInt()));
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmail(randomString());
        user.setBirthdate(LocalDate.now());
        user.setRating(0);

        return user;
    }

    public static User generateUser(TestEntityManager em) {
        final User user = new User();
        final Address flat = generateAddress();

        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername(randomString());
        user.setPassword(Integer.toString(randomInt()));
        user.setAddress(flat);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmail(randomString());
        user.setBirthdate(LocalDate.now());
        user.setRating(0);
        flat.addResident(user);

        em.persist(user.getAddress());
        em.persist(user);
        return user;
    }

    public static User generateUser() {
        final User user = new User();
        final Address flat = generateAddress();

        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername(randomString());
        user.setPassword(Integer.toString(randomInt()));
        user.setAddress(flat);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmail(randomString());
        user.setBirthdate(LocalDate.now());
        user.setRating(0);
        user.setImagePath(randomString());
        flat.addResident(user);

        return user;
    }

    public static List<User> generateUsers(int value, TestEntityManager em) {
        List<User> buffer = new ArrayList<>();
        for (int i = 0; i < value; i++) {
            buffer.add(generateUser(em));
        }
        return buffer;
    }

    public static Trip generateTrip(User author, TestEntityManager em) {
        final Trip t = new Trip();
        t.setAuthor(author);
        t.setCountry(randomString());
        t.setTitle(randomString());
        t.setDescription(randomString());
        t.setImagePath(randomString());
        t.setCapacity(randomInt());
        t.setStartDate(LocalDate.now());
        t.setEndDate(LocalDate.now());
        t.setCreated(LocalDateTime.now());

        em.persist(t);
        return t;
    }

    public static List<Trip> generateTrips(int value, User author, TestEntityManager em) {
        List<Trip> buffer = new ArrayList<>();
        for (int i = 0; i < value; i++) {
            buffer.add(generateTrip(author, em));
        }
        return buffer;
    }

    public static Wishlist generateWishlist(User author, Trip trip, TestEntityManager em) {
        Wishlist wishlist = new Wishlist();
        wishlist.setOwner(author);
        wishlist.setTrip(trip);
        em.persist(wishlist);

        return wishlist;
    }

    public static List<Wishlist> generateMultipleWithlist(int amount, User author, Trip trip, TestEntityManager em) {
        List<Wishlist> buffer = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            buffer.add(generateWishlist(author, trip, em));
        }

        return buffer;
    }
}
