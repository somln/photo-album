package com.squarecross.photoalbum.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {

    private Long albumId;
    private String albumName;
    private LocalDateTime createdAt;
    private int photoCount;
    private List<String> thumbUrls;

}