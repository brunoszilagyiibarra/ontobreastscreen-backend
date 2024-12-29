package uy.com.fing.ontologyformgeneratorapi.ontoforms;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.springframework.stereotype.Service;
import uy.com.fing.ontologyformgeneratorapi.LanguageLabelUtils;
import uy.com.fing.ontologyformgeneratorapi.ontology.BCRClassesEnum;
import uy.com.fing.ontologyformgeneratorapi.ontology.BCRPropsEnum;
import uy.com.fing.ontologyformgeneratorapi.ontology.OntologyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRClassesEnum.RISK_FACTOR_CLASS;
import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRPropsEnum.*;
import static uy.com.fing.ontologyformgeneratorapi.ontology.BCRQuestionInstancesEnum.*;

@Service
@Slf4j
@AllArgsConstructor
public class FormAdapter {

    private OntoFormsClient ontoFormsClient;
    private OntologyRepository ontologyRepository;

    private record Question(String uri, String text, String riskFactorUri, Integer order, List<Answer> answers) {}
    private record Answer(String uri, String text) {}

    public Form getWomanForm(String riskModelUri, String language) {
        final var ontoModel = ontologyRepository.getOntologyModelForTBoxById(ontoFormsClient.getOntologyFileName());

        final var questionsByClassification = getAllRiskModelQuestion(ontoModel, riskModelUri).stream()
                .map(q -> mapQuestionIndToQuestionRecord(q, language))
                .peek(q -> log.info("Se computa la pregunta {}", q))
                .collect(Collectors.groupingBy(Question::riskFactorUri));

        Form form = ontoFormsClient.getWomanForm();

        var riskFactorOrderByUris = getRiskFactorOrderValues(ontoModel);

        questionsByClassification.forEach((riskFactorUri, riskFactorQuestions) -> {

            String riskFactorLabel = LanguageLabelUtils.getLabelInLanguageOrDefault(ontoModel.getIndividual(riskFactorUri), language);

            Form formForRiskFactor = Form.builder()
                    .classUri(riskFactorUri)
                    .sectionName(riskFactorLabel)
                    .fields(new ArrayList<>())
                    .sectionOrder(riskFactorOrderByUris.get(riskFactorUri))
                    .subForms(new ArrayList<>())
                    .build();

            addFormFields(formForRiskFactor, riskFactorQuestions);

            form.addSubSection(formForRiskFactor);
        });

        return form;
    }

    private static Map<String, Integer> getRiskFactorOrderValues(OntModel ontoModel) {
        return ontoModel.listIndividuals(ResourceFactory.createResource(RISK_FACTOR_CLASS.getUri())).toList()
                .stream().collect(Collectors.toMap(Resource::getURI, r -> {
                    var orderProperty = r.getPropertyValue(ResourceFactory.createProperty(QUESTION_ORDER.getUri()));
                    return orderProperty == null ? 0 : orderProperty.asLiteral().getInt();
                }));
    }


    /**
     * Retorna todas las preguntas asociadas a un modelo de riesgo.
     * @param m
     * @param riskModelUri
     * @return
     */
    private List<Individual> getAllRiskModelQuestion(OntModel m, String riskModelUri) {
        return m.listIndividuals(ResourceFactory.createResource(BCRClassesEnum.MODEL_CLASS.getUri()))
                .filterKeep(rm -> riskModelUri.equals(rm.getURI())).next()
                .listPropertyValues(m.getProperty(BCRPropsEnum.HAS_MODEL_QUESTION.getUri()))
                .mapWith(i -> i.as(Individual.class))
                .toList();
    }

    private String getQuestionRiskFactorValue(Individual question) {

        Individual riskFactorInd = question.getPropertyValue(ResourceFactory.createProperty(ABOUT_RISK_FACTOR.getUri())).as(Individual.class);
        if(riskFactorInd == null) {
            String msj = String.format("Encontré una pregunta sin clasificación %s", question.getURI());
            log.error(msj);
            throw new RuntimeException(msj);
        }

        return riskFactorInd.getURI();
    }

