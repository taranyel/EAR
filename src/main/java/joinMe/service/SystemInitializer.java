package joinMe.service;

import jakarta.annotation.PostConstruct;
import joinMe.db.entity.Address;
import joinMe.db.entity.Flat;
import joinMe.db.entity.Role;
import joinMe.db.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Calendar;
import java.util.Date;

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
        txTemplate.execute((status) -> {
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
        User admin = new User();
        admin.setEmail(ADMIN_USERNAME);
        admin.setUsername(ADMIN_USERNAME);
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setPassword("admin");
        admin.setRole(Role.ADMIN);
        admin.setAddress(getAddress());
        admin.setBirthdate(new Date(2004, Calendar.MARCH, 4));
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        userService.persist(admin);
    }

    private Address getAddress() {
        Address address = new Flat();
        address.setStreet("Street");
        address.setCity("City");
        address.setCountry("Country");
        address.setNumber("1");
        address.setPostIndex("12345");
        addressService.persist(address);
        return address;
    }
}
