package com.if_true.company.presentation.dto;

import com.if_true.company.domain.CompanyType;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public record CompanyUpdateRequest(
	@NotBlank
	@Size(max = 100)
	String companyName,

	CompanyType companyType,

	UUID hubId,

	@NotBlank
	@Size(max = 255)
	String companyAddress
) {
}
