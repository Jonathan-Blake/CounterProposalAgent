package blake.bot.messages;

@FunctionalInterface
public interface MessageEventSubscribers {
    boolean alert(MessageEvent event);
}
