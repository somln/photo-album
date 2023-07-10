package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.PhotoService;
import com.squarecross.photoalbum.dto.PhotoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping()
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final long albumId,
                                                       @RequestParam("photos") MultipartFile[] files) throws IOException {

        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.savePhoto(albumId,file);
            photos.add(photoDto);
        }
        return ResponseEntity.ok(photos);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleIOException(IOException e) {
        return ResponseEntity.badRequest().build();
    }

}
