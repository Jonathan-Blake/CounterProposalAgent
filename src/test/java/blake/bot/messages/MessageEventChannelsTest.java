package blake.bot.messages;

import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.negoServer.Message;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static blake.bot.messages.MessageEventChannels.SENDING_PROPOSAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageEventChannelsTest {
    public static final Message RECEIVED_MESSAGE = new Message("1", "2", "3", new BasicDeal(Collections.emptyList(), Collections.emptyList()));
    static AtomicInteger nextSubscriberId = new AtomicInteger(0);

    @Test
    public void testSubscriberReceivesAlerts() {
        TestMessageEventSubscribers subscriber = new TestMessageEventSubscribers(false);

        Arrays.stream(MessageEventChannels.values()).forEach(value -> {
            value.subscribe(subscriber);
            MessageEvent testEvent = new TestMessageEvent(true);
            value.publish(testEvent);
            assertEquals(testEvent, subscriber.getLastEvent());
            value.unsubscribe(subscriber);
        });


    }

    @Test
    public void testSubscriberAlertsDoNotCrossChannel() {
        TestMessageEventSubscribers sendSubscriber = new TestMessageEventSubscribers(false);
        TestMessageEventSubscribers otherSubscriber = new TestMessageEventSubscribers(false);
        SENDING_PROPOSAL.subscribe(sendSubscriber);
        Arrays.stream(MessageEventChannels.values())
                .filter(value -> value != SENDING_PROPOSAL)
                .forEach(value -> value.subscribe(otherSubscriber));
        MessageEvent testEvent = new TestMessageEvent(true);

        SENDING_PROPOSAL.publish(testEvent);

        assertEquals(testEvent, sendSubscriber.getLastEvent());
        assertNull(otherSubscriber.getLastEvent());

        Arrays.stream(MessageEventChannels.values()).forEach(value -> {
            value.unsubscribe(sendSubscriber);
            value.unsubscribe(otherSubscriber);
        });
    }

    @Test
    public void testMultipleSubscriberReceivesAlerts() {
        TestMessageEventSubscribers subscriber = new TestMessageEventSubscribers(false);
        TestMessageEventSubscribers otherSubscriber = new TestMessageEventSubscribers(false);

        SENDING_PROPOSAL.subscribe(subscriber);
        SENDING_PROPOSAL.subscribe(otherSubscriber);
        MessageEvent testEvent = new TestMessageEvent(true);
        SENDING_PROPOSAL.publish(testEvent);

        assertEquals(testEvent, subscriber.getLastEvent());
        assertEquals(testEvent, otherSubscriber.getLastEvent());

        Arrays.stream(MessageEventChannels.values()).forEach(value -> {
            value.unsubscribe(subscriber);
            value.unsubscribe(otherSubscriber);
        });
    }

    @Test
    public void testDoesNotPropagateExceptions() {
        TestMessageEventSubscribers subscriber = new TestMessageEventSubscribers(true);
        TestMessageEventSubscribers otherSubscriber = new TestMessageEventSubscribers(false);

        SENDING_PROPOSAL.subscribe(subscriber);
        SENDING_PROPOSAL.subscribe(otherSubscriber);
        MessageEvent testEvent = new TestMessageEvent(true);

        SENDING_PROPOSAL.publish(testEvent);

        assertNull(subscriber.getLastEvent());
        assertEquals(testEvent, otherSubscriber.getLastEvent());

        Arrays.stream(MessageEventChannels.values()).forEach(value -> {
            value.unsubscribe(subscriber);
            value.unsubscribe(otherSubscriber);
        });
    }

    private static class TestMessageEvent extends MessageEvent {
        static int nextId = 0;
        private final int id;

        public TestMessageEvent(boolean b) {
            super(RECEIVED_MESSAGE);
            this.id = nextId++;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    private class TestMessageEventSubscribers implements MessageEventSubscribers {
        private final int id;
        private final LinkedList<MessageEvent> eventInfo = new LinkedList<>();
        private final boolean excepts;

        private TestMessageEventSubscribers(boolean excepts) {
            this.excepts = excepts;
            this.id = nextSubscriberId.incrementAndGet();
        }

        @Override
        public boolean alert(MessageEvent event) {
            System.out.println("Processing " + event + " from " + this);
            if (this.excepts) {
                throw new RuntimeException();
            }
            return this.eventInfo.add(event);
        }

        public Object getLastEvent() {
            try {
                return this.eventInfo.getLast();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }
}