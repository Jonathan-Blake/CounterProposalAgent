package blake.bot.messages;

import blake.bot.agents.CounterProposalAgent;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.negoServer.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class MessageHelperTest {

    @SuppressWarnings("unused") // Stringifier relies on static fields to get game to read deal string
    private static final CounterProposalAgent agent = new CounterProposalAgent();
    private static final String CONVERSATION_ID = "CONVO_ID";
    private static final String PROPOSAL_ID = "PROPOSAL_ID";
    private static final String RECEIVER = "RECEIVER";
    private static final String PERFORMATIVE = "PERFORMATIVE";
    private static final BasicDeal DEAL = new BasicDeal(Collections.emptyList(), Collections.emptyList());
    private static final DiplomacyProposal PROPOSAL = new DiplomacyProposal(PROPOSAL_ID, DEAL);
    private MessageHelper messageHelper;
    private Message mockMessage;

    @Before
    public void setup() {
        messageHelper = new MessageHelper();
        mockMessage = new Message(CONVERSATION_ID, RECEIVER, PERFORMATIVE, PROPOSAL);
    }

    @After
    public void tearDown() {
        messageHelper.shutdown();
    }

    @Test
    public void testAddProposalCreatesNewEntry() {
        assertNull(messageHelper.getConversation(CONVERSATION_ID));
        final MessageEvent event = new MessageEvent(mockMessage);
        MessageEventChannels.RECEIVING_PROPOSAL.publish(event);
        assertNotNull(messageHelper.getConversation(CONVERSATION_ID));
        assertEquals(mockMessage, messageHelper.getConversation(CONVERSATION_ID).getMessage());
    }

    @Test
    public void testAddMyProposalCreatesNewEntry() {
        assertNull(messageHelper.getConversation(CONVERSATION_ID));
        final MessageEvent event = new MessageEvent(mockMessage);
        MessageEventChannels.SENDING_PROPOSAL.publish(event);
        assertNotNull(messageHelper.getConversation(CONVERSATION_ID));
        assertEquals(mockMessage, messageHelper.getConversation(CONVERSATION_ID).getMessage());
    }

    @Test
    public void testApprovalUpdatesConversationStatus() {
        assertNull(messageHelper.getConversation(CONVERSATION_ID));
        final MessageEvent event = new MessageEvent(mockMessage);
        MessageEventChannels.SENDING_PROPOSAL.publish(event);
        MessageEventChannels.RECEIVING_ACCEPTANCE.publish(event);
        assertEquals(ConversationStatus.ACCEPTED, messageHelper.getConversation(CONVERSATION_ID).getParticipants().get(mockMessage.getSender()));
    }
}