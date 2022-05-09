package blake.bot.suppliers;

import blake.bot.analyser.AnalysedPlan;
import blake.bot.analyser.PlanCache;
import blake.bot.analyser.PlanInfo;
import blake.bot.utility.HashedPower;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.bandana.tools.Logger;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CounterProposalSupplier implements DealGenerator {
    private final Random rand;

    private final PlanCache planCache;
    //    DealGenerator proposalSupplier = DealGeneratorBuilder.get().filter(basicDeal -> this.planCache.betterThanNoDeal(planCache.lookupDeal(basicDeal))).build();
    private final Queue<DiplomacyProposal> recievedProposalQueue;
    private final Iterator<BasicDeal> iterator;
    private final Game game;
    private final Logger logger;
    private final List<Power> negotatingPowers;
    private final HashedPower me;
    //    private final DBraneTactics tactics;
    private final Supplier<List<BasicDeal>> getCommitments;


    private DiplomacyProposal currentConsideredProposal;
    private BasicDeal currentDeal;
    private DealGenerator currentPlanSupplier;

    public CounterProposalSupplier(PlanCache planCache, Game game, Logger logger, List<Power> negotiatingPowers, HashedPower me, Supplier<List<BasicDeal>> getCommitments) {
        this.planCache = planCache;
        this.game = game;
        this.logger = logger;
        this.negotatingPowers = negotiatingPowers;
        this.me = me;
//        this.tactics = tactics;
        this.getCommitments = getCommitments;
        this.recievedProposalQueue = new PriorityQueue<>(
                (a, b) -> this.planCache.getDealComparator().compare((BasicDeal) a.getProposedDeal(), (BasicDeal) b.getProposedDeal())
        );
        iterator = new BasicDealIterator(this::get, logger);
        this.rand = new Random();
    }

    private CounterProposalDeal get() {
        while (!this.recievedProposalQueue.isEmpty() || currentConsideredProposal != null) {
            if (currentConsideredProposal != null) {
                this.currentConsideredProposal = this.recievedProposalQueue.poll();
                currentDeal = (BasicDeal) this.currentConsideredProposal.getProposedDeal();
                final String proposerId = currentConsideredProposal.getId().substring(0, 3);
                currentPlanSupplier = new FilteredProposalSupplier(
                        ((Predicate<BasicDeal>) (proposal -> {
                            return proposal.getDemilitarizedZones().stream()
                                    .flatMap(dmz -> dmz.getPowers().stream())
                                    .anyMatch(power -> power.getName().equals(proposerId)) ||
                                    proposal.getOrderCommitments().stream()
                                            .anyMatch(orderCommitment -> orderCommitment.getOrder().getPower().getName().equals(proposerId));
                        })),
                        new FilteredProposalSupplier(
                                deal -> Utility.Plans.testConsistency(deal, game, getCommitments.get()),
                                new PrioritisedProposalSupplierList(
//                                        planCache,
//                                rand.nextInt(2) == 0?
                                        new EagerLimitedProposalSupplier(
                                                new PlanSupportSupplier(this::makePlan, Collections.singletonList(game.getPower(proposerId)), logger),
                                                5,
                                                this.planCache
                                        )
//                    :
                                        ,
                                        new EagerLimitedProposalSupplier(
                                                new DefensiveDMZSupplier(game, me, new HashedPower(game.getPower(proposerId)), getCommitments.get().stream().flatMap(commitment -> commitment.getDemilitarizedZones().stream()).collect(Collectors.toList())),
                                                5,
                                                this.planCache
                                        )
                                )
                        )
                );
            }
            while (currentPlanSupplier.iterator().hasNext()) {
                BasicDeal ret = Utility.Plans.merge(currentDeal, currentPlanSupplier.iterator().next());
                if (Utility.Plans.testConsistency(ret, this.game, this.getCommitments.get())) {
                    logger.logln("Counter proposal Found", true);
                    return new CounterProposalDeal(ret, currentConsideredProposal);
                }
            }

            this.currentConsideredProposal = null;
        }
        return null;
    }

    private AnalysedPlan makePlan() {
        return planCache.analysePlan(new PlanInfo(
                this.game,
                this.me,
                this.getCommitments.get(),
                currentDeal
        ));
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.recievedProposalQueue
                .removeIf(diplomacyProposal -> !Utility.Plans.testConsistency(
                        (BasicDeal) diplomacyProposal.getProposedDeal(),
                        this.game,
                        confirmedDeals)
                );
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        reset(confirmedDeals);
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.iterator;
    }

    public void addOffer(DiplomacyProposal receivedProposal) {
        logger.logln("Added new proposal to offer counters to: " + receivedProposal.getProposedDeal(), true);
        this.recievedProposalQueue.add(receivedProposal);
    }
}
