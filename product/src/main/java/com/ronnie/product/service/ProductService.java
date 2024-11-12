package com.ronnie.product.service;

import com.ronnie.product.api.dto.CreateProductReq;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.api.dto.UpdateProductReq;
import com.ronnie.product.exception.ProductNotExistException;
import com.ronnie.product.model.Product;
import com.ronnie.product.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Integer createProduct(CreateProductReq req) {
        return productRepository.save(new Product(null, req.getName(), req.getPrice())).getId();
    }

    public void updateProduct(UpdateProductReq req) {
        productRepository
                .findById(req.getProductId())
                .map(p -> productRepository.save(new Product(p.getId(), req.getName(), req.getPrice())))
                .orElseThrow(ProductNotExistException::new);
    }

    public List<ProductDTO> findProductsByIds(List<Integer> ids) {
        return stream(productRepository.findAllById(ids).spliterator(), false).map(DO ->
                new ProductDTO(DO.getId(), DO.getName(), DO.getPrice())
        ).collect(Collectors.toList());
    }

    public List<ProductDTO> listProducts() {
        return stream(productRepository.findAll().spliterator(), false)
                .map(p -> new ProductDTO(p.getId(), p.getName(), p.getPrice()))
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Integer productId) {
        return productRepository.findById(productId).map(DO -> new ProductDTO(DO.getId(), DO.getName(), DO.getPrice())).orElseThrow(ProductNotExistException::new);
    }
}
