package joinMe.db.dao;

import joinMe.db.entity.Address;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDao extends BaseDao<Address> {
    public AddressDao() {
        super(Address.class);
    }
}
