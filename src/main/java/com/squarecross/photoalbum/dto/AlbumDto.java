package com.squarecross.photoalbum.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class AlbumDto {

    Long albumId;
    String albumName;
    LocalDateTime createdAt;
    int count;
    private List<String> thumbUrls;


}
