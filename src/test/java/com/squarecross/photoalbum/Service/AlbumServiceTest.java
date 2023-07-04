package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
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
        AlbumDto findAlbum = albumService.getAlbum(albumId);

        assertThat(findAlbum.getAlbumName()).isEqualTo("테스트");
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbum(albumId+1));

    }

    @Test
    @DisplayName("앨범 이름으로 앨범 검색 테스트")
    void getAlbumByName() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto findAlbum = albumService.getAlbumByAlbumName("테스트");

        assertThat(findAlbum.getAlbumId()).isEqualTo(savedAlbum.getAlbumId());
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumByAlbumName("test"));
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

    @Test
    @DisplayName("앨범 생성 테스트")
    void testCreateAlbum() throws IOException {
        AlbumDto requestAlbumDto = new AlbumDto();
        requestAlbumDto.setAlbumName("test album");
        AlbumDto responseAlbumDto = albumService.createAlbum(requestAlbumDto);

        assertThat(responseAlbumDto.getAlbumName()).isEqualTo("test album");
        assertTrue(Files.exists(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + responseAlbumDto.getAlbumId())));
        assertTrue(Files.exists(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + responseAlbumDto.getAlbumId())));
    }

    @Test
    @DisplayName("앨범 검색, 정렬 테스트")
    void testGetAlbumList() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("abcd");
        album2.setAlbumName("abce");

        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(5); //시간차를 벌리기위해 두번째 앨범 생성 1초 딜레이
        albumRepository.save(album2);


        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("abc");
        assertEquals("abcd", resDate.get(0).getAlbumName());
        assertEquals("abce", resDate.get(1).getAlbumName());
        assertEquals(2, resDate.size());

        //앨범명 정렬, aaaa -> aaab 기준으로 나와야합니다
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("abc");
        assertEquals("abcd", resName.get(0).getAlbumName());
        assertEquals("abce", resName.get(1).getAlbumName());
        assertEquals(2, resName.size());
    }

}
