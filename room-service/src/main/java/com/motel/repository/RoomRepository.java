package com.motel.repository;

import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    Optional<Room> findByName(String name);

    boolean existsByName(String name);

    List<Room> findByStatus(RoomStatus status);
}
