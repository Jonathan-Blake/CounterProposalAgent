package blake.bot.analyser;

import blake.bot.utility.HashedPower;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.ArrayList;
import java.util.List;

public class PlanInfoBuilder {

    private Game game;
    private HashedPower me;
    private List<BasicDeal> commitments;
    private BasicDeal deal;
    private List<Power> alliance;

    public PlanInfoBuilder() {
        alliance = new ArrayList<>();
    }

    public PlanInfo build() {
        assert (game != null);
        assert (me != null);
        assert (commitments != null);
        assert (deal != null);
        return new PlanInfo(this.game, this.me, this.commitments, this.deal, this.alliance);
    }

    public PlanInfoBuilder game(Game game) {
        this.game = game;
        return this;
    }

    public PlanInfoBuilder me(HashedPower hashedPower) {
        this.me = hashedPower;
        return this;
    }

    public PlanInfoBuilder commitments(List<BasicDeal> commitments) {
        this.commitments = commitments;
        return this;
    }

    public PlanInfoBuilder deal(BasicDeal deal) {
        this.deal = deal;
        return this;
    }

    public PlanInfoBuilder alliance(List<Power> alliance) {
        this.alliance = alliance;
        return this;
    }
}
