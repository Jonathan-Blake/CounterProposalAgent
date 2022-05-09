package blake.bot.suppliers;

import blake.bot.analyser.PlanCache;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EagerLimitedProposalSupplier implements DealGenerator {
    private final DealGenerator supplier;
    private final int size;
    private final PlanCache planCache;
    private final List<BasicDeal> generatedDeals;
    private final BasicDealIterator iterator = new BasicDealIterator(this::get);

    public EagerLimitedProposalSupplier(DealGenerator supplier, int size, PlanCache planCache) {
        this.supplier = supplier;
        this.size = size;
        generatedDeals = new ArrayList<>(size);
        this.planCache = planCache;
    }

    private BasicDeal get() {
        while (supplier.iterator().hasNext() && generatedDeals.size() < size) {
            generatedDeals.add(supplier.iterator().next());
        }
        if (generatedDeals.isEmpty()) {
            return null;
        } else {

            generatedDeals.sort((o1, o2) -> planCache.getDealComparator().compare(o1, o2));
            return generatedDeals.remove(0);
        }
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals) {
        this.supplier.reset(confirmedDeals);
        this.generatedDeals.clear();
    }

    @Override
    public void reset(List<BasicDeal> confirmedDeals, Plan newPlan) {
        this.supplier.reset(confirmedDeals, newPlan);
        this.generatedDeals.clear();
    }

    @Override
    public Iterator<BasicDeal> iterator() {
        return this.iterator;
    }
}
