package blake.bot.utility;

import es.csic.iiia.fabregues.dip.board.Region;

import java.util.Objects;

public class HashableRegion {
    private final Region region;

    public HashableRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashableRegion that = (HashableRegion) o;
        return region.equals(that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region.getName());
    }
}
