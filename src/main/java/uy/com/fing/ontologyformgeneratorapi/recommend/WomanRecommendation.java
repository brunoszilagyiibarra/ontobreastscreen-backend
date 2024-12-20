package uy.com.fing.ontologyformgeneratorapi.recommend;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class WomanRecommendation {

    private Recommendation midRecommendation;
    private Recommendation highRecommendation;

    public record Recommendation(String imaging, String strength, String periodicity, String forInterval) {}
}
