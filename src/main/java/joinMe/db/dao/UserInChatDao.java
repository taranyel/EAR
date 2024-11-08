package joinMe.db.dao;

import joinMe.db.entity.UserInChat;
import org.springframework.stereotype.Repository;

@Repository
public class UserInChatDao extends BaseDao<UserInChat> {
    public UserInChatDao() {
        super(UserInChat.class);
    }
}
