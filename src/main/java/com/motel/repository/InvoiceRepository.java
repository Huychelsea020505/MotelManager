package com.motel.repository;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByTenantIdAndStatus(Long tenantId, InvoiceStatus status);

    boolean existsByTenantIdAndStatus(Long tenantId, InvoiceStatus status);
}
