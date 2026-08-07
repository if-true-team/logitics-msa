package com.if_true.product.presentation;

import com.if_true.product.application.ProductService;
import com.if_true.product.presentation.dto.InventoryResponse;
import com.if_true.product.presentation.dto.InventoryUpdateRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

	private final ProductService productService;

	public InventoryController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public Page<InventoryResponse> search(
		@RequestParam(required = false) String productName,
		@RequestParam(required = false) UUID companyId,
		@RequestParam(required = false) UUID hubId,
		Pageable pageable
	) {
		return productService.searchInventories(productName, companyId, hubId, pageable);
	}

	@GetMapping("/{productId}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public InventoryResponse get(@PathVariable UUID productId) {
		return productService.getInventory(productId);
	}

	@PatchMapping("/{productId}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public InventoryResponse adjust(
		@PathVariable UUID productId,
		@RequestHeader("X-User-Id") UUID actorId,
		@Valid @RequestBody InventoryUpdateRequest request
	) {
		return productService.adjustInventory(productId, request.productQuantity(), actorId);
	}
}
