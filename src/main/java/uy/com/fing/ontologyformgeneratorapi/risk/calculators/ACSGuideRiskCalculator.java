package uy.com.fing.ontologyformgeneratorapi.risk.calculators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskCalculation;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskLevel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModelCalculatorType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRQuestionAnswersInstancesEnum.*;
import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRQuestionInstancesEnum.*;

/**
 * Calculadora de MSP UY siguiendo reglas de la guìa.
 */
@Component
public class ACSGuideRiskCalculator extends RiskCalculator {


    public ACSGuideRiskCalculator(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public Stream<WomanExample> getWomanExamples() {
        return Arrays.stream(WomanExampleNames.values());
    }

    @Override
    public WomanExample getWomanExampleFromString(String womanExampleString) {
        return WomanExampleNames.valueOf(womanExampleString);
    }

    @Override
    public RiskCalculation doRiskCalculation(Map<String, String> womanFormData) {
        RiskLevel resultRisk = RiskLevel.MEDIUM;

        if(ACS_CHEST_RADIOTERAPY_YOUNG_AGE_YES.getUri().equals(womanFormData.get(ACS_CHEST_RADIOTERAPY_YOUNG_AGE_QUESTION.getUri())) ||
                ACS_HISTORY_BREAST_CANCER_YES.getUri().equals(womanFormData.get(ACS_HISTORY_BREAST_CANCER_QUESTION.getUri())) ||
                ACS_GENETIC_MUTATION_YES.getUri().equals(womanFormData.get(ACS_GENETIC_MUTATION_QUESTION.getUri()))) {
            resultRisk = RiskLevel.HIGH;
        }

        return RiskCalculation.builder()
                .riskLevel(resultRisk)
                .riskModelCalculatorType(getRiskModelCalculator())
                .riskModel(getRiskModel())
                .build();
    }

    @Override
    public RiskModelCalculatorType getRiskModelCalculator() {
        return RiskModelCalculatorType.ACS_GUIDELINE;
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.ACS;
    }

    @AllArgsConstructor
    @Getter
    private enum WomanExampleNames implements WomanExample {
        ACS_WOMAN_1_HIGH("/mocks/acs/example1.json", "mocks/acs/risk_high.json"),
        ACS_WOMAN_2_MID_40("/mocks/acs/example2.json", "mocks/acs/risk_mid.json"),
        ACS_WOMAN_3_MID_60("/mocks/acs/example3.json", "mocks/acs/risk_mid.json"),
        ACS_WOMAN_4_MID_70("/mocks/acs/example4.json", "mocks/acs/risk_mid.json")
        ;

        private final String path;
        private final String calcPath;
    }
}
