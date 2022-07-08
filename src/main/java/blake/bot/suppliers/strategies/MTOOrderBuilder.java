package blake.bot.suppliers.strategies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;

import java.util.Objects;

public class MTOOrderBuilder implements OrderBuilder {
    private final @JsonProperty
    int year;
    private final @JsonProperty
    Phase phase;
    private final @JsonProperty
    String power;
    private final @JsonProperty
    String location;
    private final @JsonProperty
    String dest;

    @JsonCreator
    public MTOOrderBuilder(@JsonProperty("year") int year,
                           @JsonProperty("phase") Phase phase,
                           @JsonProperty("power") String power,
                           @JsonProperty("location") String location,
                           @JsonProperty("dest") String dest) {
        this.year = year;
        this.phase = phase;
        this.power = power;
        this.location = location;
        this.dest = dest;
    }

    @Override
    public OrderCommitment construct(Game game) {
        final Power gamePower = game.getPower(this.power);
        final Region region = game.getRegion(location);
        final Region region1 = game.getRegion(dest);
        if ((gamePower == null || region == null || region1 == null)) {
            throw new AssertionError("Error Attempting to get " + gamePower + " " + location + " " + dest);
        }
        return new OrderCommitment(year, phase, new MTOOrder(gamePower, region, region1));
    }

    @Override
    public String toString() {
        return String.format("{%s %s : %s(%s -> %s) }", phase, year, power, location, dest);
    }

    @Override
    public String getPower() {
        return this.power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MTOOrderBuilder that = (MTOOrderBuilder) o;
        return year == that.year && phase == that.phase && power.equals(that.power) && location.equals(that.location) && dest.equals(that.dest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, phase, power, location, dest);
    }
}
