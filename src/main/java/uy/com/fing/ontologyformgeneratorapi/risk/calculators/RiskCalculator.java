package uy.com.fing.ontologyformgeneratorapi.risk.calculators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskCalculation;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModelCalculatorType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class RiskCalculator {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<>() {};
    private final Map<WomanExample, Map<String, String>> WOMAN_EXAMPLE_NAMES_MAP_MAP;
    private final Map<Map<String, String>, RiskCalculation> RISK_CALC_BY_WOMAN_DATA;

    protected RiskCalculator(ResourceLoader resourceLoader) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        WOMAN_EXAMPLE_NAMES_MAP_MAP = new HashMap<>();
        RISK_CALC_BY_WOMAN_DATA = new HashMap<>();

        getWomanExamples().forEach(
                woman -> {

                    Resource womanDataFile = resourceLoader.getResource("classpath:"+woman.getPath());
                    Resource womanRiskFile = resourceLoader.getResource("classpath:"+woman.getCalcPath());
                    try (InputStream inputStream = womanDataFile.getInputStream();
                         InputStream riskFileStream = womanRiskFile.getInputStream()){

                        var womanData =  objectMapper.readValue(inputStream, MAP_TYPE_REFERENCE);
                        WOMAN_EXAMPLE_NAMES_MAP_MAP.put(woman, womanData);
                        RiskCalculation value = objectMapper.readValue(riskFileStream, RiskCalculation.class);
                        RISK_CALC_BY_WOMAN_DATA.put(womanData, value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public final RiskCalculation calculateRisk(Map<String, String> womanFormData) {
        return RISK_CALC_BY_WOMAN_DATA.containsKey(womanFormData) ? RISK_CALC_BY_WOMAN_DATA.get(womanFormData) :
                doRiskCalculation(womanFormData);
    }

    public final Map<String, String> getWomanInfo(String womanExampleNames) {
        return WOMAN_EXAMPLE_NAMES_MAP_MAP.get(getWomanExampleFromString(womanExampleNames));
    }

    protected abstract WomanExample getWomanExampleFromString(String womanExampleString);
    public abstract Stream<WomanExample> getWomanExamples();
    public abstract RiskCalculation doRiskCalculation(Map<String, String> womanFormData);
    public abstract RiskModelCalculatorType getRiskModelCalculator();
    public abstract RiskModel getRiskModel();

    public interface WomanExample {
        String getPath();
        String getCalcPath();
        String name();
    }

}
