package com.rest.repository;

import com.rest.domain.QuoteLine;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface QuoteLineRepo extends CrudRepository<QuoteLine, Long> {
}
