package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    //특정 앨범에 속해있는 사진의 개수를 카운트
    int countByAlbum_AlbumId(Long albumId);
    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long albumId);
}
