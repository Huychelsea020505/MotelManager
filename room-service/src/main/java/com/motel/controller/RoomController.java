package com.motel.controller;

import com.motel.domain.Room;
import com.motel.dto.CreateRoomRequest;
import com.motel.service.RoomService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;
import java.util.List;

@Controller("/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Get
    public List<Room> findAll() {
        return roomService.findAll();
    }

    @Post
    public Room create(@Body @Valid CreateRoomRequest request) {
        return roomService.create(request);
    }

    @Get("/{id}")
    public Room findById(Long id) {
        return roomService.findById(id);
    }

    @Put("/{id}")
    public Room update(Long id, @Body @Valid CreateRoomRequest request) {
        return roomService.update(id, request);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(Long id) {
        roomService.delete(id);
    }

    @Get("/occupied")
    public List<Room> findOccupiedRooms() {
        return roomService.findOccupiedRooms();
    }
}
