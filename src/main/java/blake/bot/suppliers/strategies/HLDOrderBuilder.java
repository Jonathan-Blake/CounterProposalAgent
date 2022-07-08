package blake.bot.suppliers.strategies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;
import es.csic.iiia.fabregues.dip.orders.HLDOrder;

import java.util.Objects;

public class HLDOrderBuilder implements OrderBuilder {
    private final @JsonProperty
    int year;
    private final @JsonProperty
    Phase phase;
    private final @JsonProperty
    String power;
    private final @JsonProperty
    String region;

    @JsonCreator
    public HLDOrderBuilder(@JsonProperty("year") int year,
                           @JsonProperty("phase") Phase phase,
                           @JsonProperty("power") String power,
                           @JsonProperty("region") String region) {
        this.year = year;
        this.phase = phase;
        this.power = power;
        this.region = region;
    }

    @Override
    public OrderCommitment construct(Game game) {
        return new OrderCommitment(year, phase, new HLDOrder(game.getPower(power), game.getRegion(region)));
    }

    @Override
    public String getPower() {
        return this.power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HLDOrderBuilder that = (HLDOrderBuilder) o;
        return year == that.year && phase == that.phase && getPower().equals(that.getPower()) && region.equals(that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, phase, getPower(), region);
    }
}
