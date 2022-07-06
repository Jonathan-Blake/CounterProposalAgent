package blake.bot;

import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.*;

public class PreferedOpenings {
    public static final int ITER = 1000;
    static Game game = DiplomacyGameBuilder.createDefaultGame();
    private static List<BasicDeal> commitments = Collections.emptyList();

    public static void main(String[] args) {
        game.getProvince("GOB").getRegions().forEach(region -> System.out.println(region.getAdjacentRegions()));
        commitments = Arrays.asList(new BasicDeal(
                Collections.emptyList(),
                Arrays.asList(
                        new DMZ(game.getYear(), game.getPhase(), Arrays.asList(game.getPower("RUS")), Arrays.asList(game.getProvince("BLA")))
                )
        ));
        game.getPowers().forEach(PreferedOpenings::printPreferredMoves);
    }

    private static void printPreferredMoves(Power power) {
        System.out.println(power.getName());
        HashMap<String, Integer> moveMap = new HashMap<>();
        for (int i = 0; i < ITER; i++) {
            Plan moves = new DBraneTactics().determineBestPlan(game,
                    power,
                    commitments,
                    new ArrayList<>(Arrays.asList(game.getPower("FRA"))));
            moveMap.merge(
                    moves.getMyOrders().toString(),
                    1,
                    Integer::sum
            );
        }
        moveMap.forEach((key, val) -> System.out.println(key + ": " + val));
    }
}
