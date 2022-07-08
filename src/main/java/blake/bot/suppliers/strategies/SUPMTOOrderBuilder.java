package blake.bot.suppliers.strategies;

import com.fasterxml.jackson.annotation.JsonProperty;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.SUPMTOOrder;

import java.util.Objects;

public class SUPMTOOrderBuilder implements OrderBuilder {
    private final @JsonProperty
    int year;
    private final @JsonProperty
    Phase phase;
    private final @JsonProperty
    String power;
    private final @JsonProperty
    String loc;
    private final @JsonProperty
    MTOOrderBuilder mto;

    public SUPMTOOrderBuilder(@JsonProperty("year") int year,
                              @JsonProperty("phase") Phase phase,
                              @JsonProperty("power") String supporterPow,
                              @JsonProperty("loc") String supporterLoc,
                              @JsonProperty("mto.power") String pow,
                              @JsonProperty("mto.loc") String loc,
                              @JsonProperty("mto.dest") String dest) {
        this.year = year;
        this.phase = phase;
        this.power = supporterPow;
        this.loc = supporterLoc;
        this.mto = new MTOOrderBuilder(year, phase, pow, loc, dest);
    }

    @Override
    public OrderCommitment construct(Game game) {
        return new OrderCommitment(year, phase, new SUPMTOOrder(game.getPower(power), game.getRegion(loc), (MTOOrder) this.mto.construct(game).getOrder()));
    }

    @Override
    public String getPower() {
        return this.power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SUPMTOOrderBuilder that = (SUPMTOOrderBuilder) o;
        return year == that.year && phase == that.phase && getPower().equals(that.getPower()) && loc.equals(that.loc) && mto.equals(that.mto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, phase, getPower(), loc, mto);
    }
}
