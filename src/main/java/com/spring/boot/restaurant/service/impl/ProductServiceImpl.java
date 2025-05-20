package com.spring.boot.restaurant.service.impl;

import com.spring.boot.restaurant.dto.ProductDto;
import com.spring.boot.restaurant.exception.BadRequestException;
import com.spring.boot.restaurant.exception.NotFoundException;
import com.spring.boot.restaurant.mapper.ProductMapper;
import com.spring.boot.restaurant.model.Product;
import com.spring.boot.restaurant.repository.ProductRepo;
import com.spring.boot.restaurant.service.ProductService;
import com.spring.boot.restaurant.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private MessageSource messageSource;

    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return productMapper.toDtoList(products);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toDto)
                .orElse(null);
    }

    @Override
    public ProductDto saveProduct(ProductDto productDto) {
        if (productDto.getName() == null || productDto.getName().isBlank()) {
            Map<String, String> message = MessageUtil.getBilingualMessageMap(messageSource, "category.name.empty");
            throw new BadRequestException(message.toString());
        }

        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepo.save(product));
    }

    @Override
    public List<ProductDto> saveProductList(List<ProductDto> productDtoList) {
        if (productDtoList == null || productDtoList.isEmpty()) {
            throw new BadRequestException("Product list must not be null or empty");
        }

        for (ProductDto dto : productDtoList) {
            if (dto.getName() == null || dto.getName().isBlank()) {
                throw new BadRequestException("Product name is required.");
            }
        }

        // Filter out duplicates based on name + category ID
        List<ProductDto> nonDuplicateDtos = productDtoList.stream()
                .filter(dto -> !productRepo.existsByNameAndCategoryId(dto.getName(), dto.getCategoryId()))
                .toList();

        if (nonDuplicateDtos.isEmpty()) {
            throw new BadRequestException("All provided products are duplicates.");
        }

        List<Product> products = productMapper.toEntityList(nonDuplicateDtos);
        List<Product> savedProducts = productRepo.saveAll(products);
        return productMapper.toDtoList(savedProducts);
    }

    @Override
    public boolean deleteProductById(Long id) {
        if (!productRepo.existsById(id)) {
            return false;
        }
        productRepo.deleteById(id);
        return true;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getId() == null || !productRepo.existsById(productDto.getId())) {
            throw new NotFoundException("Product with ID " + productDto.getId() + " not found");
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
        List<Product> products = productRepo.findAllById(ids);
        if (products.isEmpty()) {
            return false;
        }
        productRepo.deleteAll(products);
        return true;
    }
}