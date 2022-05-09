package blake.bot.suppliers;

import blake.bot.utility.HashedPower;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class DefensiveDMZSupplierTest {

    Game game = DiplomacyGameBuilder.createDefaultGame();


    @Test
    public void testIterator() {
        List<BasicDeal> dmzs = new DefensiveDMZSupplier(
                game,
                new HashedPower(game.getPower("AUS")),
                new HashedPower(game.getPower("RUS")),
                Collections.emptyList()
        ).stream().collect(Collectors.toList());
        assertEquals(2, dmzs.size());
        dmzs.sort(Comparator.comparing((deal) -> deal.getDemilitarizedZones().get(0).getProvinces().get(0).getName())); // Alphabetise by first dmz province
        assertEquals(game.getRegion("GALAMY").getProvince(), dmzs.get(0).getDemilitarizedZones().get(0).getProvinces().get(0));
        assertEquals(game.getRegion("RUMAMY").getProvince(), dmzs.get(1).getDemilitarizedZones().get(0).getProvinces().get(0));
    }


    @Test
    public void testIteratorExistingDMZs() {
        List<BasicDeal> dmzs = new DefensiveDMZSupplier(
                game,
                new HashedPower(game.getPower("AUS")),
                new HashedPower(game.getPower("RUS")),
                Collections.singletonList(
                        new DMZ(
                                game.getYear(), game.getPhase(),
                                Collections.singletonList(game.getPower("RUS")),
                                Collections.singletonList(
                                        game.getRegion("RUMAMY").getProvince()
                                )
                        )
                )
        ).stream().collect(Collectors.toList());
        assertEquals(1, dmzs.size());
        assertEquals(game.getRegion("GALAMY").getProvince(), dmzs.get(0).getDemilitarizedZones().get(0).getProvinces().get(0));
//        assertEquals(game.getRegion("RUMAMY").getProvince(), dmzs.get(1).getDemilitarizedZones().get(0).getProvinces().get(0));
    }
}