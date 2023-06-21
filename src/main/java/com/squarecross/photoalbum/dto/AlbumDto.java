package com.squarecross.photoalbum.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class AlbumDto {

    Long albumId;
    String albumName;
    Date createdAt;
    int photoCount;
}
