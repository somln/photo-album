package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.repository.AlbumRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired AlbumService albumService;
    @Autowired AlbumRepository albumRepository;

    @Test
    @DisplayName("앨범 아이디로 앨범 검색 테스트")
    void getAlbumById() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        Long albumId = savedAlbum.getAlbumId();
        Album findAlbum = albumService.getAlbumById(albumId);

        assertThat(findAlbum.getAlbumName()).isEqualTo("테스트");
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumById(albumId+1));

    }

    @Test
    @DisplayName("앨범 이름으로 앨범 검색 테스트")
    void getAlbumByName() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        Album findAlbum = albumService.getAlbumByName("테스트");

        assertThat(findAlbum).isEqualTo(savedAlbum);
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumByName("test"));
    }

}