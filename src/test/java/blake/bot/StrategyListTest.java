package blake.bot;


import blake.bot.suppliers.strategies.StrategyList;
import blake.bot.utility.Utility;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyListTest {

    private Game game = DiplomacyGameBuilder.createDefaultGame();

    @Test
    void allRegisterMethodsAreConsistent() {

        StrategyList.REGISTER.getPlans().forEach(
                strategyPlan ->
                {
                    final BasicDeal build = strategyPlan.build(this.game);
                    System.out.println(strategyPlan.name);
//                    build.getOrderCommitments().forEach(orderCommitment -> System.out.println(orderCommitment));
//                    System.out.println(build.getDemilitarizedZones());
                    assertNotNull(build);
                    assertTrue(
                            Utility.Plans.testConsistency(build, this.game, new ArrayList<>()),
                            () -> String.format("%s is inconsistent", strategyPlan.name));
                });
    }

}