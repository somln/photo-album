package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.PhotoService;
import com.squarecross.photoalbum.dto.PhotoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoDto> getPhoto(@PathVariable("albumId") final long albumId,
                                             @PathVariable("photoId") final long photoId){
       return ResponseEntity.ok(photoService.getPhotoById(albumId, photoId));
    }

}
