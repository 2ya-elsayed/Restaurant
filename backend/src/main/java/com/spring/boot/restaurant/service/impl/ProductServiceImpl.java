package com.spring.boot.restaurant.service.impl;

import com.spring.boot.restaurant.dto.BundleMessage;
import com.spring.boot.restaurant.dto.ProductDto;
import com.spring.boot.restaurant.exception.BadRequestException;
import com.spring.boot.restaurant.exception.NotFoundException;
import com.spring.boot.restaurant.mapper.ProductMapper;
import com.spring.boot.restaurant.model.Product;
import com.spring.boot.restaurant.repository.ProductRepo;
import com.spring.boot.restaurant.service.ProductService;
import com.spring.boot.restaurant.service.bundleService.BundleTranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private BundleTranslatorService bundleTranslatorService;

    private final ProductMapper productMapper;

    @Override
    public List<ProductDto>     getAllProducts() {
        List<Product> products = productRepo.findAll();
        return productMapper.toDtoList(products);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> {
                    BundleMessage message = bundleTranslatorService.getBundleMessage("product.not.found");
                    return new NotFoundException(message);
                });
    }

    @Override
    public ProductDto saveProduct(ProductDto productDto) {
        if (productDto == null || productDto.getName() == null || productDto.getName().isBlank()) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.name.invalid");
            throw new BadRequestException(message);
        }

        System.out.println("---> product name not null");

        // Check for duplicate product name under same category
        if (productRepo.existsByNameAndCategoryId(productDto.getName(), productDto.getCategoryId())) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.already.exists");
            System.out.println("---> product name duplicate");
            throw new BadRequestException(message);
        }

        System.out.println("---> product not duplicated ");

        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepo.save(product));
    }

    @Override
    public List<ProductDto> saveProductList(List<ProductDto> productDtoList) {
        if (productDtoList == null || productDtoList.isEmpty()) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.list.empty");
            throw new BadRequestException(message);
        }

        for (ProductDto dto : productDtoList) {
            if (dto.getName() == null || dto.getName().isBlank()) {
                BundleMessage message = bundleTranslatorService.getBundleMessage("product.name.invalid");
                throw new BadRequestException(message);
            }
        }

        List<ProductDto> nonDuplicateDtos = productDtoList.stream()
                .filter(dto -> !productRepo.existsByNameAndCategoryId(dto.getName(), dto.getCategoryId()))
                .toList();

        if (nonDuplicateDtos.isEmpty()) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.all.duplicates");
            throw new BadRequestException(message);
        }

        List<Product> products = productMapper.toEntityList(nonDuplicateDtos);
        List<Product> savedProducts = productRepo.saveAll(products);
        return productMapper.toDtoList(savedProducts);
    }

    @Override
    public boolean deleteProductById(Long id) {
        if (!productRepo.existsById(id)) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.not.found");
            throw new NotFoundException(message);
        }
        productRepo.deleteById(id);
        return true;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto == null || productDto.getId() == null || !productRepo.existsById(productDto.getId())) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.not.found");
            throw new NotFoundException(message);
        }

        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepo.save(product));
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDto> getProductsByCategoryName(String categoryName) {
        List<Product> products = productRepo.findByCategoryName(categoryName);
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        List<Product> products = productRepo
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        return productMapper.toDtoList(products);
    }

    @Override
    public boolean deleteProductsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.id.list.empty");
            throw new BadRequestException(message);
        }

        List<Product> products = productRepo.findAllById(ids);
        if (products.isEmpty()) {
            BundleMessage message = bundleTranslatorService.getBundleMessage("product.ids.not.found");
            throw new NotFoundException(message);
        }

        productRepo.deleteAll(products);
        return true;
    }
}