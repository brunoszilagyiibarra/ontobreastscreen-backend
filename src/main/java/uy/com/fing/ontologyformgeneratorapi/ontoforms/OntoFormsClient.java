package uy.com.fing.ontologyformgeneratorapi.ontoforms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import uy.com.fing.ontologyformgeneratorapi.ontology.BCRClassesEnum;

import java.net.URI;
import java.util.List;

import static uy.com.fing.ontologyformgeneratorapi.persistence.FusekiTripleStoreClient.NAMED_GRAPH_PREFIX;

@Service
@Slf4j
public class OntoFormsClient {

    private static final String BASE_PATH = "/ontoforms-api/v1/";

    @Value("${ontoforms.client.url}")
    private String HOST;

    private final RestClient restClient;

    public OntoFormsClient() {
        restClient = RestClient.create();
    }


    public String getOntologyFileName() {
        URI uri = UriComponentsBuilder
                .fromUriString(HOST + BASE_PATH + "apps/{appName}/configurations/app-mapping")
                .build("OntoBreastScreen");

        List<String> ontoFullNames = restClient.get().uri(uri)
                .retrieve()
                .body(ParameterizedTypeReference.forType(ResolvableType.forClassWithGenerics(List.class,
                        String.class).getType()));

        return ontoFullNames.get(0).replace(NAMED_GRAPH_PREFIX, "");
    }

    public Form getWomanForm(){
        String ontoId = getOntologyFileName();

        URI uri = UriComponentsBuilder
                .fromUriString(HOST + BASE_PATH + "ontologies/{ontoId}/forms")
                .queryParam("classUri", BCRClassesEnum.WOMAN_CLASS.getUri())
                .build(ontoId);

        return restClient.get().uri(uri)
                .retrieve()
                .body(Form.class);
    }

}
