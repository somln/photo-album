package com.squarecross.photoalbum.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoMoveDto {
    Long fromAlbumId;
    Long toAlbumId;
    List<Long> photoIds;
}
