package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo){
        return PhotoDto.builder()
                .photoId(photo.getPhotoId())
                .fileName(photo.getFileName())
                .fileSize(photo.getFileSize())
                .originalUrl(photo.getOriginalUrl())
                .thumbUrl(photo.getThumbUrl())
                .uploadedAt(photo.getUploadedAt())
                .build();

    }

    public static Photo convertToModel(PhotoDto photoDto){
        return Photo.dtoBuilder()
                .photoId(photoDto.getPhotoId())
                .fileName(photoDto.getFileName())
                .fileSize(photoDto.getFileSize())
                .originalUrl(photoDto.getOriginalUrl())
                .thumbUrl(photoDto.getThumbUrl())
                .uploadedAt(photoDto.getUploadedAt())
                .build();
    }
}
