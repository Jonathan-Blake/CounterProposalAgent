package blake.bot.messages;

import blake.bot.agents.DomainKnowledgeAgent;
import blake.bot.utility.DatedObject;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ConversationInfoTest {
    @SuppressWarnings("unused") // Stringifier relies on static fields to get game to read deal string
    private static final DomainKnowledgeAgent agent = new DomainKnowledgeAgent();

    private static final String CONVERSATION_ID = "CONVO_ID";
    private static final String PROPOSAL_ID = "PROPOSAL_ID";
    private static final String RECEIVER = "RECEIVER";
    private static final String PERFORMATIVE = "PERFORMATIVE";
    private static final BasicDeal DEAL = new BasicDeal(Collections.emptyList(), Collections.emptyList());
    private static final DiplomacyProposal PROPOSAL = new DiplomacyProposal(PROPOSAL_ID, DEAL);

    private static final Game game = DiplomacyGameBuilder.createDefaultGame();

    public void testGetCommitmentsByPowerName() {
    }

    public void testGetDMZsByPowerName() {
    }

    @Test
    public void testGetExpiryDateSingleObject() {
        agent.game = game;
        BasicDeal deal = new BasicDeal(Collections.emptyList(), Collections.singletonList(
                new DMZ(1900, Phase.SPR, Arrays.asList(game.getPower("ENG"), game.getPower("AUS")), Arrays.asList(game.getProvince("BOH")))
        ));
        System.out.println(deal);
        DiplomacyProposal proposal = new DiplomacyProposal(PROPOSAL_ID, deal);
        MessageEvent event = new MessageEvent(new Message(CONVERSATION_ID, RECEIVER, PERFORMATIVE, proposal));
        ConversationInfo ci = new ConversationInfo(event.getMessage());
        assertEquals(new DatedObject(1900, Phase.SPR), ci.getExpiryDate());
        assertEquals(new DatedObject(1900, Phase.SPR), ci.getStartDate());
    }

    @Test
    public void testGetExpiryDate() {
        BasicDeal deal = new BasicDeal(Collections.emptyList(), Collections.singletonList(
                new DMZ(1900, Phase.SPR, Arrays.asList(game.getPower("ENG"), game.getPower("AUS")), Arrays.asList(game.getProvince("BOH")))
        ));
        System.out.println(deal);
        DiplomacyProposal proposal = new DiplomacyProposal(PROPOSAL_ID, deal);
        MessageEvent event = new MessageEvent(new Message(CONVERSATION_ID, RECEIVER, PERFORMATIVE, proposal));
        ConversationInfo ci = new ConversationInfo(event.getMessage());
        assertEquals(new DatedObject(1900, Phase.SPR), ci.getExpiryDate());
        assertEquals(new DatedObject(1900, Phase.SPR), ci.getStartDate());
    }

    public void testGetStartDate() {
    }
}