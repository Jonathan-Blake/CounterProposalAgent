package blake.bot.suppliers;

import blake.bot.analyser.AnalysedPlan;
import blake.bot.utility.HashedPower;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import ddejonge.bandana.tools.Logger;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlanSupportSupplier implements DealGenerator {
    private final Supplier<AnalysedPlan> getCurrentPlan;
    private final List<Power> negotiatingPowers;
    private final Logger logger;
    private final BasicDealIterator myIterator;
    private final List<Region> myRegions;
    private final Set<Region> adjacentRegions;
    private final Optional<Region> dmzRegion;
    private AnalysedPlan currentPlan;
    private Iterator<Order> orderIterator;
    private Order currentOrder;

    private List<Region> mtoAdjacentTargets;
    private List<Region> hldAdjacentSupports;

    public PlanSupportSupplier(Supplier<AnalysedPlan> getCurrentPlan, List<Power> negotiatingPowers, Logger logger) {
        this.logger = logger;
        this.getCurrentPlan = getCurrentPlan;
        this.negotiatingPowers = negotiatingPowers;
        this.currentPlan = this.getCurrentPlan();
        final HashedPower me = currentPlan.getInfo().getMe();
        this.myRegions = getGame().getRegions().stream().filter(
                region -> Optional.ofNullable(
                                getGame().getController(region))
                        .map(value -> value.getName().equals(me.asString()))
                        .orElse(false)
        ).collect(Collectors.toList());
        this.adjacentRegions = myRegions.stream()
                .flatMap(region -> region.getAdjacentRegions().stream())
                .filter(region -> !this.myRegions.contains(region))
                .collect(Collectors.toSet());
        dmzRegion = getGame().getRegions().stream()
                .filter(region -> !adjacentRegions.contains(region))
                .findFirst();
        logger.logln("I will demilitarize " + dmzRegion, true);
        logger.logln("I Control  " + Arrays.toString(myRegions.toArray()), true);
        logger.logln("I can See " + Arrays.toString(adjacentRegions.toArray()), true);
        myIterator = new BasicDealIterator(this::get, logger);
    }

    private AnalysedPlan getCurrentPlan() {
        if (currentPlan == null) {
            logger.logln("Retrieving Plan ", true);
            this.currentPlan = getCurrentPlan.get();
            this.orderIterator = Utility.Lists.createSortedList(currentPlan.getPlan().getMyOrders(), Comparator.comparing(this::getOrderPriority)).iterator();
            if (this.orderIterator.hasNext()) {
                currentOrder = this.orderIterator.next();
            }
        }
        return currentPlan;
    }

    private int getOrderPriority(Order t) {
        if (t instanceof MTOOrder) {
            return 0;
        } else if (t instanceof HLDOrder) {
            return 1;
        } else {
            return 2;
        }
    }

    private BasicDeal get() {
        logger.logln("Searching for Support Order ", true);
        getCurrentPlan();
        while (currentOrder != null) {//this.orderIterator.hasNext() ||
            if (currentOrder instanceof MTOOrder) {
                MTOOrder mtoOrder = (MTOOrder) currentOrder;
                if (mtoAdjacentTargets == null) {
                    mtoAdjacentTargets = mtoOrder.getDestination().getAdjacentRegions().stream()
                            .filter(region -> negotiatingPowers.contains(getGame().getController(region)))
                            .filter(region -> !getGame().getController(region).getName().equals(this.currentPlan.getInfo().getMe().asString()))
                            .collect(Collectors.toList());
                }
                if (mtoAdjacentTargets.isEmpty()) {
                    //All targets processed for this order
                    mtoAdjacentTargets = null;
                } else {
                    BasicDeal ret = buildSupportOrder(mtoOrder);
                    if (ret != null) {
                        return ret;
                    }
                }
            } else if (currentOrder instanceof HLDOrder) {
//                HLDOrder hldOrder = (HLDOrder) currentOrder;
//                if(hldAdjacentSupports == null){
//                    hldAdjacentSupports = hldOrder.getLocation().getAdjacentRegions().stream()
//                            .filter(region -> {
//                                final Power controller = getGame().getController(region);
//                                return negotiatingPowers.contains(controller) && !controller.getName().equals(this.currentPlan.getInfo().getMe().asString());
//                            })
//                            .collect(Collectors.toList());
//                }
//                if(!hldAdjacentSupports.isEmpty()){
//                    BasicDeal ret = buildHldSupportOrder(hldOrder);
//                    if(ret != null){
//                        return ret;
//                    }
//                }
            }
            if (this.orderIterator.hasNext()) {
                currentOrder = this.orderIterator.next();
            } else {
                currentOrder = null;
            }
        }
        logger.logln("Could not find Support Order", true);
        return null;
    }

    private BasicDeal buildHldSupportOrder(HLDOrder hldOrder) {
        Region supporter = hldAdjacentSupports.remove(0);
        logger.logln("Requesting Hold Support From : " + supporter + " for " + hldOrder, true);
        Optional<Region> getDmz = dmzRegion.isPresent() ? dmzRegion : (adjacentRegions.stream()
                .filter(region -> !myRegions.contains(region))
                .filter(region -> !currentPlan.getTargets().contains(region)).findFirst());
        final BasicDeal[] ret = {null};
        getDmz.ifPresent(dmz ->
                ret[0] = new BasicDeal(
                        Collections.singletonList(
                                new OrderCommitment(getGame().getYear(), getGame().getPhase(), new SUPOrder(getGame().getController(supporter), supporter, hldOrder))
                        ),
                        Collections.singletonList(
                                new DMZ(getGame().getYear(), getGame().getPhase(), Collections.singletonList(this.currentPlan.getInfo().getMe().asPower()), Collections.singletonList(dmz.getProvince()))
                        )
                )
        );
        return ret[0];
    }

    private BasicDeal buildSupportOrder(MTOOrder mtoOrder) {
        Region supportRegion = mtoAdjacentTargets.remove(0);
        if (!mtoOrder.getDestination().getAdjacentRegions().contains(supportRegion)) {
            logger.logln("Error:  unit in " + supportRegion.getName() + " cannot reach destination in " + mtoOrder.getDestination() + " to support.");
            return null;
        }
        logger.logln("Requesting Move Support From : " + supportRegion + " for " + mtoOrder, true);
        Optional<Region> getDmz = dmzRegion.isPresent() ? dmzRegion : (adjacentRegions.stream()
                .filter(region -> !myRegions.contains(region))
                .filter(region -> !currentPlan.getTargets().contains(region)).findFirst());
        final BasicDeal[] ret = {null};
        getDmz.ifPresent(dmz ->
                ret[0] = new BasicDeal(
                        Collections.singletonList(
                                new OrderCommitment(getGame().getYear(), getGame().getPhase(), new SUPMTOOrder(getGame().getController(supportRegion), supportRegion, mtoOrder))
                        ),
                        Collections.singletonList(
                                new DMZ(getGame().getYear(), getGame().getPhase(), Collections.singletonList(this.currentPlan.getInfo().getMe().asPower()), Collections.singletonList(dmz.getProvince()))
                        )
                )
        );
        return ret[0];
    }

    private Game getGame() {
        return this.currentPlan
                .getInfo()
                .getGame();
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.currentPlan = null;
        this.currentOrder = null;
        this.orderIterator = null;
        this.mtoAdjacentTargets = null;
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        reset(confirmedDeals);
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.myIterator;
    }
}
