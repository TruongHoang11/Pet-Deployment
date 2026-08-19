package N18.haui.Pet_18.service;

import java.util.List;

public interface RecommendationService {
    List<Long> recommendProducts(List<Long> productIds);
    List<Long> recommendServices(List<Long> serviceIds);
}
