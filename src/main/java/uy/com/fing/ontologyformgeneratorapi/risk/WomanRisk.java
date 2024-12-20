package uy.com.fing.ontologyformgeneratorapi.risk;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Getter
public class WomanRisk {

    private String womanUri;
    private RiskCalculationOutputDTO riskCalculation;
    private String riskLevel;
    private String riskLevelUri;
}
