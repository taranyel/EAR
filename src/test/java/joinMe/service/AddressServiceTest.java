package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AddressDao;
import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import joinMe.environment.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
public class AddressServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AddressService addressService;

    @SpyBean
    private AddressDao addressDao;

    private Address address;

    private User user;

    @BeforeEach
    public void setUp() {
        user = Generator.generateUserWithoutAddress(em);
        address = Generator.generateAddress();
        em.persist(address);
        user.setAddress(address);
        em.persist(user);
    }

    @Test
    public void addResidentTest() {
        addressService.addResident(address, user);
        verify(addressDao).update(address);

        final Address result = em.find(Address.class, address.getId());
        assertEquals(1, result.getResidents().size());
        assertEquals(address.getResidents().get(0), result.getResidents().get(0));
    }

    @Test
    public void removeResidentTest() {
        addressService.addResident(address, user);
        verify(addressDao).update(address);

        addressService.removeResident(address, user);

        final Address result = em.find(Address.class, address.getId());
        assertTrue(result.getResidents().isEmpty());
    }
}
