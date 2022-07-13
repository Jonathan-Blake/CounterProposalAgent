package blake.bot.analyser;


import blake.bot.utility.DatedObject;
import blake.bot.utility.Utility;
import ddejonge.bandana.gameBuilder.DiplomacyGameBuilder;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Phase;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.orders.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AdvancedAdjudicatorTest {

    @Test
    public void correctlySetsDates() {
        Game currentGame = DiplomacyGameBuilder.createDefaultGame();
        for (int i = 0; i < 20; i++) {
            Game nextGame = AdvancedAdjudicator.determineOutcome(currentGame, Collections.emptyList());
            assertEquals(Utility.Dates.getNextDate(new DatedObject(currentGame)), new DatedObject(nextGame));
            currentGame = nextGame;
        }
    }

    @Test
    public void handlesNoOrders() {
        Game currentGame = DiplomacyGameBuilder.createDefaultGame();
        Game noMovement = AdvancedAdjudicator.determineOutcome(currentGame, Collections.emptyList());
        for (Region region : currentGame.getRegions()) {
            System.out.println(region);
            assertEquals(currentGame.getController(region), noMovement.getController(region));
        }
    }

    @Test
    public void handlesSingleOrder() {
        Game currentGame = DiplomacyGameBuilder.createDefaultGame();
        Game noMovement = AdvancedAdjudicator.determineOutcome(currentGame, Collections.singletonList(new MTOOrder(
                currentGame.getPower("RUS"),
                currentGame.getRegion("WARAMY"),
                currentGame.getRegion("GALAMY"))));
        for (Region region : currentGame.getRegions()) {
            if (region.getName().equals("WARAMY")) {
                assertNull(noMovement.getController(noMovement.getRegion("WARAMY")));
            } else if (region.getName().equals("GALAMY")) {
                assertEquals("RUS", noMovement.getController(noMovement.getRegion("GALAMY")).getName());
            } else {
                assertEquals(currentGame.getController(region), noMovement.getController(region));
            }
            System.out.println(region);
        }
    }

    @Test
    public void handlesFailedOrder() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("RUS", "WARAMY");
        builder.placeUnit("AUS", "BUDAMY");
        Game currentGame = builder.createMyGame();
        Game failedMovement = AdvancedAdjudicator.determineOutcome(currentGame, Arrays.asList(
                new MTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("WARAMY"), currentGame.getRegion("GALAMY")),
                new MTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"), currentGame.getRegion("GALAMY"))
        ));
        for (Region region : currentGame.getRegions()) {
            assertEquals(currentGame.getController(region), failedMovement.getController(region));
        }
    }

    @Test
    public void invadingWithSupportOrder() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("AUS", "BUDAMY");
        builder.placeUnit("RUS", "VIEAMY");
        builder.placeUnit("AUS", "GALAMY");
        Game currentGame = builder.createMyGame();
        final MTOOrder mtoOrder = new MTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), currentGame.getRegion("VIEAMY"));
        Game failedMovement = AdvancedAdjudicator.determineOutcome(currentGame, Arrays.asList(
                mtoOrder,
                new HLDOrder(currentGame.getPower("RUS"), currentGame.getRegion("VIEAMY")),
                new SUPMTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"), mtoOrder)
        ));
        for (Region region : currentGame.getRegions()) {
            if (region.getName().equals("VIEAMY")) {
                assertEquals(failedMovement.getPower("AUS"), failedMovement.getController(region));
            } else if (region.getName().equals("GALAMY")) {
                assertNull(failedMovement.getController(region));
            } else {
                assertEquals(currentGame.getController(region), failedMovement.getController(region));
            }
        }
    }

    @Test
    public void invadingWithSupportOrderImplicitHold() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("AUS", "BUDAMY");
        builder.placeUnit("RUS", "VIEAMY");
        builder.placeUnit("AUS", "GALAMY");
        Game currentGame = builder.createMyGame();
        final MTOOrder mtoOrder = new MTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), currentGame.getRegion("VIEAMY"));
        Game failedMovement = AdvancedAdjudicator.determineOutcome(currentGame, Arrays.asList(
                mtoOrder,
                new SUPMTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"), mtoOrder)
        ));
        for (Region region : currentGame.getRegions()) {
            if (region.getName().equals("VIEAMY")) {
                assertEquals(failedMovement.getPower("AUS"), failedMovement.getController(region));
            } else if (region.getName().equals("GALAMY")) {
                assertNull(failedMovement.getController(region));
            } else {
                assertEquals(currentGame.getController(region), failedMovement.getController(region));
            }
        }
    }

    @Test
    public void invadedSupportHLDOrderNotDisplaced() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("AUS", "BUDAMY");
        builder.placeUnit("RUS", "VIEAMY");
        builder.placeUnit("AUS", "GALAMY");
        Game currentGame = builder.createMyGame();
//        final MTOOrder mtoOrder = new MTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), currentGame.getRegion("VIEAMY"));
        Game failedMovement = AdvancedAdjudicator.determineOutcome(currentGame, Arrays.asList(
                new SUPOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), new HLDOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"))),
                new MTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("VIEAMY"), currentGame.getRegion("GALAMY"))
        ));
        for (Region region : currentGame.getRegions()) {
            assertEquals(currentGame.getController(region), failedMovement.getController(region));
        }
    }

    @Test
    public void invadedSupportHLDOrderDisplaced() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("AUS", "BUDAMY");
        builder.placeUnit("AUS", "GALAMY");
        builder.placeUnit("RUS", "VIEAMY");
        builder.placeUnit("RUS", "BOHAMY");
        builder.placeUnit("RUS", "SERAMY");
        Game currentGame = builder.createMyGame();
