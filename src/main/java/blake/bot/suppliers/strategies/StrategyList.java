package blake.bot.suppliers.strategies;

import blake.bot.utility.Utility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StrategyList {

    private final List<StrategyPlan> plans;

    public StrategyList(final List<StrategyPlan> strategyPlans) {
        this.plans = Collections.unmodifiableList(strategyPlans);
    }

    @JsonCreator()
    public StrategyList(@JsonProperty("plans") final StrategyPlan... strategyPlans) {
        this(Arrays.asList(strategyPlans));
    }

    public StrategyList filter(final Predicate<StrategyPlan> filter) {
        return new StrategyList(plans.stream().filter(filter).collect(Collectors.toList()));
    }

    public StrategyList sort(final Comparator<StrategyPlan> comparator) {
        return new StrategyList(Utility.Lists.createSortedList(this.plans, comparator));
    }

    public Optional<StrategyPlan> match(final BasicDeal deal, final Game game) {
        return getPlans().stream()
                .filter(strategyPlan -> Utility.Deals.areIdentical(deal, strategyPlan.build(game)))
                .findFirst();
    }

    public Optional<StrategyPlan> match(final Predicate<StrategyPlan> predicate) {
        return getPlans().stream()
                .filter(predicate)
                .findFirst();
    }

    public List<StrategyPlan> getPlans() {
        return this.plans;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyList that = (StrategyList) o;
        return plans.equals(that.plans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plans);
    }
}
