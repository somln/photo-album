package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.AlbumService;
import com.squarecross.photoalbum.dto.AlbumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping(value = "/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable("albumId") final Long id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }






}
