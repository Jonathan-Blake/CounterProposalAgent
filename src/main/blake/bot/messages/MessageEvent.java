package blake.bot.messages;

import ddejonge.negoServer.Message;

public class MessageEvent {
    private final Message message;

    public MessageEvent(Message receivedMessage) {
        this.message = receivedMessage;
    }

    public Message getMessage() {
        return message;
    }
}
