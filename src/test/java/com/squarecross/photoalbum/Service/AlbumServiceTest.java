package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
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
    @Autowired PhotoRepository photoRepository;

    @Test
    @DisplayName("앨범 아이디로 앨범 검색 테스트")
    void getAlbumById() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        Long albumId = savedAlbum.getAlbumId();
        AlbumDto findAlbum = albumService.getAlbumById(albumId);

        assertThat(findAlbum.getAlbumName()).isEqualTo("테스트");
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumById(albumId+1));

    }

    @Test
    @DisplayName("앨범 이름으로 앨범 검색 테스트")
    void getAlbumByName() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto findAlbum = albumService.getAlbumByName("테스트");

        assertThat(findAlbum.getAlbumId()).isEqualTo(savedAlbum.getAlbumId());
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumByName("test"));
    }

    @Test
    @DisplayName("앨범의 사진 개수 카운트 테스트")
    void testPhotoCount(){

        Album album = new Album();
        album.setAlbumName("album1");
        albumRepository.save(album);

        Photo photo = new Photo();
        photo.setFileName("photo1");
        photo.setAlbum(album);
        photoRepository.save(photo);

        int count = photoRepository.countByAlbum_AlbumId(album.getAlbumId());
        assertThat(count).isEqualTo(1);
    }
}