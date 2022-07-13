package blake.bot.suppliers.strategies;

import blake.bot.utility.DatedObject;
import blake.bot.utility.Utility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StrategyPlan {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public final @JsonProperty("name")
    String name;
    public final @JsonProperty("orders")
    List<OrderBuilder> orders;
    public final @JsonProperty("dmzs")
    List<DMZBuilder> dmzs;
    private final @JsonProperty("weight")
    int weight;
    private final @JsonProperty("targets")
    List<String> targets;
    private Set<String> participantSet;
    private BasicDeal deal;

    @JsonCreator
    public StrategyPlan(@JsonProperty("name") String name,
                        @JsonProperty("weight") int weight,
                        @JsonProperty("targets") List<String> targets,
                        @JsonProperty("orders") List<OrderBuilder> orders,
                        @JsonProperty("dmzs") List<DMZBuilder> dmzs) {
        this.name = name;
        this.weight = weight;
        this.targets = targets;
        this.orders = orders;
        this.dmzs = dmzs;
    }

    public BasicDeal build(Game game) {
        if (this.deal == null) {
            final DatedObject currentDate = new DatedObject(game);
            this.deal = new BasicDeal(
                    orders.stream()
                            .map(builder -> builder.construct(game))
                            .filter(orderCommitment -> !Utility.Dates.isHistory(new DatedObject(orderCommitment), currentDate))
                            .collect(Collectors.toList()),
                    dmzs.stream()
                            .map(builder -> builder.construct(game))
                            .filter(dmz -> !Utility.Dates.isHistory(new DatedObject(dmz), currentDate))
                            .collect(Collectors.toList())
            );
        }
        return this.deal;
    }

    public Set<String> participants() {
        if (participantSet == null) {
            this.participantSet = new HashSet<>();
            this.orders.forEach(orderBuilder -> participantSet.add(orderBuilder.getPower()));
            this.dmzs.forEach(dmzBuilder -> participantSet.addAll(dmzBuilder.getPowers()));
        }
        return this.participantSet;
    }

    public Set<String> targets() {
        return new HashSet<>(targets);
    }

    //Sort in ascending order
    public int getAdjustedWeight(List<Power> negotiators) {
        List<String> negotiatorTargets = Utility.Lists.mapList(negotiators, Power::getName);
        negotiatorTargets.retainAll(targets());
        return weight - participants().size() - negotiatorTargets.size();
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return String.format("[ name = %s, orders = %s]", this.name, this.orders);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyPlan that = (StrategyPlan) o;
        return weight == that.weight && name.equals(that.name) && targets.equals(that.targets) && orders.equals(that.orders) && dmzs.equals(that.dmzs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, targets, orders, dmzs, participantSet, deal);
    }
}
