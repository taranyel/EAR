package joinMe.db.dao;

import joinMe.db.entity.Rating;
import org.springframework.stereotype.Repository;

@Repository
public class RatingDao extends BaseDao<Rating> {
    public RatingDao() {
        super(Rating.class);
    }
}
