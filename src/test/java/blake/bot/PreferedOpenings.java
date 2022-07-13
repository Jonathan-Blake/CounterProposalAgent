package blake.bot;

import blake.bot.suppliers.strategies.StrategyRegister;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PreferedOpenings {
    public static final int ITER = 1000;
    static Game game = DiplomacyGameBuilder.createDefaultGame();
    private static List<BasicDeal> commitments = Collections.emptyList();

    public static void main(String[] args) {

        printStrategyWeights();

        game.getRegions().forEach(region -> System.out.println(region + " " + region.getAdjacentRegions()));
        commitments = Collections.singletonList(new BasicDeal(
                Collections.emptyList(),
                Collections.singletonList(
                        new DMZ(game.getYear(), game.getPhase(), Collections.singletonList(game.getPower("RUS")), Collections.singletonList(game.getProvince("BLA")))
                )
        ));
        game.getPowers().forEach(PreferedOpenings::printPreferredMoves);

    }

    private static void printStrategyWeights() {
        HashMap<Integer, ArrayList<String>> map = new HashMap<>();
        StrategyRegister.REGISTER.getPlans().forEach(
                strategyPlan -> map.merge(strategyPlan.getAdjustedWeight(Collections.emptyList()),
                        new ArrayList<>(Collections.singletonList(strategyPlan.name)),
                        (o, n) -> {
                            o.addAll(n);
                            return o;
                        }
                ));
        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    private static void printPreferredMoves(Power power) {
        System.out.println(power.getName());
        HashMap<String, Integer> moveMap = new HashMap<>();
        for (int i = 0; i < ITER; i++) {
            Plan moves = new DBraneTactics().determineBestPlan(game,
                    power,
                    commitments,
                    new ArrayList<>(Collections.singletonList(game.getPower("FRA"))));
            moveMap.merge(
                    moves.getMyOrders().toString(),
                    1,
                    Integer::sum
            );
        }
        moveMap.forEach((key, val) -> System.out.println(key + ": " + val));
    }
}
