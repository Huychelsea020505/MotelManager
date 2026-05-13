package com.motel.repository;

import com.motel.domain.Tenant;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {
    Optional<Tenant> findByCitizenId(String citizenId);

    boolean existsByCitizenId(String citizenId);

    List<Tenant> findByRoomId(Long roomId);
}
