package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.service.RecommendationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private static final String PRODUCT_RULES_PATH = "recommendation/product/association_rules.json";
    private static final String SERVICE_RULES_PATH = "recommendation/service/association_rules.json";

    private final ObjectMapper objectMapper;
    private final List<Rule> productRules;
    private final List<Rule> serviceRules;

    public RecommendationServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.productRules = loadRules(PRODUCT_RULES_PATH);
        this.serviceRules = loadRules(SERVICE_RULES_PATH);
    }

    @Override
    public List<Long> recommendProducts(List<Long> productIds) {
        return recommend(productIds, productRules);
    }

    @Override
    public List<Long> recommendServices(List<Long> serviceIds) {
        return recommend(serviceIds, serviceRules);
    }

    private List<Long> recommend(List<Long> itemIds, List<Rule> rules) {
        if (itemIds == null || itemIds.isEmpty() || rules == null || rules.isEmpty()) {
            return List.of();
        }

        Set<Long> requested = new HashSet<>(itemIds);

        return rules.stream()
                .filter(rule -> requested.containsAll(rule.getAntecedent()))
                .flatMap(rule -> rule.getConsequent().stream())
                .filter(recommendedId -> !requested.contains(recommendedId))
                .distinct()
                .toList();
    }

    private List<Rule> loadRules(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(inputStream, new TypeReference<List<Rule>>() {});
            }
        } catch (IOException ex) {
            log.error("Failed to load recommendation rules from {}", classpathLocation, ex);
            return List.of();
        }
    }

    private static class Rule {
        private List<Long> antecedent;
        private List<Long> consequent;
        private Double confidence;
        private Double support;
        private Double lift;

        public List<Long> getAntecedent() {
            return antecedent;
        }

        public void setAntecedent(List<Long> antecedent) {
            this.antecedent = antecedent;
        }

        public List<Long> getConsequent() {
            return consequent;
        }

        public void setConsequent(List<Long> consequent) {
            this.consequent = consequent;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }

        public Double getSupport() {
            return support;
        }

        public void setSupport(Double support) {
            this.support = support;
        }

        public Double getLift() {
            return lift;
        }

        public void setLift(Double lift) {
            this.lift = lift;
        }
    }
}
