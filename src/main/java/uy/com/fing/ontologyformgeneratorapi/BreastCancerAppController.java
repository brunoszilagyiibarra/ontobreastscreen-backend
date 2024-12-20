package uy.com.fing.ontologyformgeneratorapi;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.ontologyformgeneratorapi.ontoforms.Form;
import uy.com.fing.ontologyformgeneratorapi.ontoforms.FormAdapter;
import uy.com.fing.ontologyformgeneratorapi.recommend.WomanRecommendation;
import uy.com.fing.ontologyformgeneratorapi.risk.RiskCalculatorService;
import uy.com.fing.ontologyformgeneratorapi.risk.WomanRisk;
import uy.com.fing.ontologyformgeneratorapi.risk.wip.RiskCalculatorException;

import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping( "breast-cancer-recommendation-api/v1")
public class BreastCancerAppController {

    private FormAdapter formAdapter;
    private BreastCancerStudiesService breastCancerStudiesService;
    private RiskCalculatorService riskCalculatorService;

    public record CalculateRiskFormData(Map<String, String> womanHistory, String riskModelUri) {}

    @GetMapping("/models")
    public List<BreastCancerStudiesService.IndividualDescriptor> getModels() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Se comienza recuperar los modelos disponibles, lenguage {}", language);

        var individuals = breastCancerStudiesService.getModels(language);

        log.info("Se retornan {} individuos, lenguaje {}", individuals, language);
        return individuals;
    }

    @GetMapping("/form")
    public Form getForm(@RequestParam String riskModelUri, @RequestParam(required = false) String womanExampleName) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Se comienza recuperar el formulario de mujer para el riskModel {}, womanExampleName {}, language {}",
                riskModelUri, womanExampleName, language);

        Form womanForm = formAdapter.getWomanForm(riskModelUri, language);

        if(womanExampleName != null && !womanExampleName.isBlank()){
            final var womanData = riskCalculatorService.getWomanFormDataFromExample(riskModelUri, womanExampleName);
            formAdapter.fillWomanFormDataIntoForm(womanForm, womanData);
        }

        log.info("Se retorna el formulario de mujer para el risk model {}, womanExampleName {} y language {}, resultado: {}",
                riskModelUri, womanExampleName, language, womanForm);
        return womanForm;
    }

    @GetMapping("/woman_examples")
    public List<String> getWomanExamplesForMockedMode(@RequestParam String riskModel) {
        log.info("Comenzando a retornar la lista de mujeres de ejemplo para el mockedMode de ingreso mujeres para riskModel {}", riskModel);

        var womanExampleNames = riskCalculatorService.getWomanExampleNames(riskModel);

        log.info("Finalizando el computo de la lista de mujeres de ejemplo para el mockedMode de riskModel {} con resultado {}",
                riskModel, womanExampleNames);
        return womanExampleNames;

    }

    @PostMapping("/woman")
    public WomanRisk submitWomanFormAndCalculateRisk(@RequestBody CalculateRiskFormData data) throws RiskCalculatorException {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a calcular el riesgo de contraer cancer de mama con los siguientes parámetros {} y language {}", data, language);

        WomanRisk womanRisk = breastCancerStudiesService.calculateRiskAndCreateWoman(data.riskModelUri(), data.womanHistory(), language);

        log.info("Finalizando calculo del riesgo de contraer cancer de mama con los siguientes parámetros {}, language {} y resultado {}",
                data, language, womanRisk);
        return womanRisk;
    }

    @GetMapping("/recommendation_guides")
    public List<BreastCancerStudiesService.IndividualDescriptor> getRecommendationGuides(@RequestParam("risk") String riskLevelUri) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a obtener las guías de recomendación de estudios para el nivel {} y lenguage {}", riskLevelUri, language);

        List<BreastCancerStudiesService.IndividualDescriptor> guidelines = breastCancerStudiesService.getAllGuidelinesFor(riskLevelUri, language);

        log.info("Finalizando obtención de las guias de recommendación de estudios con nivel {} y lenguaje {} con resultado {}", riskLevelUri, language,
                guidelines);
        return guidelines;
    }

    @GetMapping("/recommendation")
    public WomanRecommendation getWomanRecommendation(@RequestParam("womanId") String womanId, @RequestParam("guidelineUri") String guidelineUri) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a obtener la recomendación de estudios para la mujer {} y lenguage {}", womanId, language);

        WomanRecommendation womanRecommendation = breastCancerStudiesService.getWomanAllRecommendations(womanId, guidelineUri, language);

        log.info("Finalizando obtención de recomendación de estudios para la mujer {} y lenguage {} con resultado {}", womanId, language,
                womanRecommendation);
        return womanRecommendation;
    }

    @GetMapping("/recommendation_explain")
    public List<String> getWomanRecommendationExplain(@RequestParam("womanId") String womanId, @RequestParam("guidelineUri") String guidelineUri) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        log.info("Comenzando a obtener la explicación recomendación de estudios para la mujer {} y lenguage {}", womanId, language);

        List<String> explainList = breastCancerStudiesService.getExplainFor(womanId, guidelineUri);

        log.info("Finalizando obtención de la explicación de recomendación de estudios para la mujer {} y lenguage {} con resultado {}", womanId, language,
                explainList);
        return explainList;
    }

}