    private List<Answer> getQuestionPossibleAnswers(Individual question, String language) {
        final var ansText = ResourceFactory.createProperty(ANSWER_HAS_TEXT.getUri());

        return question.listPropertyValues(ResourceFactory.createProperty(QUESTION_HAS_ANSWER.getUri()))
                .mapWith(r -> {
                    var respo = r.as(Individual.class);

                    var ansTextInternal = respo.listPropertyValues(ansText).filterKeep(i ->
                            language.equals(i.asLiteral().getLanguage())).next();

                    if(ansTextInternal == null) {
                        log.error("El texto de la respuesta {} es vacío y eso es un error", question.getURI());
                        throw new RuntimeException();
                    }

                    return new Answer(respo.getURI(), ansTextInternal.asLiteral().getString());
                }).toList();
    }


    private String getQuestionDescription(Individual question, String language) {
        RDFNode propertyValue = question.listPropertyValues(ResourceFactory.createProperty(QUESTION_HAS_TEXT.getUri()))
                .filterKeep(i -> language.equals(i.asLiteral().getLanguage())).next();
        return propertyValue.asLiteral().getString();
    }

    /**
     * Recupera el orden de las preguntas desde la annotation.
     * @param question
     * @return
     */
    private int getQuestionOrder(Individual question) {
        var orderVal = question.getPropertyValue(ResourceFactory.createProperty(QUESTION_ORDER.getUri()));
        return orderVal == null ? 0 : orderVal.asLiteral().getInt();
    }

    private Question mapQuestionIndToQuestionRecord(Individual q, String language) {
        return new Question(q.getURI(), getQuestionDescription(q, language), getQuestionRiskFactorValue(q),
                getQuestionOrder(q), getQuestionPossibleAnswers(q, language));
    }

    private void addFormFields(Form subForm, List<Question> questions) {
        for (Question question : questions) {

            Form.FormField field;

            if(question.answers().isEmpty()) {
                String typeFiled = "string";

                if(question.uri().contains("age") || question.uri().equals(IBIS_HORMONAL_HRT_HOW_LONG.getUri())
                        || question.uri().equals(IBIS_HORMONAL_HRT_YEARS_AGO.getUri())
                        || question.uri().equals(IBIS_HORMONAL_HRT_LENGHT.getUri())
                ) {
                    typeFiled = "integer";
                } else if (question.text().contains("Peso") || question.text().contains("Altura")) {
                    typeFiled = "double";
                }

                field = Form.DatatypeField.builder()
                        .label(question.text())
                        .uri(question.uri())
                        .datatype(typeFiled)
                        .order(question.order())
                        .build();

            } else {
                var options = question.answers().stream().map(i -> new Form.FieldOption(i.text, i.uri)).toList();

                field = Form.ObjectField.builder()
                        .label(question.text())
                        .uri(question.uri())
                        .singleOption(true)
                        .options(options)
                        .order(question.order())
                        .build();
            }

            subForm.addField(field);
        }
    }

    public void fillWomanFormDataIntoForm(Form form, Map<String, String> womanData) {
        womanData.forEach((key, value) -> findFieldAndModify(form, key, value));
    }

    private void findFieldAndModify(Form form, String fieldUri, String valueOrOption) {

        Optional<Form.FormField> field = form.getFields().stream()
                .map(f -> {
                    f.setEditable(false);
                    return f;
                })
                .filter(f -> f.getUri().equals(fieldUri)).findAny();

        if(field.isPresent()) {
            Form.FormField formField = field.get();
            if(formField instanceof Form.DatatypeField datatypeField) {
                datatypeField.setValue(valueOrOption);
            } else if(formField instanceof Form.ObjectField objectField) {
                Form.FieldOption fieldOption = objectField.getOptions().stream().filter(op -> op.uri().equals(valueOrOption)).findFirst().get();
                objectField.setSelectedOption(fieldOption);
            }
        } else {
            form.getSubForms().forEach(subForm -> findFieldAndModify(subForm, fieldUri, valueOrOption));
        }
    }

}
