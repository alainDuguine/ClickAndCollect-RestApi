package org.clickandcollect.webservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.clickandcollect.business.contract.ProductService;
import org.clickandcollect.model.entity.Product;
import org.clickandcollect.webservice.dto.ProductDto;
import org.clickandcollect.webservice.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/products")
@Slf4j
public class ProductApiController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductApiController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam(value = "category", required = false) String category,
                                                        @PathVariable Long restaurantId) {
        log.info("Retrieving list of products for restaurant id '{}'", restaurantId);
        List<Product> products = this.productService.findProductsByRestaurantId(restaurantId, category);
        log.info("{} products found", products.size());
        return new ResponseEntity<>(this.productMapper.listProductToListProductDto(products), HttpStatus.OK);
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long restaurantId,
                                                  @PathVariable Long productId) {
        log.info("Retrieving the product '{}' for restaurant id '{}'", productId, restaurantId);
        Product product = this.productService.findProductByIds(restaurantId, productId);
        log.info("Product '{}' found", product.getId());
        return new ResponseEntity<>(this.productMapper.productToProductDto(product), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ProductDto> addProduct(@PathVariable Long restaurantId,
                                                 @Valid @RequestBody ProductDto productDto) {
        log.info("Adding a new product to restaurant id '{}'", restaurantId);
        Product product = this.productService.saveProduct(restaurantId, this.productMapper.productDtoToProduct(productDto));
        log.info("Product '{}' created", product.getId());
        return new ResponseEntity<>(this.productMapper.productToProductDto(product), HttpStatus.CREATED);
    }

    @PutMapping("{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long restaurantId,
                                                    @PathVariable Long productId,
                                                    @Valid @RequestBody ProductDto productDto) {
        log.info("Updating product id '{}' for restaurant id '{}'", productId, restaurantId);
        Product product = this.productService.updateProduct(
                restaurantId,
                productId,
                this.productMapper.productDtoToProduct(productDto)
        );
        log.info("Product '{}' updated", productId);
        return new ResponseEntity<>(this.productMapper.productToProductDto(product), HttpStatus.OK);
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long restaurantId,
                                              @PathVariable Long productId) {
        log.info("Delete product id '{}' for restaurant id '{}'", productId, restaurantId);
        this.productService.deleteProduct(restaurantId, productId);
        log.info("Product '{}' deleted", productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
