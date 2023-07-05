package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.AlbumService;
import com.squarecross.photoalbum.dto.AlbumDto;
import lombok.RequiredArgsConstructor;
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

    //앨범 목록 가져오기
    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAlbumList(@RequestParam(value="keyword", required=false, defaultValue="") final String keyword,
                                                       @RequestParam(value="sort", required=false, defaultValue = "byDate") final String sort,
                                                       @RequestParam(value="orderBy", required=false, defaultValue = "desc") final String orderBy){
        return ResponseEntity.ok(albumService.getAlbumList(keyword, sort, orderBy));
    }

    //앨범명 변경하기
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbumName(@PathVariable("albumId") final long albumId,
                                                    @RequestBody final AlbumDto albumDto){
        return ResponseEntity.ok(albumService.updateAlbum(albumId, albumDto));
    }

    //앨범 삭제하기
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("albumId") final long albumId) throws IOException {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.ok().build();
    }




 }
