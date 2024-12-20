package uy.com.fing.ontologyformgeneratorapi.risk.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RiskModelCalculatorType {
    IBIS_MOCKED("IBIS - mocked calculator"),
    IBIS_IKONOPEDIOA_WEB_SCRAPPER("IBIS - ibis.ikonopedia.com web scrapper"),
    MSP_UY_GUIDELINE("MSP - UY Guideline"),
    MSP_MOCKED("MSP - Mocked Guideline"),
    ACS_GUIDELINE("ASC - Guideline"),
    ACS_MOCKED("ACS - Mocked Guideline");

    private final String description;
}
