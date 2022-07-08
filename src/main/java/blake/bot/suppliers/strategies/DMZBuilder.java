package blake.bot.suppliers.strategies;

import blake.bot.utility.Utility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DMZBuilder {
    private final @JsonProperty
    int year;
    private final @JsonProperty
    Phase phase;
    private final @JsonProperty
    List<String> powers;
    private final @JsonProperty
    List<String> provinces;

    @JsonCreator
    public DMZBuilder(@JsonProperty("year") int year,
                      @JsonProperty("phase") Phase phase,
                      @JsonProperty("powers") List<String> powers,
                      @JsonProperty("provinces") List<String> provinces) {
        this.year = year;
        this.phase = phase;
        this.powers = powers;
        this.provinces = provinces;
    }

    public DMZ construct(Game game) {
        return new DMZ(year, phase, Utility.Hashing.stringToPower(powers, game), Utility.Hashing.stringToRegion(provinces, game));
    }

    public Collection<String> getPowers() {
        return this.powers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DMZBuilder that = (DMZBuilder) o;
        return year == that.year && phase == that.phase && getPowers().equals(that.getPowers()) && provinces.equals(that.provinces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, phase, getPowers(), provinces);
    }
}
