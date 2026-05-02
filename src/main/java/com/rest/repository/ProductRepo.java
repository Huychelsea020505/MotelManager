package com.rest.repository;

import com.rest.domain.Product;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {
}
