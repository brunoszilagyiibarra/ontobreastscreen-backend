package uy.com.fing.ontologyformgeneratorapi.integration;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uy.com.fing.ontologyformgeneratorapi.BreastCancerStudiesService;
import uy.com.fing.ontologyformgeneratorapi.persistence.FusekiTripleStoreClient;

import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class BreastCancerStudiesServiceIT {

    private static final String BREAST_CANCER_RECOMMENDATION_OWL = "Breast_cancer_recommendation_19_8_24_fixes.owl";

    @Autowired
    private BreastCancerStudiesService sut;

    @MockBean
    private FusekiTripleStoreClient tripleStoreClient;



//    @Test
//    void getCreateNewWomanReturnsExpectedRecommendationResult() {
//        mockTripleStoreWithLocalImplementation();
//
//        WomanHistoryData womanHistoryData = WomanHistoryData.builder()
//                .age(45)
//                .build();
//
//        String newWomanIndividual = sut.createNewWomanIndividual(RiskModel.IBIS, RiskLevel.HIGH, womanHistoryData);
//
//        var n = sut.getWomanAllRecommendations(newWomanIndividual);
//
//        WomanRecommendation womanRecommendation = new WomanRecommendation();
//        womanRecommendation.setHighRecommendation(
//                new WomanRecommendation.Recommendation("Magnetic resonance", "Strong Recommendation", "Annual", "Age_gt_or_eq_30"));
//        womanRecommendation.setMidRecommendation(
//                new WomanRecommendation.Recommendation("Screening Mammography", "Strong Recommendation", "Annual", "Age_Range_45_to_54"));
//        assertEquals(womanRecommendation, n);
//    }

    private void mockTripleStoreWithLocalImplementation() {
        Model model = RDFDataMgr.loadModel(BREAST_CANCER_RECOMMENDATION_OWL, Lang.RDFXML);
        when(tripleStoreClient.getOntologyByName(BREAST_CANCER_RECOMMENDATION_OWL)).thenReturn(model);
    }

}
