package org.clickandcollect.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.clickandcollect.business.contract.RestaurantService;
import org.clickandcollect.business.exception.ResourceDuplicationException;
import org.clickandcollect.business.exception.UnknownResourceException;
import org.clickandcollect.consumer.repository.CategoryRepository;
import org.clickandcollect.consumer.repository.ProductRepository;
import org.clickandcollect.consumer.repository.RestaurantRepository;
import org.clickandcollect.model.entitie.Category;
import org.clickandcollect.model.entitie.Product;
import org.clickandcollect.model.entitie.Restaurant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Product> findProductsByRestaurantId(Long restaurantId) {
        log.info("Retrieving products for restaurant id '{}'", restaurantId);
        return this.productRepository.findAllByRestaurantId(restaurantId);
    }

    @Override
    public Product findProductByIds(Long restaurantId, Long productId) {
        log.info("Retrieving product id '{}' for restaurant id '{}'", productId, restaurantId);
        return this.productRepository.findProductByIdAndRestaurantId(productId, restaurantId)
                .orElseThrow(() -> new UnknownResourceException("Unknown product '" + productId + "'for restaurant '" + restaurantId + "'"));
    }

    @Override
    public Product saveProduct(Long restaurantId, Product product) {
        log.info("Retrieving restaurant id '{}'", restaurantId);
        if(this.restaurantRepository.findById(restaurantId).isPresent()){
            log.info("Restaurant id '{}' found", restaurantId);
            product.setRestaurant(Restaurant.builder().id(restaurantId).build());
            log.info("Retrieving category '{}'", product.getCategory().getName());
            Optional<Category> category = this.categoryRepository.findCategoryByName(product.getCategory().getName());
            if (category.isPresent()){
                log.info("Category found with id '{}'", category.get().getId());
                product.setCategory(category.get());
                try {
                    product = this.productRepository.save(product);
                    log.info("Product id '{}' saved to database", product.getId());
                } catch (DataIntegrityViolationException e) {
                    throw new ResourceDuplicationException("Product name '" + product.getName() + "' already exists");
                }
            } else {
                log.warn("Category '{}' does not exists", product.getCategory().getName());
                throw new UnknownResourceException("Unknown category " + product.getCategory().getName());
            }
        } else {
            log.warn("Restaurant id '{}' does not exists", restaurantId);
            throw new UnknownResourceException("Unknown restaurant " + restaurantId);
        }
        return product;
    }

    @Override
    public Product updateProduct(Long restaurantId, Long productId, Product product) {
        log.info("Retrieving product id '{}' for restaurant '{}'", productId, restaurantId);
        if (this.productRepository.findProductByIdAndRestaurantId(productId, restaurantId).isPresent()) {
            log.info("Product found");
            product.setId(productId);
            return this.saveProduct(restaurantId, product);
        } else {
            log.info("Product not found");
            throw new UnknownResourceException("Product '" + productId + "' doesn't exists for restaurant '" + restaurantId + "'");
        }
    }

    @Override
    public void deleteProduct(Long restaurantId, Long productId) {
        log.info("Retrieving product id '{}' for restaurant '{}'", productId, restaurantId);
        Product product = this.productRepository
                .findProductByIdAndRestaurantId(productId, restaurantId)
                .orElseThrow(() -> new UnknownResourceException("Product '" + productId + "' doesn't exists for restaurant '" + restaurantId + "'"));
        log.info("Deleting product");
        this.productRepository.delete(product);
    }

}
