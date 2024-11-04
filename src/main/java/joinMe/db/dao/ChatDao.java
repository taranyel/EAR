package joinMe.db.dao;

import joinMe.db.entity.Chat;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDao extends BaseDao<Chat> {
    public ChatDao() {
        super(Chat.class);
    }
}
