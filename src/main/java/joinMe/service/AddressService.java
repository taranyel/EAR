package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AddressDao;
import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
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
        Address address = dao.find(id);
        if (address == null) {
            throw NotFoundException.create("Address", id);
        }
        return address;
    }

    public List<Address> findAll() {
        return dao.findAll();
    }

    public Address findByAll(Address address) {
        Objects.requireNonNull(address);
        return dao.findByAll(address);
    }

    public void validateAddressType(String type) {
        if (!Objects.equals(type, "flat") || !Objects.equals(type, "house")) {
            throw new IllegalArgumentException("Invalid address type");
        }
    }
}
