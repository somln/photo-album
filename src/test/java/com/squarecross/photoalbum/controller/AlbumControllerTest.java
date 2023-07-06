package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.repository.AlbumRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
    @DisplayName("앨범 Id로 앨범 GET 테스트")
    void getAlbum() {

    }

    @Test
    void createAlbum() {
    }

    @Test
    void getAlbumList() {
    }

    @Test
    void updateAlbumName() {
    }

    @Test
    void deleteAlbum() {
    }
}