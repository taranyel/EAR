package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Address;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressDao extends BaseDao<Address> {
    public AddressDao() {
        super(Address.class);
    }

    public List<Address> findAll() {
        try {
            return em.createNamedQuery("Address.findAll", Address.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Address findByAll(Address address) {
        try {
            return em.createNamedQuery("Address.findByAll", Address.class)
                    .setParameter("city", address.getCity())
                    .setParameter("country", address.getCountry())
                    .setParameter("number", address.getNumber())
                    .setParameter("street", address.getStreet())
                    .setParameter("postIndex", address.getPostIndex())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
