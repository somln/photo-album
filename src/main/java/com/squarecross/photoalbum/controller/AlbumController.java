package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.AlbumService;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;


    //앨범 정보 가져오기
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("albumId") final long albumId) {
        return ResponseEntity.ok(albumService.getAlbum(albumId));
    }

    //앨범 생성하기
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody final AlbumDto albumDto) throws IOException {
        return ResponseEntity.ok(albumService.createAlbum(albumDto));
    }

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAlbumList(@RequestParam(value="keyword", required=false, defaultValue="") final String keyword,
                                                       @RequestParam(value="sort", required=false, defaultValue = "byDate") final String sort,
                                                       @RequestParam(value="orderBy", required=false, defaultValue = "desc") final String orderBy){
        return ResponseEntity.ok(albumService.getAlbumList(keyword, sort, orderBy));
    }


 }
