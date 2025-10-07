package com.example.patients.util;

import com.example.patients.dto.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting pagination requests to Pageable objects
 */
@Component
public class PaginationUtil {
    
    /**
     * Convert PaginationRequest to Spring Data Pageable
     */
    public static Pageable toPageable(PaginationRequest request) {
        if (request == null) {
            return PageRequest.of(0, 20);
        }
        
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        
        // Convert sort strings to Sort object
        Sort sort = createSort(request.getSort());
        
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * Create Sort object from list of sort strings
     * Expected format: "field,direction" (e.g., "id,asc" or "email,desc")
     */
    private static Sort createSort(List<String> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return Sort.unsorted();
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        
        for (String sortStr : sortList) {
            if (sortStr == null || sortStr.trim().isEmpty()) {
                continue;
            }
            
            String[] parts = sortStr.split(",");
            String field = parts[0].trim();
            
            if (field.isEmpty()) {
                continue;
            }
            
            // Default to ASC if direction not specified or invalid
            Sort.Direction direction = Sort.Direction.ASC;
            if (parts.length > 1) {
                String directionStr = parts[1].trim().toLowerCase();
                if ("desc".equals(directionStr)) {
                    direction = Sort.Direction.DESC;
                }
            }
            
            orders.add(new Sort.Order(direction, field));
        }
        
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
}