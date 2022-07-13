package blake.bot;

import blake.bot.suppliers.strategies.*;
import es.csic.iiia.fabregues.dip.board.Phase;

import java.util.Arrays;
import java.util.Collections;

public class OldRegister {
    private static int startYear = 1901;
    public static StrategyList OldREGISTER =
            new StrategyList(
                    new StrategyPlan(
                            "ENG Northern Opening, Fra Support",
                            10,
                            Arrays.asList("GER"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ENG", "EDIFLT", "NWGFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ENG", "LONFLT", "NTHFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "PARAMY", "BURAMY"),
                                    new MTOOrderBuilder(startYear, Phase.FAL, "ENG", "NTHFLT", "BELFLT"),
                                    new SUPMTOOrderBuilder(startYear, Phase.FAL, "FRA", "BURAMY", "ENG", "NTHFLT", "BELFLT")
                            ),
                            Collections.emptyList()
                    ),
                    new StrategyPlan(
                            "GER Bliz, Hol version",
                            10,
                            Arrays.asList("Eng"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "KIEFLT", "HOLFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "BERAMY", "KIEAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "MUNAMY", "RUHAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "MOSAMY", "STPAMY"),
                                    new MTOOrderBuilder(startYear, Phase.FAL, "GER", "RUHAMY", "BELAMY"),
                                    new MTOOrderBuilder(startYear, Phase.FAL, "RUS", "STPAMY", "NWYAMY")
                            ),
                            Collections.emptyList()
                    ),

                    new StrategyPlan(
                            "Peace in center",
                            10,
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("GER"), Arrays.asList("TYR", "BOH")),
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("AUS"), Arrays.asList("VEN", "TYR", "BOH")),
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("ITA"), Arrays.asList("TRI", "TYR"))
                            )
                    ),

                    new StrategyPlan(
                            "Rus Octopus",
                            10,
                            Collections.singletonList("ENG"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "STPSCS", "GOBFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "MOSAMY", "STPAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "WARAMY", "GALAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "SEVFLT", "BLAFLT")
                            ),
                            Collections.emptyList()
                    ),

                    new StrategyPlan(
                            "Rus Tur Attack",
                            10,
                            Collections.singletonList("TUR"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "WARAMY", "UKRAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "SEVFLT", "BLAFLT")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Collections.singletonList("AUS"), Collections.singletonList("GAL")),
                                    new DMZBuilder(startYear, Phase.SPR, Collections.singletonList("TUR"), Collections.singletonList("BLA"))
                            )
                    ),

                    new StrategyPlan(
                            "Rus NthOpen Gal",
                            10,
                            Collections.singletonList("AUS"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "MOSAMY", "STPAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "WARAMY", "GALAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "SEVFLT", "RUMFLT")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Collections.singletonList("RUS"), Collections.singletonList("BLA")),
                                    new DMZBuilder(startYear, Phase.SPR, Collections.singletonList("TUR"), Collections.singletonList("BLA"))
                            )
                    ),

                    new StrategyPlan(
                            "Rus Ita Kill Austria",
                            10,
                            Collections.singletonList("AUS"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "WARAMY", "GALAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY", "TYRAMY")
                            ),
                            Arrays.asList(
                            )
                    ),

