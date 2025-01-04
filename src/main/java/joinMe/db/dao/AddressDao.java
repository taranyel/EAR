package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);
        cq.select(root)
                .where(cb.equal(root.get("city"), address.getCity()))
                .where(cb.equal(root.get("country"), address.getCountry()))
                .where(cb.equal(root.get("number"), address.getNumber()))
                .where(cb.equal(root.get("street"), address.getStreet()))
                .where(cb.equal(root.get("postIndex"), address.getPostIndex()));

        try {
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
