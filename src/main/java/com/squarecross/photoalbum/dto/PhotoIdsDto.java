package com.squarecross.photoalbum.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoIdsDto {

    List<Long> photoIds;

}
