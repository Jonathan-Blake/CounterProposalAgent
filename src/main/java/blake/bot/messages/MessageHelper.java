package blake.bot.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHelper {
    private final MessageEventSubscribers myProposalCreationHook;
    private final MessageEventSubscribers messageAcceptedHook;
    private final MessageEventSubscribers messageConfirmationHook;
    private final MessageEventSubscribers messageRejectionHook;
    private final MessageEventSubscribers messageProposalHook;
    private final List<String> myProposals;
    private final Map<String, ConversationInfo> conversations;

    public MessageHelper() {
        this.conversations = new HashMap<>();
        this.myProposals = new ArrayList<>();

        this.messageAcceptedHook = this::messageAcceptanceReceived;
        MessageEventChannels.RECEIVING_ACCEPTANCE.subscribe(this.messageAcceptedHook);
        this.messageConfirmationHook = this::messageConfirmationReceived;
        MessageEventChannels.RECEIVING_CONFIRMATION.subscribe(this.messageConfirmationHook);
        this.messageRejectionHook = this::messageRejectionReceived;
        MessageEventChannels.RECEIVING_REJECTION.subscribe(this.messageRejectionHook);
        messageProposalHook = this::newConversationCreation;
        MessageEventChannels.RECEIVING_PROPOSAL.subscribe(this.messageProposalHook);

        this.myProposalCreationHook = this::addToMyProposals;
        MessageEventChannels.SENDING_PROPOSAL.subscribe(this.myProposalCreationHook);
        MessageEventChannels.SENDING_APPROVAL.subscribe(this.messageAcceptedHook);
        MessageEventChannels.SENDING_REJECTION.subscribe(this.messageRejectionHook);
    }

    public boolean shutdown() {
        return MessageEventChannels.SENDING_PROPOSAL.unsubscribe(this.myProposalCreationHook)
                && MessageEventChannels.SENDING_APPROVAL.unsubscribe(this.messageAcceptedHook)
                && MessageEventChannels.SENDING_REJECTION.unsubscribe(this.messageRejectionHook)
                && MessageEventChannels.RECEIVING_ACCEPTANCE.unsubscribe(this.messageAcceptedHook)
                && MessageEventChannels.RECEIVING_CONFIRMATION.unsubscribe(this.messageConfirmationHook)
                && MessageEventChannels.RECEIVING_REJECTION.unsubscribe(this.messageRejectionHook)
                && MessageEventChannels.RECEIVING_PROPOSAL.unsubscribe(this.messageProposalHook)
                ;
    }

    public ConversationInfo getConversation(String conversationId) {
        return this.conversations.get(conversationId);
    }

    private boolean newConversationCreation(final MessageEvent messageEvent) {
        System.out.println("New Message Received");
        final String conversationId = messageEvent.getMessage().getConversationId();
        if (this.conversations.get(conversationId) != null) {
            System.out.println("Conversation ID ALREADY EXISTS");
            return false;
        } else {
            System.out.println("ADDING NEW CONVERSATION TO MAP");
            this.conversations.put(conversationId, new ConversationInfo(messageEvent.getMessage()));
            return true;
        }
    }

    private boolean addToMyProposals(final MessageEvent messageEvent) {
        ConversationInfo ci = new ConversationInfo(messageEvent.getMessage());
        this.conversations.put(messageEvent.getMessage().getConversationId(), ci);
        return this.myProposals.add(messageEvent.getMessage().getConversationId());
    }

    private boolean messageAcceptanceReceived(final MessageEvent event) {
        return this.conversations.get(event.getMessage().getConversationId()).markAcceptance(event.getMessage().getSender());
    }

    private boolean messageConfirmationReceived(final MessageEvent messageEvent) {
        return this.conversations.get(messageEvent.getMessage().getConversationId()).markConfirmation();
    }

    private boolean messageRejectionReceived(final MessageEvent messageEvent) {
        return this.conversations.get(messageEvent.getMessage().getConversationId()).markRejection(messageEvent.getMessage().getSender());
    }
}
