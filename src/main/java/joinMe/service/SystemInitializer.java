package joinMe.service;

import jakarta.annotation.PostConstruct;
import joinMe.db.entity.Address;
import joinMe.db.entity.Flat;
import joinMe.db.entity.Role;
import joinMe.db.entity.User;
import joinMe.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

@Component
public class SystemInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    /**
     * Default admin username
     */
    private static final String ADMIN_USERNAME = "admin";

    private final UserService userService;

    private final AddressService addressService;

    private final PlatformTransactionManager txManager;

    @Autowired
    public SystemInitializer(UserService userService,
                             PlatformTransactionManager txManager,
                             AddressService addressService) {
        this.userService = userService;
        this.txManager = txManager;
        this.addressService = addressService;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute(status -> {
            generateAdmin();
            return null;
        });
    }

    /**
     * Generates an admin account if it does not already exist.
     */
    private void generateAdmin() {
        if (userService.exists(ADMIN_USERNAME)) {
            return;
        }
        Address address = createAddress();

        User admin = createUser();
        admin.setAddress(address);

        address.addResident(admin);

        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        userService.persist(admin);
    }

    private Address createAddress() {
        return Flat.builder()
                .street("Street")
                .city("City")
                .country("Country")
                .number("1")
                .postIndex("12345")
                .residents(new ArrayList<>())
                .build();
    }

    private User createUser() {
        return User.builder()
                .email(ADMIN_USERNAME)
                .username(ADMIN_USERNAME)
                .firstName("System")
                .lastName("Administrator")
                .password("admin")
                .role(Role.ADMIN)
                .trips(new ArrayList<>())
                .joinRequests(new ArrayList<>())
                .wishlists(new ArrayList<>())
                .rating(0)
                .status(Constants.DEFAULT_ACCOUNT_STATUS)
                .imagePath("")
                .complaints(new ArrayList<>())
                .attendlists(new ArrayList<>())
                .birthdate(LocalDate.of(2004, Month.APRIL, 5))
                .build();
    }
}
