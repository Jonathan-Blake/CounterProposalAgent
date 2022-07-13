package blake.bot.suppliers;

import blake.bot.suppliers.strategies.StrategyList;
import blake.bot.suppliers.strategies.StrategyPlan;
import blake.bot.suppliers.strategies.StrategyRegister;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.tools.Logger;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PregenStrategySupplier implements DealGenerator {

    private final Logger logger;
    private StrategyList strategies;
    private Game game;
    private Iterator<StrategyPlan> strategyIterator;
    private Iterator<BasicDeal> iterator = new BasicDealIterator(this::get);

    public PregenStrategySupplier(Game game, Power me, List<Power> negotiators, Logger logger) {
        this.game = game;
        this.logger = logger;
        this.strategies = StrategyRegister.REGISTER
                .filter(strategyPlan -> strategyPlan.participants().contains(me.getName()))
                .filter(strategyPlan -> Utility.Lists.mapList(negotiators, Power::getName).containsAll(strategyPlan.participants()))
                .filter(strategyPlan -> !strategyPlan.targets().contains(me.getName()))
                .sort(Comparator.comparingInt(strategyPlan -> strategyPlan.getAdjustedWeight(negotiators)));
        strategyIterator = this.strategies.getPlans().iterator();
    }

    private BasicDeal get() {
        if (this.strategyIterator == null) {
            return null;
        }
        if (strategyIterator.hasNext()) {
            StrategyPlan next = strategyIterator.next();
            logger.logln("Next deal is " + next.name);
            final BasicDeal basicDeal = next.build(this.game);
            logger.logln(String.format("Proposing pregen deal %s", basicDeal), true);
            return basicDeal;
        }
        return null;
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.strategies = this.strategies.filter(strategyPlan -> Utility.Plans.testConsistency(strategyPlan.build(this.game), this.game, confirmedDeals));
        strategyIterator = this.strategies.getPlans().iterator();
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        this.reset(confirmedDeals);
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.iterator;
    }

    public Optional<StrategyPlan> match(BasicDeal deal) {
        return this.strategies.match(deal, this.game);
    }
}
