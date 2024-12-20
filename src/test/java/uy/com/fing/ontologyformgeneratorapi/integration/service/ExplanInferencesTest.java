package uy.com.fing.ontologyformgeneratorapi.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uy.com.fing.ontologyformgeneratorapi.ontology.OntologyRepository;
import uy.com.fing.ontologyformgeneratorapi.persistence.FusekiTripleStoreClient;

import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class ExplanInferencesTest {

    private static final String BREAST_CANCER_RECOMMENDATION_OWL = "Breast_cancer_recommendation_bruno_24_11_24.rdf";

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

    @Test
    void explainInferenceForWoman4() {

//        Resource woman4 = model.getResource("http://purl.org/ontology/breast_cancer_recommendation#Woman4");
//        Resource object = model.getResource("http://purl.org/ontology/breast_cancer_recommendation#high_40_to_50_age_recommendation");

        Model model = RDFDataMgr.loadModel(BREAST_CANCER_RECOMMENDATION_OWL, Lang.RDFXML);
        OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF, model);
        ontologyModel.setStrictMode(false); //TODO: Algunas definiciones de ontologías hacen que falle apache Jena.
        ontologyModel.setDerivationLogging(true);

        ontologyModel.prepare();

        // Primero, imprimimos todas las tripletas inferidas
        System.out.println("Imprimiendo tripletas inferidas:");
        StmtIterator stmtIterator = ontologyModel.listStatements();
        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.nextStatement();
            System.out.println("Inferido: " + stmt.getSubject() + " " + stmt.getPredicate() + " " + stmt.getObject());
        }

        // Ahora intentamos obtener derivaciones para una tripleta inferida
        System.out.println("\nIntentando obtener derivaciones para las tripletas inferidas:");
        stmtIterator = ontologyModel.listStatements();  // Volvemos a iterar sobre las inferencias

        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.nextStatement();
//            Triple triple = new Triple(stmt.getSubject().asNode(), stmt.getPredicate().asNode(), stmt.getObject().asNode());

            // Verificar si estamos obteniendo el grafo de inferencias adecuado
//            if (ontologyModel.getGraph() instanceof PelletInfGraph) {
//                PelletInfGraph pelletInfGraph = (PelletInfGraph) ontologyModel.getGraph();

                // Obtener las derivaciones (explicaciones) asociadas a esta tripleta
                var derivations = ontologyModel.getDerivation(stmt);
                if (derivations != null) {
                    while (derivations.hasNext()) {
                        var derivationTriple = derivations.next();
                        System.out.println("Derivación: " + derivationTriple);
                    }
                } else {
                    System.out.println("No se encontraron derivaciones para la tripleta: " + stmt);
                }
            }
        }


}
