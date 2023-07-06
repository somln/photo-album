package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
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
        Album album = Album.createAlbum("테스트");
        Album savedAlbum = albumRepository.save(album);

        Long albumId = savedAlbum.getAlbumId();
        AlbumDto findAlbum = albumService.getAlbum(albumId);

        assertThat(findAlbum.getAlbumName()).isEqualTo("테스트");
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbum(albumId+1));

    }

    @Test
    @DisplayName("앨범 이름으로 앨범 검색 테스트")
    void getAlbumByName() {
        Album album = Album.createAlbum("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto findAlbum = albumService.getAlbumByAlbumName("테스트");

        assertThat(findAlbum.getAlbumId()).isEqualTo(savedAlbum.getAlbumId());
        assertThrows(EntityNotFoundException.class, ()-> albumService.getAlbumByAlbumName("test"));
    }


    @Test
    @DisplayName("앨범 생성 테스트")
    void createAlbum() throws IOException {
        AlbumDto requestAlbumDto = AlbumDto.builder().albumName("test album").build();
        AlbumDto responseAlbumDto = assertDoesNotThrow(() -> albumService.createAlbum(requestAlbumDto));

        assertThat(responseAlbumDto.getAlbumName()).isEqualTo("test album");
        assertTrue(Files.exists(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + responseAlbumDto.getAlbumId())));
        assertTrue(Files.exists(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + responseAlbumDto.getAlbumId())));
    }

    @Test
    @DisplayName("앨범 검색, 정렬 테스트")
    void getAlbumList() throws InterruptedException {
        Album album1 = Album.createAlbum("test1");
        Album album2 = Album.createAlbum("test2");
        Album album3 = Album.createAlbum("aaa");


        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(1); //시간차를 벌리기위해 두번째 앨범 생성 1초 딜레이
        albumRepository.save(album2);
        albumRepository.save(album3);

        //앨범 생성된 순으로 오름차순 정렬 확인
        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtAsc("test");
        assertEquals("test1", resDate.get(0).getAlbumName());
        assertEquals("test2", resDate.get(1).getAlbumName());
        assertEquals(2, resDate.size());  //이름에 test가 들어간 앨범을 검색했을 때 2개가 나오는 지 확인

        //앨범명으로 내림차순 정렬 확인
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameDesc("test");
        assertEquals("test2", resName.get(0).getAlbumName());
        assertEquals("test1", resName.get(1).getAlbumName());
        assertEquals(2, resName.size());
    }

    @Test
    @DisplayName("앨범명 수정 테스트")
    void updateAlbum() throws IOException {
        //앨범 생성
        AlbumDto albumDto = AlbumDto.builder().albumName("before").build();
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId(); // 생성된 앨범 아이디 추출
        AlbumDto updateDto= AlbumDto.builder().albumName("after").build();
        albumService.updateAlbum(albumId, updateDto);

        AlbumDto updatedDto = albumService.getAlbum(albumId);

        //앨범명 변경되었는지 확인
        assertEquals("after", updatedDto.getAlbumName());
    }

    @Test
    @DisplayName("앨범 삭제 테스트")
    void deleteAlbum() throws IOException {
        AlbumDto albumDto = AlbumDto.builder().albumName("test").build();
        AlbumDto savedAlbum = assertDoesNotThrow(() -> albumService.createAlbum(albumDto));

        Long albumId = savedAlbum.getAlbumId();
        assertDoesNotThrow(() -> albumService.deleteAlbum(albumId));

        assertThrows(EntityNotFoundException.class, () -> albumService.getAlbum(albumId));
        assertFalse(Files.exists(Paths.get(Constants.PATH_PREFIX+"/photos/original/"+albumId)));
        assertFalse(Files.exists(Paths.get(Constants.PATH_PREFIX+"/photos/thumb/"+albumDto)));
    }

}
