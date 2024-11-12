package com.ronnie.product.controller;

import com.ronnie.common.api.Result;
import com.ronnie.product.api.dto.CreateProductReq;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.api.dto.UpdateProductReq;
import com.ronnie.product.service.ProductService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    Result<Integer> createProduct(@RequestBody @Valid CreateProductReq createProductReq) {
        return Result.success(productService.createProduct(createProductReq));
    }

    @PutMapping
    Result<Void> updateProduct(UpdateProductReq updateProductReq) {
        productService.updateProduct(updateProductReq);
        return Result.success();
    }

    @GetMapping("/{productId}")
    Result<ProductDTO> getProduct(@PathVariable Integer productId) {
        return Result.success(productService.findById(productId));
    }

    @GetMapping
    Result<List<ProductDTO>> findProductsByIds(@RequestParam(required = false)  List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)){
            //fixme here returning all product is just for demonstrating, don't do this in production
            return Result.success(productService.listProducts());
        }
        return Result.success(productService.findProductsByIds(ids));
    }
}
