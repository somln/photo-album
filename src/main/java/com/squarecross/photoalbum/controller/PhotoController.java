package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.Service.PhotoService;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.dto.PhotoMoveDto;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {

    private final PhotoService photoService;

    //사진 정보 가져오기
    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoDto> getPhoto(@PathVariable("albumId") final long albumId,
                                             @PathVariable("photoId") final long photoId){
       return ResponseEntity.ok(photoService.getPhotoById(albumId, photoId));
    }

    //사진 업로드하기
    @PostMapping()
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final long albumId,
                                                       @RequestParam("photos") MultipartFile[] files) throws IOException {

        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            PhotoDto photoDto = photoService.uploadPhoto(albumId,file);
            photos.add(photoDto);
        }
        return ResponseEntity.ok(photos);
    }

    //사진 다운로드 하기
    @GetMapping("/download")
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response){
        try {
            if (photoIds.length == 1){
                File imageFile = photoService.getImageFile(photoIds[0]);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(imageFile), outputStream);
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //사진 목록 가져오기
    @GetMapping
    public ResponseEntity<List<PhotoDto>> getPhotoList(@PathVariable Long albumId,
                                                       @RequestParam(defaultValue = "byDate") String sort){
        return ResponseEntity.ok(photoService.getPhotoList(albumId, sort));
    }

    //사진 옮기기
    @PutMapping("/move")
    public ResponseEntity<List<PhotoDto>> movePhotos(@PathVariable String albumId,
                                                     @RequestBody PhotoMoveDto photoMoveDto) throws IOException {
        photoService.movePhotos(photoMoveDto);
        return ResponseEntity.ok(photoService.getPhotoList(photoMoveDto.getFromAlbumId(), "byDate"));
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleIOException(IOException e) {
        return ResponseEntity.badRequest().build();
    }

}