//        final MTOOrder mtoOrder = new MTOOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), currentGame.getRegion("VIEAMY"));
        final MTOOrder mtoOrder = new MTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("BOHAMY"), currentGame.getRegion("GALAMY"));
        Game failedMovement = AdvancedAdjudicator.determineOutcome(currentGame, Arrays.asList(
                new SUPOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"), new HLDOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"))),
                new SUPOrder(currentGame.getPower("AUS"), currentGame.getRegion("BUDAMY"), new HLDOrder(currentGame.getPower("AUS"), currentGame.getRegion("GALAMY"))),
                mtoOrder,
                new SUPMTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("VIEAMY"), mtoOrder),
                new MTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("SERAMY"), currentGame.getRegion("BUDAMY"))
        ));
        for (Region region : currentGame.getRegions()) {
            if (region.getName().equals("BOHAMY")) {
                assertNull("Bohemia should be empty", failedMovement.getController(region));
            } else if (region.getName().equals("GALAMY")) {
                assertEquals("Russia invaded Gal", failedMovement.getPower("RUS"), failedMovement.getController(region));
            } else {
                assertEquals("No change in " + region.getName(), currentGame.getController(region), failedMovement.getController(region));
            }
        }
    }

    @Test
    public void handleGuessedOrders() {
        DiplomacyGameBuilder builder = new DiplomacyGameBuilder();
        builder.setPhase(Phase.SPR, 1901);
        builder.placeUnit("AUS", "BUDAMY");
        builder.placeUnit("AUS", "GALAMY");
        builder.placeUnit("RUS", "VIEAMY");
        builder.placeUnit("RUS", "BOHAMY");
        builder.placeUnit("RUS", "SERAMY");
        Game currentGame = builder.createMyGame();

        List<Order> moves = AdvancedAdjudicator.guessAllOrders(currentGame,
                Collections.singletonList(new BasicDeal(
//                         Collections.singletonList(new SUPMTOOrder(currentGame.getPower("RUS"), currentGame.getRegion(""), ))
                        Collections.emptyList(),
                        Collections.singletonList(new DMZ(
                                currentGame.getYear(),
                                currentGame.getPhase(),
                                Arrays.asList(currentGame.getPower("RUS"), currentGame.getPower("AUS")),
                                Collections.singletonList(currentGame.getProvince("TRI"))
                        ))
                ))
//                 Collections.emptyList()
        );
        System.out.println(currentGame.getProvince("TRI").getRegions());
        System.out.println(moves);
//        moves = Arrays.asList(
//                new MTOOrder(currentGame.getPower("RUS"), currentGame.getRegion("BOHAMY"), currentGame.getRegion("MUNAMY") )
//        );
        Game bestGuessOutcome = AdvancedAdjudicator.determineOutcome(currentGame, moves);
        for (Region region : currentGame.getRegions()) {
            System.out.println(region + " " + bestGuessOutcome.getController(region));
            if (region.getName().equals("BOHAMY")) {
                assertNull("Bohemia should be empty", bestGuessOutcome.getController(region));
            } else if (region.getName().equals("MUHAMY")) {
                assertEquals("Russia invaded MUH", bestGuessOutcome.getPower("RUS"), bestGuessOutcome.getController(region));
            } else if (region.getName().equals("BUDAMY")) {
                assertEquals("Russia invaded BUD", bestGuessOutcome.getPower("RUS"), bestGuessOutcome.getController(region));
            } else {
                assertEquals("No change in " + region.getName(), currentGame.getController(region), bestGuessOutcome.getController(region));
            }
        }
    }


    @Test
//    @Ignore("Takes too long, manually test for timing")
    public void timingOrderGuessing() {
        final int attempts = 1000;
        long[] times = new long[attempts];
        final Game defaultGame = DiplomacyGameBuilder.createDefaultGame();
        for (int i = 0; i < attempts; i++) {
            long start = System.currentTimeMillis();
            AdvancedAdjudicator.guessAllOrders(defaultGame, Collections.emptyList());
            long end = System.currentTimeMillis();
            long timeTaken = end - start;
            times[i] = timeTaken;
        }
        calculateMeanAndInterval(attempts, times);
    }

    @Test
//    @Ignore("Takes too long, manually test for timing")
    public void timingOrderGuessingWithAssumptions() {
        final int attempts = 1000;
        long[] times = new long[attempts];
        final Game defaultGame = DiplomacyGameBuilder.createDefaultGame();
        List<BasicDeal> assumptions = AdvancedAdjudicator.guessOrdersForPowers(defaultGame.getNonDeadPowers(), defaultGame);
        for (int i = 0; i < attempts; i++) {
            long start = System.currentTimeMillis();
            AdvancedAdjudicator.guessAllOrders(defaultGame, assumptions);
            long end = System.currentTimeMillis();
            long timeTaken = end - start;
            times[i] = timeTaken;
        }
        calculateMeanAndInterval(attempts, times);
    }

    private void calculateMeanAndInterval(int attempts, long[] times) {
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        long mean = sum / attempts;
        long squaredDifferenceSum = 0;
        for (long num : times) {
            final long l = num - mean;
            squaredDifferenceSum += l * l;
        }
        long variance = squaredDifferenceSum / attempts;
        double standardDeviation = Math.sqrt(variance);

        // value for 95% confidence interval, source: https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps
        double confidenceLevel = 1.96;
        double temp = confidenceLevel * standardDeviation / Math.sqrt(attempts);
        System.out.println(mean + " +- " + standardDeviation + " confidence = " + temp);
    }
}