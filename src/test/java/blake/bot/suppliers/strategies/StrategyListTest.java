package blake.bot.suppliers.strategies;

import blake.bot.utility.Utility;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyListTest {


    private Game game = DiplomacyGameBuilder.createDefaultGame();

    @Test
    void allRegisterMethodsAreConsistent() {
        StrategyRegister.REGISTER.getPlans().forEach(
                strategyPlan ->
                {
                    final BasicDeal build = strategyPlan.build(this.game);
                    System.out.println(strategyPlan.name);
                    assertNotNull(build);
                    assertTrue(
                            Utility.Plans.testConsistency(build, this.game, new ArrayList<>()),
                            () -> String.format("%s is inconsistent", strategyPlan.name));
                });
    }

    @Test
    void messagesAreSorted() {
        Power me = game.getPower("ENG");
        List<Power> negotiators = Arrays.asList(
                game.getPower("GER"), game.getPower("RUS"), me
        );
        StrategyList test = StrategyRegister.REGISTER
                .filter(strategyPlan -> strategyPlan.participants().contains(me.getName()))
                .filter(strategyPlan -> Utility.Lists.mapList(negotiators, Power::getName).containsAll(strategyPlan.participants()))
                .filter(strategyPlan -> !strategyPlan.targets().contains(me.getName()))
                .sort(Comparator.comparingInt(strategyPlan -> strategyPlan.getAdjustedWeight(negotiators)));
        for (StrategyPlan plan : test.getPlans()) {
            System.out.println(plan.getAdjustedWeight(negotiators) + " " + plan + " ");
        }
    }

}