//                new StrategyPlan(
//                        "Rus AUS ITA Kill Turkey",
//                        10,
//                        Collections.singletonList("TUR"),
//                        Arrays.asList(
//                                new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "SEVFLT", "RUMFLT"),
//                                new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "MOSAMY", "SEVAMY"),
//                                new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "BUDAMY", "SERAMY"),
//                                new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "VIEAMY", "BUDAMY")
//                        ),
//                        Arrays.asList(
//                                new DMZBuilder(startYear, Phase.SPR, Arrays.asList("ITA"), Arrays.asList("TRI"))
//                        )
//                )

                    new StrategyPlan(
                            "Southern Hedgehog, GER DMZ",
                            10,
                            Arrays.asList("RUS", "TUR", "ITA"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "TRIFLT", "VENFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "VIEAMY", "GALAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "BUDAMY", "SERAMY")

                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("GER"), Arrays.asList("TYR", "TRI", "BOH"))
                            )
                    ),
                    new StrategyPlan(
                            "Southern Hedgehog, ITA DMZ",
                            10,
                            Arrays.asList("RUS", "TUR"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "VIEAMY", "GALAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "AUS", "BUDAMY", "SERAMY")

                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("ITA"), Arrays.asList("TYR", "TRI"))
                            )
                    ),
                    new StrategyPlan(
                            "ITA TYR Attack FRA DMZ",
                            10,
                            Arrays.asList("AUS"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY", "TYRAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "ROMAMY", "VENAMY")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("FRA"), Arrays.asList("PIE"))
                            )
                    ),
                    new StrategyPlan(
                            "ITA TYR Attack Turkey Support",
                            10,
                            Arrays.asList("AUS"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY", "TYRAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "ROMAMY", "VENAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "NAPFLT", "IONFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "TUR", "CONAMY", "BULAMY"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "TUR", "BULAMY", "SERAMY"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "ITA", "IONFLT", "GREFLT")
                            ),
                            Arrays.asList(
                            )
                    ),
                    new StrategyPlan(
                            "ITA FRA Attack Ger Support",
                            10,
                            Arrays.asList("FRA"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY", "PIEAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "ROMAMY", "VENAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "MUNAMY", "BURAMY"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "ITA", "PIEAMY", "MARAMY"),
                                    new SUPMTOOrderBuilder(startYear, Phase.AUT, "GER", "BURAMY", "ITA", "PIEAMY", "MARAMY")
                            ),
                            Arrays.asList(
                            )
                    ),
                    new StrategyPlan(
                            "ITA FRA Attack AUS Guarantee",
                            10,
                            Arrays.asList("FRA"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY", "PIEAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "ROMAMY", "VENAMY"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "ITA", "PIEAMY", "MARAMY")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("AUS"), Arrays.asList("VEN"))
                            )
                    ),
                    new StrategyPlan(
                            "ITA Sleeper Attack",
                            10,
                            Arrays.asList("FRA"),
                            Arrays.asList(
                                    new HLDOrderBuilder(startYear, Phase.SPR, "ITA", "VENAMY"),
                                    new HLDOrderBuilder(startYear, Phase.SPR, "ITA", "ROMAMY"),
                                    new HLDOrderBuilder(startYear, Phase.AUT, "ITA", "VENAMY"),
                                    new HLDOrderBuilder(startYear, Phase.AUT, "ITA", "ROMAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ITA", "NAPFLT", "IONFLT"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "ITA", "IONFLT", "TUNFLT"),
//                                    new BLDOrderBuilder(startYear, Phase.FAL, "ITA", "NAPFLT"),
                                    new MTOOrderBuilder(startYear + 1, Phase.SPR, "ITA", "VENAMY", "PIEAMY"),
                                    new MTOOrderBuilder(startYear + 1, Phase.SPR, "ITA", "ROMAMY", "VENAMY"),
                                    new MTOOrderBuilder(startYear + 1, Phase.SPR, "ITA", "TUNFLT", "WESFLT"),
                                    new MTOOrderBuilder(startYear + 1, Phase.SPR, "ITA", "NAPFLT", "TUNFLT")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.SPR, Arrays.asList("AUS"), Arrays.asList("VEN")),
                                    new DMZBuilder(startYear, Phase.AUT, Arrays.asList("AUS"), Arrays.asList("VEN")),
                                    new DMZBuilder(startYear + 1, Phase.SPR, Arrays.asList("AUS"), Arrays.asList("VEN")),
                                    new DMZBuilder(startYear + 1, Phase.AUT, Arrays.asList("AUS"), Arrays.asList("VEN"))
                            )
                    ),
                    new StrategyPlan(
                            "FRA Bur Opening Norwegian conflict",
                            10,
                            Arrays.asList("GER", "ENG"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "BREFLT", "MAOFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "MARAMY", "SPAAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "PARAMY", "BURAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "RUS", "MOSAMY", "STPAMY")
                            ),
                            Arrays.asList(
                            )
                    ),
                    new StrategyPlan(
                            "FRA GER Eng Attack",
                            10,
                            Arrays.asList("ENG"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "BREFLT", "ECHFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "PARAMY", "PICAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "MUNAMY", "RUHAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "KIEFLT", "DENFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "GER", "BERAMY", "KIEAMY"),
                                    new SUPMTOOrderBuilder(startYear, Phase.AUT, "GER", "RUHAMY", "GER", "KIEAMY", "HOLAMY"),
                                    new SUPMTOOrderBuilder(startYear, Phase.AUT, "FRA", "ECHFLT", "FRA", "PICAMY", "BELAMY")
                            ),
                            Arrays.asList(
                            )
                    ),
                    new StrategyPlan(
                            "FRA Eng Ger Attack",
                            10,
                            Arrays.asList("GER"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "BREFLT", "MAOFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "MARAMY", "BURAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "ENG", "LONFLT", "ECHFLT"),
                                    new MTOOrderBuilder(startYear, Phase.AUT, "ENG", "ECHFLT", "BELFLT")
                            ),
                            Arrays.asList(
                                    new DMZBuilder(startYear, Phase.AUT, Arrays.asList("FRA"), Arrays.asList("BEL"))
                            )
                    ),
                    new StrategyPlan(
                            "FRA Pie Opening",
                            10,
                            Arrays.asList("ITA"),
                            Arrays.asList(
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "BREFLT", "MAOFLT"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "MARAMY", "PIEAMY"),
                                    new MTOOrderBuilder(startYear, Phase.SPR, "FRA", "PARAMY", "GASAMY"),
                                    new SUPMTOOrderBuilder(startYear, Phase.AUT, "AUS", "TRIAMY", "FRA", "PIEAMY", "VENAMY")
                            ),
                            Arrays.asList(
                            )
                    )

            );
}
