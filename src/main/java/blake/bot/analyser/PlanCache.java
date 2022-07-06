package blake.bot.analyser;

import blake.bot.utility.DatedObject;
import blake.bot.utility.HashedPower;
import blake.bot.utility.JavaDumbbot;
import blake.bot.utility.Utility;
import ddejonge.bandana.dbraneTactics.DBraneTactics;
import ddejonge.bandana.dbraneTactics.Plan;
import ddejonge.bandana.negoProtocol.BasicDeal;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Region;

import java.util.*;
import java.util.function.Predicate;

public class PlanCache {

    private static int dumbBotAccepts = 0;
    private static int dumbBotRejects = 0;
    private static int planIsBetter;
    private static int planIsWorse;
    private static int futureAccepts;
    private static int futureRejects;
    private final DBraneTactics tactics;
    Power me;
    Game game;
    AnalysedPlan noDealPlan;
    private AnalysedPlan alliancePlan;
    private List<BasicDeal> commitments;
    private JavaDumbbot dumbBotAnalysis;
    private final Comparator<AnalysedPlan> planComparator = (o1, o2) -> {
        int dbraneComp = Comparator.comparing(AnalysedPlan::getDBraneValue).compare(o1, o2);
        return dbraneComp != 0 ?
                dbraneComp :
                Comparator.<AnalysedPlan>comparingInt(plan -> plan.getDumbbotValue(currentDumbotValues())).compare(o1, o2);
    };
    SortedMap<AnalysedPlan, PlanInfo> plans = new TreeMap<>(planComparator);

    public PlanCache(DBraneTactics tactics) {
        this.tactics = tactics;
    }

    public static int getFutureAccepts() {
        return futureAccepts;
    }

    public static void setFutureAccepts(int dumbBotAccepts) {
        PlanCache.futureAccepts = dumbBotAccepts;
    }

    public static int getFutureRejects() {
        return futureRejects;
    }

    public static void setFutureRejects(int dumbBotRejects) {
        PlanCache.futureRejects = dumbBotRejects;
    }

    public static int getDumbBotAccepts() {
        return dumbBotAccepts;
    }

    public static void setDumbBotAccepts(int dumbBotAccepts) {
        PlanCache.dumbBotAccepts = dumbBotAccepts;
    }

    public static int getDumbBotRejects() {
        return dumbBotRejects;
    }

    public static void setDumbBotRejects(int dumbBotRejects) {
        PlanCache.dumbBotRejects = dumbBotRejects;
    }

    public static int getPlanIsBetter() {
        return planIsBetter;
    }

    public static void incrementPlanIsBetter() {
        PlanCache.planIsBetter++;
    }

    public static int getPlanIsWorse() {
        return planIsWorse;
    }

    public static void incrementPlanIsWorse() {
        PlanCache.planIsWorse++;
    }

    public AnalysedPlan analysePlan(PlanInfo planInfo) {
        AnalysedPlan ret = new AnalysedPlan(
                this.tactics.determineBestPlan(
                        planInfo.getGame(),
                        planInfo.getMe().asPower(),
                        planInfo.getCommitments(),
                        planInfo.getAlliesAsPower()),
                planInfo);
        if (ret.getPlan() != null && Utility.Plans.testConsistency(planInfo.getDeal(), this.game, planInfo.getCommitments())) {
            plans.put(ret, planInfo);
            return ret;
        } else {
            return null;
        }
    }

    public AnalysedPlan lookupDeal(BasicDeal basicDeal) {
        return plans.entrySet().stream()
                .filter(entry -> entry.getValue().getDeal().equals(basicDeal)).map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> analysePlan(new PlanInfo(game, new HashedPower(me), commitments, basicDeal, Collections.emptyList())));
    }

    public boolean betterThanAlliancePlan(AnalysedPlan proposal) {
        final AnalysedPlan currentPlan = this.getAllianceAnalysedPlan();
        int dbraneComp = Comparator.comparing(AnalysedPlan::getDBraneValue).compare(proposal, currentPlan);
        int ret;
        if (dbraneComp != 0) {
            ret = dbraneComp;
            if (ret > 0) {
                PlanCache.incrementPlanIsBetter();
            } else {
                PlanCache.incrementPlanIsWorse();
            }
            System.out.println(" following DBrane comp " + proposal.getDBraneValue() + " " + currentPlan.getDBraneValue());
        } else {
            ret = Integer.compare(
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(proposal.getExpectedResult()),
                            this.me,
                            proposal.getInfo().getCommitments()
                    ).getValue(),
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(currentPlan.getExpectedResult()),
                            this.me,
                            currentPlan.getInfo().getCommitments()
                    ).getValue());
            if (ret > 0) {
                setFutureAccepts(getFutureAccepts() + 1);
            } else {
                setFutureRejects(getFutureRejects() + 1);
            }
        }

        return ret > 0;
    }

    private AnalysedPlan getAllianceAnalysedPlan() {
        return this.alliancePlan;
    }

    public boolean betterThanNoDeal(AnalysedPlan proposal) {
        final AnalysedPlan currentPlan = this.getNoDealAnalysedPlan();
        int dbraneComp = Comparator.comparing(AnalysedPlan::getDBraneValue).compare(proposal, currentPlan);
        int ret;
        if (dbraneComp != 0) {
            ret = dbraneComp;
            if (ret > 0) {
                PlanCache.incrementPlanIsBetter();
            } else {
                PlanCache.incrementPlanIsWorse();
            }
            System.out.println(" following DBrane comp " + proposal.getDBraneValue() + " " + currentPlan.getDBraneValue());
        } else {
            ret = Integer.compare(
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(proposal.getExpectedResult()),
                            this.me,
                            proposal.getInfo().getCommitments()
                    ).getValue(),
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(currentPlan.getExpectedResult()),
                            this.me,
                            currentPlan.getInfo().getCommitments()
                    ).getValue());
            if (ret > 0) {
                setFutureAccepts(getFutureAccepts() + 1);
            } else {
                setFutureRejects(getFutureRejects() + 1);
            }
        }
