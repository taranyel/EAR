package joinMe.db.dao;

import org.springframework.stereotype.Repository;

@Repository
public class Message extends BaseDao<Message> {
    public Message() {
        super(Message.class);
    }
}
