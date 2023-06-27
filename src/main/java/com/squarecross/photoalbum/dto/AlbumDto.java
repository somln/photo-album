package com.squarecross.photoalbum.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AlbumDto {

    Long albumId;
    String albumName;
    LocalDateTime createdAt;
    int count;

}
