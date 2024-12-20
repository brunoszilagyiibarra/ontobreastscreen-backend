package uy.com.fing.ontologyformgeneratorapi.risk.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RiskLevel {
    MEDIUM("http://purl.org/ontology/breast_cancer_recommendation#Medium"),
    HIGH("http://purl.org/ontology/breast_cancer_recommendation#High");

    private String uri;
}
