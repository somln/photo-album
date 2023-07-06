package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;

import java.util.List;
import java.util.stream.Collectors;

public class AlbumMapper {

    public static AlbumDto convertToDto(Album album){
        return AlbumDto.builder()
                .albumId(album.getAlbumId())
                .albumName(album.getAlbumName())
                .createdAt(album.getCreatedAt())
                .build();
    }

    public static Album convertToModel(AlbumDto albumDto){
        return Album.dtoBuilder()
                .albumId(albumDto.getAlbumId())
                .albumName(albumDto.getAlbumName())
                .createdAt(albumDto.getCreatedAt())
                .build();
    }

    public static List<AlbumDto> convertToDtoList(List<Album> albums){
        return albums.stream().map(AlbumMapper::convertToDto).collect(Collectors.toList());
    }
}
