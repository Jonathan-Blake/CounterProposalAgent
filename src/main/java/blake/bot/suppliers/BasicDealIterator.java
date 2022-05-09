package blake.bot.suppliers;

import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.tools.Logger;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class BasicDealIterator implements Iterator<BasicDeal> {
    private final Supplier<BasicDeal> supplier;
    private final Logger verbose;
    private BasicDeal next = null;

    public BasicDealIterator(Supplier<BasicDeal> supplier) {
        this.supplier = supplier;
        verbose = null;
    }

    public BasicDealIterator(Supplier<BasicDeal> supplier, Logger logger) {
        this.supplier = supplier;
        this.verbose = logger;
    }


    @Override
    public boolean hasNext() {
        if (next == null) {
            next = get();
        }
        return next != null;
    }

    @Override
    public BasicDeal next() {
        BasicDeal temp = null;
        if (hasNext()) {
            temp = this.next;
            this.next = null;
        }
        if (temp == null) {
            throw new NoSuchElementException();
        }
        if (verbose != null) {
            this.verbose.log("Returning Deal " + temp, true);
        }
        return temp;
    }

    private BasicDeal get() {
        return this.supplier.get();
    }
}
