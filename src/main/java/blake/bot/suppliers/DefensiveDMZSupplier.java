package blake.bot.suppliers;

import blake.bot.utility.HashableRegion;
import blake.bot.utility.HashedPower;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Region;

import java.util.*;
import java.util.stream.Collectors;

public class DefensiveDMZSupplier implements DealGenerator {
    private final HashedPower negotiator;
    private final HashedPower me;
    private final ArrayList<HashableRegion> sharedInterest;
    private final List<HashableRegion> myProvincesOfInterest;
    private final List<HashableRegion> negotiatorsProvincesOfInterest;
    private List<DMZ> currentDMZs;
    private Game game;
    private Iterator<BasicDeal> iterator = new BasicDealIterator(this::get);

    public DefensiveDMZSupplier(Game game, HashedPower me, HashedPower other, List<DMZ> currentDMZs) {
        this.currentDMZs = currentDMZs;
        this.game = game;
        this.me = me;
        this.negotiator = other;
        myProvincesOfInterest = game.getRegions().stream()
                .filter(region -> regionOfInterestToPower(game, me, region))
                .map(HashableRegion::new)
                .collect(Collectors.toList());
        Set<HashableRegion> myAdjacentSet = new HashSet<>(myProvincesOfInterest);
        myProvincesOfInterest.forEach(myRegion -> {
            synchronized (myAdjacentSet) {
                myAdjacentSet.addAll(Utility.Lists.mapList(myRegion.getRegion().getAdjacentRegions(), HashableRegion::new));
            }
        });

        negotiatorsProvincesOfInterest = game.getRegions().stream()
                .filter(region -> regionOfInterestToPower(game, other, region))
                .map(HashableRegion::new)
                .collect(Collectors.toList());
        Set<HashableRegion> negotiatorsSphereOfInterest = new HashSet<>(negotiatorsProvincesOfInterest);
        negotiatorsProvincesOfInterest.forEach(region -> {
            synchronized (negotiatorsSphereOfInterest) {
                negotiatorsSphereOfInterest.addAll(Utility.Lists.mapList(region.getRegion().getAdjacentRegions(), HashableRegion::new));
            }
        });
        sharedInterest = new ArrayList<>(myAdjacentSet);
        sharedInterest.retainAll(negotiatorsSphereOfInterest);
    }

    private boolean regionOfInterestToPower(final Game game, final HashedPower me, final Region region) {
        final Power controller = game.getController(region);
        if (controller != null && me.asString().equals(controller.getName())) return true;
        final Power owner = game.getOwner(region.getProvince());
        if (owner != null && me.asString().equals(owner.getName())) return true;
        return false;
    }

    private BasicDeal get() {
        while (!sharedInterest.isEmpty()) {
            Region dmzRegion = sharedInterest.remove(0).getRegion();
            final DMZ dmz = new DMZ(
                    this.game.getYear(),
                    this.game.getPhase(),
                    Collections.singletonList(negotiator.asPower()),
                    Collections.singletonList(dmzRegion.getProvince())
            );
            if (currentDMZs.stream().noneMatch(oldDMZ -> Utility.Plans.dmzsAreIdentical(dmz, oldDMZ))) {
                return new BasicDeal(
                        Collections.emptyList(),
                        Collections.singletonList(dmz)
                );
            }
        }
        return null;
    }


    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.currentDMZs = confirmedDeals.stream().flatMap(basicDeal -> basicDeal.getDemilitarizedZones().stream()).collect(Collectors.toList());
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        this.reset(confirmedDeals);
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.iterator;
    }
}
