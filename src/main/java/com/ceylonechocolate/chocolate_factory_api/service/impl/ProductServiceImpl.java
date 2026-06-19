package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ProductRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ProductResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Product;
import com.ceylonechocolate.chocolate_factory_api.entity.ProductCategory;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductCategoryRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.ProductRepository;
import com.ceylonechocolate.chocolate_factory_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrueAndIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository
                .findByCategoryIdAndIsDeletedFalse(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return mapToResponse(findProductById(id));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Product already exists: " + request.getName()
            );
        }

        ProductCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        String code = getNextProductCode(request.getCategoryId());

        Product product = Product.builder()
                .category(category)
                .code(code)
                .name(request.getName())
                .variant(request.getVariant())
                .packagingType(request.getPackagingType())
                .unit(Product.Unit.valueOf(request.getUnit()))
                .weightPerUnit(request.getWeightPerUnit())
                .sellingPrice(request.getSellingPrice())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null
                        ? request.getIsActive() : true)
                .isDeleted(false)
                .build();

        productRepository.save(product);
        log.info("Product created: {}", product.getCode());

        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = findProductById(id);

        if (!product.getName().equals(request.getName()) &&
                productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Product already exists: " + request.getName()
            );
        }

        ProductCategory category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        product.setCategory(category);
        product.setName(request.getName());
        product.setVariant(request.getVariant());
        product.setPackagingType(request.getPackagingType());
        product.setUnit(Product.Unit.valueOf(request.getUnit()));
        product.setWeightPerUnit(request.getWeightPerUnit());
        product.setSellingPrice(request.getSellingPrice());
        product.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        productRepository.save(product);
        log.info("Product updated: {}", product.getCode());

        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        product.setIsDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
        log.info("Product deleted: {}", product.getCode());
    }

    @Override
    public String getNextProductCode(Long categoryId) {
        ProductCategory category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        String prefix = category.getCodePrefix();

        return productRepository
                .findTopByCategoryIdOrderByIdDesc(categoryId)
                .map(product -> {

                    // Example: PRD-CHB-003
                    String code = product.getCode();

                    String[] parts = code.split("-");

                    int lastNumber =
                            Integer.parseInt(parts[2]);

                    return String.format(
                            "PRD-%s-%03d",
                            prefix,
                            lastNumber + 1
                    );

                })
                .orElse(
                        String.format("PRD-%s-001", prefix)
                );
    }

    private Product findProductById(Long id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found with id: " + id
                        )
                );
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .codePrefix(product.getCategory().getCodePrefix())
                .code(product.getCode())
                .name(product.getName())
                .variant(product.getVariant())
                .packagingType(product.getPackagingType())
                .unit(product.getUnit().name())
                .weightPerUnit(product.getWeightPerUnit())
                .sellingPrice(product.getSellingPrice())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}