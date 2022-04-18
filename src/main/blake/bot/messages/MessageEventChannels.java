package blake.bot.messages;

import java.util.HashSet;
import java.util.Set;

public enum MessageEventChannels {
    SENDING_MESSAGE,
    SENDING_APPROVAL,
    SENDING_REJECTION,
    RECEIVING_MESSAGE,
    RECEIVING_APPROVAL,
    RECEIVING_REJECTION,
    RECEIVING_CONFIRMATION, RECEIVING_ACCEPTANCE;

    Set<MessageEventSubscribers> subscribers = new HashSet<>();


    public boolean subscribe(MessageEventSubscribers subscriber) {
        return this.subscribers.add(subscriber);
    }

    public boolean unsubscribe(MessageEventSubscribers unsubscribe) {
        return this.subscribers.remove(unsubscribe);
    }

    public void publish(MessageEvent event) {
        this.subscribers.forEach(subscriber -> {
            try {
                subscriber.alert(event);
            } catch (RuntimeException e) {
                System.out.println("CAUGHT EXCEPTION");
            }
        });
    }
}
