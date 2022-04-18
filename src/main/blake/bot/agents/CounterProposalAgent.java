package blake.bot.agents;

import blake.bot.analyser.AdvancedAdjudicator;
import blake.bot.analyser.PlanCache;
import blake.bot.analyser.PlanInfo;
import blake.bot.analyser.PlanInfoMatcher;
import blake.bot.messages.MessageEvent;
import blake.bot.messages.MessageEventChannels;
import blake.bot.suppliers.DealGenerator;
import blake.bot.utility.HashedPower;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.orders.Order;

import java.util.Collections;
import java.util.List;

public class CounterProposalAgent extends AbstractNegotiationLoopNegotiator {
    private final PlanCache planCache;
    private Object proposalSupplier;
    private List<Province> previouslyOwned;

    CounterProposalAgent(String[] args) {
        super(args);
        this.planCache = new PlanCache(this.getTacticalModule());
    }

    @Override
    public void negotiate(long negotiationDeadline) {
        this.planCache.setNextTurn(this.getGame(), this.getMe(), this.getConfirmedDeals());
        this.planCache.setNoDealPlan(this.planCache.analysePlan(
                new PlanInfo(
                        this.game,
                        new HashedPower(this.getMe()),
                        this.getConfirmedDeals(),
                        new BasicDeal(Collections.emptyList(), Collections.emptyList()))));
        super.negotiate(negotiationDeadline);
        this.proposalSupplier = null;
        this.previouslyOwned = this.getMe().getOwnedSCs();
        this.getLogger().logln("CounterProposalAgent adjudicator data: " + AdvancedAdjudicator.getData(), true);
        this.getLogger().logln(String.format("DumbotDecisions data: accepted = %d rejected = %d", PlanCache.getDumbBotAccepts(), PlanCache.getDumbBotRejects()), true);
    }

    @Override
    protected void handleRejectedMessage(Message receivedMessage) {
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        if (receivedMessage.getMessageId().contains(this.getMe().getName())) {
            //Rejected my proposal
        }
        this.planCache.removePlan(PlanInfoMatcher.dealId(receivedMessage.getMessageId()));
        this.getLogger().logln("CounterProposalAgent.negotiate() Received rejection from " + receivedMessage.getSender() + ": " + receivedProposal);
    }

    @Override
    protected void handleConfirmationMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_CONFIRMATION.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("CounterProposalAgent.negotiate() Received confirmation from " + receivedMessage.getSender() + ": " + receivedProposal);
    }

    @Override
    protected void handleProposalMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_MESSAGE.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("CounterProposalAgent.negotiate() Received proposal from " + receivedMessage.getSender() + ": " + receivedProposal);
    }

    @Override
    protected void handleAcceptanceMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_ACCEPTANCE.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("APDAgent.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + receivedProposal);
    }

    @Override
    protected DealGenerator getProposalSupplier() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void receivedOrder(Order order) {

    }
}
