package blake.bot.suppliers.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyRegister {
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

    private StrategyRegister() {
    }
}