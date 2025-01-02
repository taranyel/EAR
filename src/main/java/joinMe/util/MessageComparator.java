package joinMe.util;

import joinMe.db.entity.Message;

public class MessageComparator {

    public static int compare(Message message1, Message message2) {
        return message1.getTime().compareTo(message2.getTime());
    }
}
