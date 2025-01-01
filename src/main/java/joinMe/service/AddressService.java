package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AddressDao;
import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
public class AddressService {
    private final AddressDao dao;

    @Autowired
    public AddressService(AddressDao dao) {
        this.dao = dao;
    }

    public void persist(Address address) {
        Objects.requireNonNull(address);
        dao.persist(address);
    }

    public void update(Address address) {
        dao.update(address);
    }

    public void addResident(Address address, User resident) {
        Objects.requireNonNull(address);
        Objects.requireNonNull(resident);
        address.addResident(resident);
        dao.update(address);
    }

    public void removeResident(Address address, User resident) {
        Objects.requireNonNull(address);
        Objects.requireNonNull(resident);
        address.removeResident(resident);
        dao.update(address);
    }
}
