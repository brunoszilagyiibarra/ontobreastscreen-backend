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
public class MSPUyGuideRiskCalculator extends RiskCalculator {

    public MSPUyGuideRiskCalculator(ResourceLoader resourceLoader) {
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

        if(UY_HEREDITARY_RISK_YES.getUri().equals(womanFormData.get(UY_HEREDITARY_RISK_QUESTION.getUri())) ||
                UY_CHEST_RADIOTERAPHY_YES.getUri().equals(womanFormData.get(UY_CHEST_RADIOTERAPHY_QUESTION.getUri())) ||
                UY_HIPERPLASIA_ATIPIA_YES.getUri().equals(womanFormData.get(UY_HIPERPLASIA_ATIPIA_QUESTION.getUri()))) {
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
        return RiskModelCalculatorType.MSP_UY_GUIDELINE;
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.MSP_UY;
    }

    @AllArgsConstructor
    @Getter
    private enum WomanExampleNames implements WomanExample {
        MSP_WOMAN_1_HIGH("/mocks/msp/example1.json", "mocks/msp/risk_high.json"),
        MSP_WOMAN_2_MID_40("/mocks/msp/example2.json", "mocks/msp/risk_mid.json"),
        MSP_WOMAN_3_MID_60("/mocks/msp/example3.json", "mocks/msp/risk_mid.json"),
        MSP_WOMAN_4_MID_70("/mocks/msp/example4.json", "mocks/msp/risk_mid.json")
        ;

        private final String path;
        private final String calcPath;
    }
}
