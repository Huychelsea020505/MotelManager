package com.rest.controller;

import com.rest.domain.Quote;
import com.rest.repository.QuoteRepo;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.exceptions.HttpStatusException;
import java.util.ArrayList;
import java.util.List;

@Controller("/quote")
public class QuoteController {
    private final QuoteRepo quoteRepo;

    public QuoteController(QuoteRepo quoteRepo) {
        this.quoteRepo = quoteRepo;
    }

    @Post
    public Quote createQuote(@Body Quote quote) {
        return quoteRepo.save(quote);
    }

    @Get("/{id}")
    public Quote getQuote(Long id) {
        return quoteRepo.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Quote not found"));
    }

    @Get
    public List<Quote> getQuotes() {
        Iterable<Quote> quotes = quoteRepo.findAll();
        List<Quote> result = new ArrayList<>();
        quotes.forEach(result::add);
        return result;
    }

    @Put("/{id}")
    public Quote updateQuote(Long id, @Body Quote update) {
        if (!quoteRepo.existsById(id)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Quote not found");
        }
        update.setId(id);
        return quoteRepo.update(update);
    }

    @Delete("/{id}")
    public void deleteQuote(Long id) {
        Quote quote = quoteRepo.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Quote not found"));
        quoteRepo.delete(quote);
    }
}
