package blake.bot.suppliers;

import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;

public class CounterProposalDeal extends BasicDeal {
    private final DiplomacyProposal previousProposal;

    public CounterProposalDeal(BasicDeal ret, DiplomacyProposal currentConsideredProposal) {
        super(ret.getOrderCommitments(), ret.getDemilitarizedZones());
        this.previousProposal = currentConsideredProposal;
    }

    public DiplomacyProposal getPreviousProposal() {
        return previousProposal;
    }
}
