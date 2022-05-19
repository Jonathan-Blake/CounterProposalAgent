package blake.bot.agents;

import blake.bot.analyser.*;
import blake.bot.messages.MessageEvent;
import blake.bot.messages.MessageEventChannels;
import blake.bot.messages.MessageHelper;
import blake.bot.suppliers.CounterProposalSupplier;
import blake.bot.suppliers.DealGenerator;
import blake.bot.suppliers.PlanSupportSupplier;
import blake.bot.suppliers.PrioritisedProposalSupplierList;
import blake.bot.utility.HashedPower;
import blake.bot.utility.Relationship;
import blake.bot.utility.RelationshipMatrix;
import blake.bot.utility.Utility;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;

import java.util.*;

public class CounterProposalAgent extends AbstractNegotiationLoopNegotiator {

    public static final double DISLIKE_PROBABILITY_IF_FALSE = 0.9;
    public static final double DISLIKE_LIKELIHOOD = 0.1;
    public static final double LIKE_PROBABILITY_IF_FALSE = 0.3;
    public static final double LIKE_LIKELIHOOD = 0.7;

    private final PlanCache planCache;
    private final MessageHelper messageHelper;
    private final Map<String, Integer> acceptanceSource;
    private CounterProposalSupplier counterProposalProposal;
    private DealGenerator proposalSupplier;
    private List<Province> previouslyOwned;
    private RelationshipMatrix<Double> relationshipMatrix;

    public CounterProposalAgent(String[] args) {
        super(args);
        this.planCache = new PlanCache(this.getTacticalModule());
        this.messageHelper = new MessageHelper();
        acceptanceSource = new HashMap<>();
        MessageEventChannels.RECEIVING_PROPOSAL.subscribe(
                event -> {
                    Message receivedMessage = event.getMessage();
                    DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
                    BasicDeal deal = (BasicDeal) receivedProposal.getProposedDeal();
                    if (Utility.Plans.testConsistency(deal, this.getGame(), this.getConfirmedDeals())) {
                        AnalysedPlan newPlan = planCache.analysePlan(
                                new PlanInfo(
                                        this.game,
                                        new HashedPower(this.getMe()),
                                        this.getConfirmedDeals(),
                                        deal
                                )
                        );
                        newPlan.getInfo().setDealId(receivedProposal.getId());

                        if (newPlan.getDBraneValue() - planCache.getNoDealAnalysedPlan().getDBraneValue() == 0
                                && this.planCache.getNoDealPlan().getMyOrders().containsAll(newPlan.getPlan().getMyOrders())
                                && this.getAllies().contains(this.game.getPower(receivedMessage.getSender()))
                        ) {
                            this.getLogger().logln("APDAgent.negotiate() Accepted proposal (same orders) from " + receivedMessage.getSender() + ": " + receivedProposal, true);
                            this.getLogger().logln("My plan was " + this.planCache.getNoDealPlan().getMyOrders(), true);
                            this.getLogger().logln("The proposal was " + newPlan.getPlan().getMyOrders(), true);
                            this.acceptAndClearInconsistentPlans(receivedProposal.getId(), receivedMessage.getSender(), Utility.Lists.append(this.getConfirmedDeals(), deal));
                            this.acceptProposal(receivedProposal.getId());
                            this.acceptanceSource.merge("Orders are the same", 1, Integer::sum);
                        } else if (planCache.betterThanNoDeal(newPlan)) {
                            this.acceptProposal(receivedProposal.getId());
                            this.acceptanceSource.merge("Plan is better", 1, Integer::sum);
                            this.acceptAndClearInconsistentPlans(receivedProposal.getId(), receivedMessage.getSender(), Utility.Lists.append(this.getConfirmedDeals(), deal));
                            this.getLogger().logln("APDAgent.negotiate() Accepted proposal from " + receivedMessage.getSender() + ": " + receivedProposal, true);
                        } else if (newPlan.getDBraneValue() - planCache.getNoDealAnalysedPlan().getDBraneValue() == 0) {
//                            this.acceptProposal(receivedProposal.getId());
                            this.acceptanceSource.merge("Plan is not worse", 1, Integer::sum);
                            this.counterProposalProposal.addOffer(receivedProposal);
                            this.rejectProposal(receivedProposal.getId());
//                            this.acceptAndClearInconsistentPlans(receivedProposal.getId(), receivedMessage.getSender(), Utility.Lists.append(this.getConfirmedDeals(), deal));
                            this.getLogger().logln("APDAgent.negotiate() CounterPropose proposal from " + receivedMessage.getSender() + ": " + receivedProposal, true);
                        } else {
//                            this.rejectProposal(receivedProposal.getId());
//                            this.counterProposalProposal.addOffer(receivedProposal);
                            this.acceptanceSource.merge("Plan Rejected", 1, Integer::sum);
                            this.getLogger().logln("APDAgent.negotiate() Rejected proposal from " + receivedMessage.getSender() + ": " + receivedProposal);
                            this.rejectAndDislikeProposal(receivedProposal.getId(), receivedMessage.getSender());
                        }
                    } else {
                        this.acceptanceSource.merge("Plan is inconsistent", 1, Integer::sum);
                        this.getLogger().logln("APDAgent.negotiate() Rejected proposal from " + receivedMessage.getSender() + ": " + receivedProposal);
                        this.rejectAndDislikeProposal(receivedProposal.getId(), receivedMessage.getSender());
//                        this.rejectProposal(receivedProposal.getId());
                    }
                    return true;
                });
    }


