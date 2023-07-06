package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlbumControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlbumController albumController;

    @Autowired
    private AlbumRepository albumRepository;

    @AfterEach
    void testDown(){
        albumRepository.deleteAll();
    }

    @Test
    @DisplayName("앨범 생성 POST 테스트")
    void createAlbum() {

        Album album = Album.createAlbum("test");
        AlbumDto albumDto = AlbumMapper.convertToDto(album);

        ResponseEntity<AlbumDto> responseEntity = restTemplate.postForEntity("/albums", albumDto, AlbumDto.class);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("test", responseEntity.getBody().getAlbumName());
        assertNotNull(responseEntity.getBody().getAlbumId());
        assertNotNull(responseEntity.getBody().getCreatedAt());
        assertEquals(0, responseEntity.getBody().getPhotoCount());

    }


    @Test
    @DisplayName("앨범 Id로 앨범 GET 테스트")
    void getAlbum() {

        Album album = Album.createAlbum("test");
        AlbumDto albumDto = AlbumMapper.convertToDto(album);
        ResponseEntity<AlbumDto> postResponseEntity = restTemplate.postForEntity("/albums", albumDto, AlbumDto.class);

        Long albumId = postResponseEntity.getBody().getAlbumId();
        ResponseEntity<AlbumDto> responseEntity = restTemplate.getForEntity("/albums/"+albumId, AlbumDto.class);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("test", responseEntity.getBody().getAlbumName());
        assertEquals(albumId, responseEntity.getBody().getAlbumId());
        assertNotNull(responseEntity.getBody().getCreatedAt());
        assertEquals(0, responseEntity.getBody().getPhotoCount());

    }

    @Test
    @DisplayName("앨범 목록 GET 테스트")
    void getAlbumList() {

        Album album1 = Album.createAlbum("test1");
        Album album2 = Album.createAlbum("test2");
        Album album3 = Album.createAlbum("test3");
        AlbumDto albumDto1 = AlbumMapper.convertToDto(album1);
        AlbumDto albumDto2 = AlbumMapper.convertToDto(album2);
        AlbumDto albumDto3 = AlbumMapper.convertToDto(album3);
        restTemplate.postForEntity("/albums", albumDto1, AlbumDto.class);
        restTemplate.postForEntity("/albums", albumDto2, AlbumDto.class);
        restTemplate.postForEntity("/albums", albumDto3, AlbumDto.class);

        ResponseEntity<List<AlbumDto>> responseEntity = restTemplate.exchange("/albums",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumDto>>() {});

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("test1", responseEntity.getBody().get(0).getAlbumName());
        assertEquals("test2", responseEntity.getBody().get(1).getAlbumName());
        assertEquals("test3", responseEntity.getBody().get(2).getAlbumName());
    }

    @Test
    @DisplayName("sort, keyword, orderBy로 앨범 검색 및 결과 정렬 GET 테스트")
    void getSortedAlbumList() {
        List<Album> albumList = new ArrayList<>();
        albumList.add(Album.createAlbum("ignoredAlbum"));

        for(int i=1; i<=4; i++){
            albumList.add(Album.createAlbum("test"+i));
        }

        Collections.shuffle(albumList);

        for(Album album: albumList){
            AlbumDto albumDto = AlbumMapper.convertToDto(album);
            restTemplate.postForEntity("/albums", albumDto, AlbumDto.class);
        }

        ResponseEntity<List<AlbumDto>> responseEntity = restTemplate.exchange("/albums?sort=byName&keyword=test&orderBy=desc",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumDto>>() {
                });

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(4, responseEntity.getBody().size());
        assertEquals("test4", responseEntity.getBody().get(0).getAlbumName());
        assertEquals("test3", responseEntity.getBody().get(1).getAlbumName());
        assertEquals("test2", responseEntity.getBody().get(2).getAlbumName());
        assertEquals("test1", responseEntity.getBody().get(3).getAlbumName());
    }


    @Test
    @DisplayName("앨범 ID로 앨범 DELETE 테스트")
    void deleteAlbum() {
        // given
        Album album = Album.createAlbum("test");
        AlbumDto albumDto = AlbumMapper.convertToDto(album);
        ResponseEntity<AlbumDto> postResponseEntity = restTemplate.postForEntity("/albums", albumDto, AlbumDto.class);

        // when
        restTemplate.delete("/albums" + postResponseEntity.getBody().getAlbumId());

        // then
        ResponseEntity<AlbumDto> responseEntity = restTemplate.getForEntity("/albums" + postResponseEntity.getBody().getAlbumId(), AlbumDto.class);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }
}