package com.if_true.company.infrastructure;

import com.if_true.company.domain.Company;
import com.if_true.company.domain.CompanyType;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpecification {

    public static Specification<Company> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Company> companyNameContains(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("companyName")),
                        "%" + companyName.toLowerCase() + "%"
                );
    }

    public static Specification<Company> companyTypeEquals(CompanyType companyType) {
        if (companyType == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("companyType"), companyType);
    }

    public static Specification<Company> hubIdEquals(UUID hubId) {
        if (hubId == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hubId"), hubId);
    }
}