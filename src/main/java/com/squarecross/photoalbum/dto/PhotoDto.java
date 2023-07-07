package com.squarecross.photoalbum.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDto {

    private Long photoId;
    private String fileName;
    private int fileSize;
    private String originalUrl;
    private String thumbUrl;
    private LocalDateTime uploadedAt;
    private Long albumId;
}