//        if(ret ==0 ){
//            ret = Integer.compare(
//                    proposal.getDumbbotValue(calculateDumbbot(proposal.getExpectedResult())),
//                    currentPlan.getDumbbotValue(calculateDumbbot(currentPlan.getExpectedResult())));
//
//            if (ret > 0) {
//                setDumbBotAccepts(getDumbBotAccepts() + 1);
//            } else {
//                setDumbBotRejects(getDumbBotRejects() + 1);
//            }
//        }

        return ret > 0;
    }

    private JavaDumbbot calculateDumbbot(Game expectedResult) {
        return new JavaDumbbot(expectedResult, this.me, false);
    }

    private Map<Region, Integer> currentDumbotValues() {
        return this.dumbBotAnalysis.getDestinationValue();
    }

    public Comparator<BasicDeal> getDealComparator() {
        return (o1, o2) -> {
            AnalysedPlan o1Plan = lookupDeal(o1);
            AnalysedPlan o2Plan = lookupDeal(o2);

            return this.planComparator.compare(o1Plan, o2Plan);
        };
    }

    public void setNextTurn(Game game, Power me, List<BasicDeal> confirmedDeals) {
        this.game = game;
        this.me = me;
        this.commitments = confirmedDeals;
        this.dumbBotAnalysis = new JavaDumbbot(this.game, this.me, true);
        this.removePlan(
                planInfo -> Utility.Dates.isHistory(new DatedObject(planInfo), new DatedObject(this.game))
        );
    }

    public void removePlan(Predicate<PlanInfo> planInfoPredicate) {
        Set<AnalysedPlan> plansToRemove = new HashSet<>();
        plans.forEach((analysedPlan, planInfo) -> {
            if (planInfoPredicate.test(planInfo)) {
                plansToRemove.add(analysedPlan);
            }
        });
        plansToRemove.forEach(plan -> plans.remove(plan));

    }

    public Plan getNoDealPlan() {
        return this.noDealPlan.getPlan();
    }

    public void setNoDealPlan(AnalysedPlan analysePlan) {
        this.noDealPlan = analysePlan;
    }

    public AnalysedPlan getNoDealAnalysedPlan() {
        return this.noDealPlan;
    }

    public AnalysedPlan getAlliancePlan() {
        return this.alliancePlan;
    }

    public void setAlliancePlan(AnalysedPlan analysePlan) {
        this.alliancePlan = analysePlan;
    }

    public boolean betterOrEqualAllianceDeal(AnalysedPlan proposal) {
        final AnalysedPlan currentPlan = this.getAlliancePlan();
        int dbraneComp = Comparator.comparing(AnalysedPlan::getDBraneValue).compare(proposal, currentPlan);
        int ret;
        if (dbraneComp != 0) {
            ret = dbraneComp;
//            if (ret > 0) {
//                PlanCache.incrementPlanIsBetter();
//            } else {
//                PlanCache.incrementPlanIsWorse();
//            }
            System.out.println(" following DBrane comp for alliance " + proposal.getDBraneValue() + " " + currentPlan.getDBraneValue());
        } else {
            ret = Integer.compare(
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(proposal.getExpectedResult()),
                            this.me,
                            proposal.getInfo().getCommitments()
                    ).getValue(),
                    tactics.determineBestPlan(
                            AdvancedAdjudicator.advanceToMovementPhase(currentPlan.getExpectedResult()),
                            this.me,
                            currentPlan.getInfo().getCommitments()
                    ).getValue());
//            if (ret > 0) {
//                setDumbBotAccepts(getDumbBotAccepts() + 1);
//            } else {
//                setDumbBotRejects(getDumbBotRejects() + 1);
//            }
            if (ret == 0) {
                ret = Integer.compare(
                        proposal.getDumbbotValue(new JavaDumbbot(proposal.getInfo().getGame(), proposal.getInfo().getMe().asPower(), false)),
                        currentPlan.getDumbbotValue(new JavaDumbbot(currentPlan.getInfo().getGame(), currentPlan.getInfo().getMe().asPower(), false)));
            }
        }

        return ret >= 0;
    }
}
