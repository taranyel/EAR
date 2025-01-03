package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AddressDao;
import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Objects.requireNonNull(address);
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

    public void setAddress(Address address, User user) {
        Address existingAddress = findByAll(address);
        if (existingAddress == null) {
            address.addResident(user);
            dao.persist(address);
            user.setAddress(address);
        } else {
            existingAddress.addResident(user);
            dao.update(existingAddress);
            user.setAddress(existingAddress);
        }
    }

    public Address findByID(int id) {
        return dao.find(id);
    }

    public List<Address> findAll() {
        return dao.findAll();
    }

    public Address findByAll(Address address) {
        Objects.requireNonNull(address);
        return dao.findByAll(address);
    }
}
