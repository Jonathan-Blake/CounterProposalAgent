package blake.bot.suppliers;

import blake.bot.utility.HashableRegion;
import blake.bot.utility.HashedPower;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import ddejonge.bandana.tools.Logger;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CoordinatedAllianceSupplier implements DealGenerator {
    private final Supplier<List<Power>> getAllies;
    private final DBraneTactics tacticalModule;
    private final Game game;
    private final Logger logger;
    private List<BasicDeal> commitments;
    private boolean proposed;
    private final Iterator<BasicDeal> iterator = new BasicDealIterator(this::get);

    public CoordinatedAllianceSupplier(Supplier<List<Power>> getAllies, DBraneTactics tacticalModule, Game game, List<BasicDeal> commitments, Logger logger) {
        this.getAllies = getAllies;
        this.tacticalModule = tacticalModule;
        this.game = game;
        this.commitments = commitments;
        this.logger = logger;
        proposed = false;
    }

    private BasicDeal get() {
        if (!proposed) {
            final List<Power> powerList = getAllies.get();
            Map<HashedPower, Plan> alliance = powerList.stream().collect(Collectors.<Power, HashedPower, Plan>toMap(
                    HashedPower::new,
                    power -> tacticalModule.determineBestPlan(game, power, commitments, powerList)
            ));
            List<OrderCommitment> orders = alliance.values().stream()
                    .flatMap((Plan plan) -> plan.getMyOrders().stream())
                    .map(order -> new OrderCommitment(game.getYear(), game.getPhase(), order))
                    .collect(Collectors.toList());
            proposed = true;
            Map<HashableRegion, List<Order>> orderDestinations = new HashMap<>();
            orders.forEach(orderCommitment -> orderDestinations.merge(
                    new HashableRegion(Utility.Plans.getFinalDestination(orderCommitment.getOrder())),
                    new LinkedList<>(Collections.singleton(orderCommitment.getOrder())),
                    (prev, one) -> {
                        prev.addAll(one);
                        return prev;
                    }
            ));
            orderDestinations.forEach((key, value) -> {
                if (value.size() > 1) {
                    logger.logln("Order Conflicts detected" + Arrays.toString(value.toArray()) + " retaining " + value.remove(0));
                    value.forEach(order -> orders.removeIf(orderCommitment -> orderCommitment.getOrder() == order));
                }
            });
            if (orders.isEmpty()) {
                return null;
            } else {
                BasicDeal ret = new BasicDeal(
                        orders,
                        Collections.emptyList()
                );
                logger.logln("Proposing Alliance plan " + ret, true);
                return ret;
            }
        }
        return null;
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.commitments = confirmedDeals;
        this.proposed = false;
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        this.commitments = confirmedDeals;
        this.proposed = false;
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.iterator;
    }
}
