package com.rest.repository;

import com.rest.domain.Quote;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface QuoteRepo extends CrudRepository<Quote, Long> {
}