    public CounterProposalAgent() {
        this(new String[]{});
    }

    public static void main(String[] args) {
        CounterProposalAgent myPlayer = new CounterProposalAgent(args);
        myPlayer.run();
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
        this.getLogger().logln(String.format("DumbotDecisions data: accepted = %d rejected = %d, DBraneAccept = %d, DBraneReject = %d", PlanCache.getDumbBotAccepts(), PlanCache.getDumbBotRejects(), PlanCache.getPlanIsBetter(), PlanCache.getPlanIsWorse()), true);
        this.getLogger().logln(String.format("Orders received : %s ", Arrays.toString(acceptanceSource.entrySet().toArray())), true);
    }

    @Override
    protected void handleRejectedMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_REJECTION.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
//        if (receivedMessage.getMessageId().contains(this.getMe().getName())) {
//            //Rejected my proposal
//        }
//        this.planCache.removePlan(PlanInfoMatcher.dealId(receivedMessage.getMessageId()));
        this.getLogger().logln("CounterProposalAgent.negotiate() Received rejection from " + receivedMessage.getSender() + ": " + receivedProposal, true);
    }

    @Override
    protected void handleConfirmationMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_CONFIRMATION.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("CounterProposalAgent.negotiate() Received confirmation from " + receivedMessage.getSender() + ": " + receivedProposal, true);
    }

    @Override
    protected void handleProposalMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_PROPOSAL.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("CounterProposalAgent.negotiate() Received proposal from " + receivedMessage.getSender() + ": " + receivedProposal, true);
    }

    @Override
    protected void handleAcceptanceMessage(Message receivedMessage) {
        MessageEventChannels.RECEIVING_ACCEPTANCE.publish(new MessageEvent(receivedMessage));
        DiplomacyProposal receivedProposal = (DiplomacyProposal) receivedMessage.getContent();
        this.getLogger().logln("APDAgent.negotiate() Received acceptance from " + receivedMessage.getSender() + ": " + receivedProposal, true);
    }

    @Override
    protected DealGenerator getProposalSupplier() {
        if (proposalSupplier == null) {
            this.counterProposalProposal = new CounterProposalSupplier(planCache, game, getLogger(), getNegotiatingPowers(), new HashedPower(this.getMe()), this::getConfirmedDeals);
            this.proposalSupplier = new PrioritisedProposalSupplierList(
                    getLogger(),
                    counterProposalProposal,
//                    new CoordinatedAllianceSupplier(this::getAllies, getTacticalModule(), getGame(), getConfirmedDeals(), getLogger()),
                    new PlanSupportSupplier(this.planCache::getNoDealAnalysedPlan, this.getNegotiatingPowers(), getLogger())
            );
        }
//        getLogger().logln("Retrieving Proposal Supplier " + this.proposalSupplier,true);
        return this.proposalSupplier;
    }

    @Override
    public void start() {

    }

    @Override
    public void receivedOrder(Order order) {
        if (order instanceof MTOOrder && order.getPower() != this.getMe()) {
            MTOOrder mtoOrder = (MTOOrder) order;
            if (this.previouslyOwned == null) {
                this.getLogger().logln("PreviouslyOwned is null?????", true);
                return;
            }
            if (mtoOrder.getDestination() == null) {
                this.getLogger().logln("__________________________________________Somehow MTO has no destination?????", true);
                return;
            }
            if (this.previouslyOwned.contains(mtoOrder.getDestination().getProvince())) {
                if (this.getAllies().contains(mtoOrder.getPower())) {
                    //Dislike or break alliance, whichever is smaller.
                    Optional<Double> currentRelationship = this.getRelationshipMatrix().getRelationship(mtoOrder.getPower());
                    currentRelationship.ifPresent(curr -> this.getRelationshipMatrix().setRelationship(mtoOrder.getPower(),
                            Math.min(
                                    0.25d,
                                    Utility.Probability.bayes(
                                            curr,
                                            DISLIKE_PROBABILITY_IF_FALSE,
                                            DISLIKE_LIKELIHOOD
                                    ))));
                } else {
                    this.dislike(mtoOrder.getPower());
                }
            }
        }
    }

    private void acceptAndClearInconsistentPlans(String id, String sender, List<BasicDeal> newCommitments) {
        this.acceptProposal(id);
        this.like(sender);
        this.planCache.removePlan(PlanInfoMatcher.stillConsistent(newCommitments).negate());
    }

    private void rejectAndDislikeProposal(String id, String sender) {
        this.dislike(sender);
        this.rejectProposal(id);
    }

    private List<Power> getAllies() {
        return this.getRelationshipMatrix().getAllies();
    }

    private RelationshipMatrix<Double> getRelationshipMatrix() {
        if (this.relationshipMatrix == null) {
            this.relationshipMatrix = RelationshipMatrix.getDoubleMatrix(this.getMe(), this.getNegotiatingPowers(), this.getGame(), 0.75, (value -> {
                if (value >= 0.5) {
                    return Relationship.ALLIED;
                } else {
                    return Relationship.WAR;
                }
            }));
        }
        return this.relationshipMatrix;
    }

    private void dislike(String power) {
        dislike(this.getGame().getPower(power));
    }

    private void dislike(Power power) {
        Optional<Double> currentRelationship = this.getRelationshipMatrix().getRelationship(power);
        currentRelationship.ifPresent(curr -> {
            this.getRelationshipMatrix().setRelationship(power,
                    Math.max(
                            0.000001d,
                            Utility.Probability.bayes(
                                    curr,
                                    DISLIKE_PROBABILITY_IF_FALSE,
                                    DISLIKE_LIKELIHOOD
                            )));
            this.getLogger().logln(String.format("CounterProposal Agent dislikes %s : %s -> %s", power.getName(), currentRelationship.get(), this.getRelationshipMatrix().getRelationship(power)));
        });
    }

    private void like(String power) {
        like(this.getGame().getPower(power));
    }

    private void like(Power power) {
        Optional<Double> currentRelationship = this.getRelationshipMatrix().getRelationship(power);
        currentRelationship.ifPresent(curr -> {
            this.getRelationshipMatrix().setRelationship(power,
                    Math.min(
                            0.999999d,
                            Utility.Probability.bayes(
                                    curr,
                                    LIKE_PROBABILITY_IF_FALSE,
                                    LIKE_LIKELIHOOD
                            )));
            this.getLogger().logln(String.format("CounterProposal likes %s : %s -> %s", power.getName(), currentRelationship.get(), this.getRelationshipMatrix().getRelationship(power)));
        });
    }
}
