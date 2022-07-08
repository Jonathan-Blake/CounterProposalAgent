package blake.bot.suppliers.strategies;

import blake.bot.utility.Utility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StrategyList {

    public static final StrategyList REGISTER;

    static {
        StrategyList tempRegister;
        final List<String> fileAddresses = Arrays.asList(
                "AusRusStrategies.json",
                "AusTurStrategies.json",
                "EngAusStrategies.json",
                "EngFraStrategies.json",
                "EngGerStrategies.json",
                "EngItaStrategies.json",
                "EngRusStrategies.json",
                "EngTurStrategies.json",
                "FraAusStrategies.json",
                "FraGerStrategies.json",
                "FraItaStrategies.json",
                "FraRusStrategies.json",
                "GerAusStrategies.json",
                "GerRusStrategies.json",
                "GerTurStrategies.json",
                "ItaAusStrategies.json",
                "ItaGerStrategies.json",
                "ItaRusStrategies.json",
                "ItaTurStrategies.json",
                "PregenStrategies.json",
                "RusTurStrategies.json",
                "TripleAllianceStrategies.json"
        );
        final ArrayList<StrategyList> fileContents = new ArrayList<>(fileAddresses.size());
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClassLoader classLoader = StrategyList.class.getClassLoader();
            for (String pathname : fileAddresses) {
                InputStream in = classLoader.getResourceAsStream(pathname);
                fileContents.add(objectMapper.readValue(in, StrategyList.class));
            }
            tempRegister = new StrategyList(fileContents.stream()
                    .flatMap(strategyList -> strategyList.getPlans().stream())
                    .collect(Collectors.toList())
            );
        } catch (java.io.IOException e) {
            e.printStackTrace();
            tempRegister = new StrategyList(Collections.emptyList());
        }
        REGISTER = tempRegister;
    }

    private final List<StrategyPlan> plans;

    public StrategyList(List<StrategyPlan> strategyPlans) {
        this.plans = Collections.unmodifiableList(strategyPlans);
    }

    @JsonCreator()
    public StrategyList(@JsonProperty("plans") StrategyPlan... strategyPlans) {
        this(Arrays.asList(strategyPlans));
    }

    public StrategyList filter(Predicate<StrategyPlan> filter) {
        return new StrategyList(plans.stream().filter(filter).collect(Collectors.toList()));
    }

    public StrategyList sort(Comparator<StrategyPlan> comparator) {
        return new StrategyList(Utility.Lists.createSortedList(this.plans, comparator));
    }

    public List<StrategyPlan> getPlans() {
        return this.plans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyList that = (StrategyList) o;
        return plans.equals(that.plans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plans);
    }
}
