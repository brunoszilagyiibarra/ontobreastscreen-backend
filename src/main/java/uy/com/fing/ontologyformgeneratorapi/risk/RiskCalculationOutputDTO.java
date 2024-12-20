package uy.com.fing.ontologyformgeneratorapi.risk;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RiskCalculationOutputDTO {

    private Double riskTenYears;
    private Double riskAllLife;
    private String riskModel;
    private String riskModelCalculator;
}
