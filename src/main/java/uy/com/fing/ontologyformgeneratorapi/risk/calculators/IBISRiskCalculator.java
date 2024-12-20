package uy.com.fing.ontologyformgeneratorapi.risk.calculators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskCalculation;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskLevel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModelCalculatorType;
import uy.com.fing.ontologyformgeneratorapi.risk.wip.IBISIkonopediaWebScrapper;
import uy.com.fing.ontologyformgeneratorapi.risk.wip.RiskCalculatorException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Calculadora de MSP UY siguiendo reglas de la guìa.
 */
@Component
public class IBISRiskCalculator extends RiskCalculator {

    private final IBISIkonopediaWebScrapper ibisIkonopediaWebScrapper;

    public IBISRiskCalculator(ResourceLoader resourceLoader, IBISIkonopediaWebScrapper ibisIkonopediaWebScrapper) {
        super(resourceLoader);
        this.ibisIkonopediaWebScrapper = ibisIkonopediaWebScrapper;
    }

    @Override
    public RiskCalculation doRiskCalculation(Map<String, String> womanFormData) {
        try{
            return ibisIkonopediaWebScrapper.calculateRisk(womanFormData);
        } catch (RiskCalculatorException e) {
            //TODO: throw 429 en caso de detectar el máximo cantidad de usos.
            //TODO: debemos elevar la excepción y hacer algo a nivel de front??.
            return RiskCalculation.builder()
                    .riskLevel(RiskLevel.MEDIUM)
                    .riskModelCalculatorType(getRiskModelCalculator())
                    .riskModel(getRiskModel())
                    .build();
        }
    }

    @Override
    public Stream<WomanExample> getWomanExamples() {
        return Arrays.stream(WomanExampleNames.values());
    }

    @Override
    public RiskModelCalculatorType getRiskModelCalculator() {
        return RiskModelCalculatorType.IBIS_IKONOPEDIOA_WEB_SCRAPPER;
    }

    @Override
    public WomanExample getWomanExampleFromString(String womanExampleString) {
        return WomanExampleNames.valueOf(womanExampleString);
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.IBIS;
    }

    @AllArgsConstructor
    @Getter
    private enum WomanExampleNames implements WomanExample {
        IBIS_WOMAN_1_RISK_HIGH_36_8("mocks/ibis/example1.json", "mocks/ibis/riskCalc1.json"),
        IBIS_WOMAN_2_RISK_HIGH_31_7("mocks/ibis/example2.json", "mocks/ibis/riskCalc2.json"),
        IBIS_WOMAN_3_RISK_MEDIUM_18_3("mocks/ibis/example3.json", "mocks/ibis/riskCalc3.json"),
        IBIS_WOMAN_4_RISK_MEDIUM_8_5("mocks/ibis/example4.json", "mocks/ibis/riskCalc4.json"),
        IBIS_WOMAN_5_RISK_HIGH_25_7_SISTER("mocks/ibis/example5.json", "mocks/ibis/riskCalc5.json"),
        IBIS_WOMAN_6_RISK_HIGH_41_9_SISTER_MOTHER("mocks/ibis/example6.json", "mocks/ibis/riskCalc6.json"),
        IBIS_WOMAN_7_RISK_MEDIUM_4_4("mocks/ibis/example7.json", "mocks/ibis/riskCalc7.json"),
        IBIS_WOMAN_8_RISK_MEDIUM_16_9("mocks/ibis/example8.json", "mocks/ibis/riskCalc8.json"),
        IBIS_WOMAN_9_RISK_HIGH_41_SISTER_MOTHER("mocks/ibis/example9.json", "mocks/ibis/riskCalc9.json"),
        IBIS_WOMAN_10_RISK_MEDIUM_7_6("mocks/ibis/example10.json", "mocks/ibis/riskCalc10.json");

        private final String path;
        private final String calcPath;
    }
}
