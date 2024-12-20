package uy.com.fing.ontologyformgeneratorapi.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uy.com.fing.ontologyformgeneratorapi.IndividualMappingUtils;
import uy.com.fing.ontologyformgeneratorapi.ontology.OntologyRepository;
import uy.com.fing.ontologyformgeneratorapi.persistence.FusekiTripleStoreClient;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskLevel;
import uy.com.fing.ontologyformgeneratorapi.risk.dtos.RiskModel;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRPropsEnum.AGE_DPROP;
import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRPropsEnum.HAS_RANGE_PROP;

@SpringBootTest
@Slf4j
public class IndividualMappingUtilsTest {

    private static final String BREAST_CANCER_RECOMMENDATION_OWL = "Breast_cancer_recommendation_19_8_24_fixes.owl";

    @Autowired
    private OntologyRepository ontologyRepository;

    @MockBean
    private FusekiTripleStoreClient tripleStoreClient;

    private OntModel model;

    @BeforeEach
    void setUp() {
        when(tripleStoreClient.getOntologyByName(BREAST_CANCER_RECOMMENDATION_OWL))
                .thenReturn(RDFDataMgr.loadModel(BREAST_CANCER_RECOMMENDATION_OWL, Lang.RDFXML));
        model = ontologyRepository.getOntologyModelABoxByIdFor(BREAST_CANCER_RECOMMENDATION_OWL);
    }

    /**
     * Recupera todas las datatype properties y se asegura que el sistema cargue las mismas.
     */
    @Test
    void whenGetRiskIndividualThenReturnExpectedInstance() {
        var riskIndividual = IndividualMappingUtils.getRiskIndividual(model, RiskModel.IBIS, RiskLevel.HIGH);

        assertEquals("http://purl.org/ontology/breast_cancer_recommendation#IBIS_model_high",
                riskIndividual.getURI());
    }


    @ParameterizedTest
    @MethodSource("provideAgeData")
    void whenGetAgeIndividualThenReturnExpectedInstance(Integer age, Set<String> expectedTags) {
        var ageIndividual = IndividualMappingUtils.getAgeIndividual(model, age);

        assertEquals(age, ageIndividual.getPropertyValue(model.getProperty(AGE_DPROP.getUri())).asLiteral().getInt());

        var tags = ageIndividual.listPropertyValues(model.getProperty(HAS_RANGE_PROP.getUri()))
                .mapWith(interval -> interval.as(Individual.class).getLabel(null))
                .toSet();
        assertEquals(expectedTags, tags);
    }

    static Stream<Arguments> provideAgeData() {
        return Stream.of(
                Arguments.of(29, Set.of()),
                Arguments.of(30, Set.of("Age_gt_or_eq_30")),
                Arguments.of(40, Set.of("Age_gt_or_eq_30", "Age_Range_40_to_44")),
                Arguments.of(44, Set.of("Age_gt_or_eq_30", "Age_Range_40_to_44")),
                Arguments.of(45, Set.of("Age_gt_or_eq_30", "Age_Range_45_to_54")),
                Arguments.of(54, Set.of("Age_gt_or_eq_30", "Age_Range_45_to_54")),
                Arguments.of(55, Set.of("Age_gt_or_eq_30", "Age_gt_or_eq_55")),
                Arguments.of(80, Set.of("Age_gt_or_eq_30", "Age_gt_or_eq_55"))
        );
    }

}
