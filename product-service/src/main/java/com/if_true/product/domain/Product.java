package com.if_true.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "p_product")
public class Product extends BaseEntity {

	@Column(name = "company_id", nullable = false)
	private UUID companyId;

	@Column(name = "hub_id", nullable = false)
	private UUID hubId;

	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Column(name = "product_description", columnDefinition = "text")
	private String productDescription;

	@Column(name = "product_quantity", nullable = false)
	private Long productQuantity;

	protected Product() {
	}

	private Product(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity, UUID actorId) {
		validateQuantity(productQuantity);
		this.companyId = companyId;
		this.hubId = hubId;
		this.productName = productName;
		this.productDescription = productDescription;
		this.productQuantity = productQuantity;
		initializeAudit(actorId);
	}

	public static Product create(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity, UUID actorId) {
		return new Product(companyId, hubId, productName, productDescription, productQuantity, actorId);
	}

	public void update(UUID companyId, UUID hubId, String productName, String productDescription, Long productQuantity, UUID actorId) {
		if (companyId != null) {
			this.companyId = companyId;
		}
		if (hubId != null) {
			this.hubId = hubId;
		}
		if (productName != null) {
			this.productName = productName;
		}
		if (productDescription != null) {
			this.productDescription = productDescription;
		}
		if (productQuantity != null) {
			validateQuantity(productQuantity);
			this.productQuantity = productQuantity;
		}
		markUpdated(actorId);
	}

	public void delete(UUID actorId) {
		markDeleted(actorId);
	}

	public void adjustQuantity(Long productQuantity, UUID actorId) {
		validateQuantity(productQuantity);
		this.productQuantity = productQuantity;
		markUpdated(actorId);
	}

	private static void validateQuantity(Long quantity) {
		if (quantity == null || quantity < 0) {
			throw new IllegalArgumentException("Product quantity must be greater than or equal to 0.");
		}
	}

	public UUID getCompanyId() {
		return companyId;
	}

	public UUID getHubId() {
		return hubId;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public Long getProductQuantity() {
		return productQuantity;
	}
}
