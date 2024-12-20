package uy.com.fing.ontologyformgeneratorapi.risk;

import org.springframework.stereotype.Service;
import uy.com.fing.ontologyformgeneratorapi.risk.calculators.RiskCalculator;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskCalculation;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RiskCalculatorService {

    private final Map<RiskModel, RiskCalculator> riskCalculators;

    public RiskCalculatorService(Set<RiskCalculator> riskCalculatorSet) {
        riskCalculators = riskCalculatorSet.stream().collect(Collectors.toMap(
                RiskCalculator::getRiskModel, s -> s));

    }

    public RiskCalculation calculateRiskForModelAndData(String riskModelUri, Map<String, String> womanHistoryProps) {
        return riskCalculators.get(RiskModel.fromString(riskModelUri)).calculateRisk(womanHistoryProps);
    }

    public List<String> getWomanExampleNames(String riskModelUri) {
        return riskCalculators.get(RiskModel.fromString(riskModelUri)).getWomanExamples().map(RiskCalculator.WomanExample::name).toList();
    }

    public Map<String, String> getWomanFormDataFromExample(String riskModelUri, String womanExample) {
        return riskCalculators.get(RiskModel.fromString(riskModelUri)).getWomanInfo(womanExample);
    }
}
