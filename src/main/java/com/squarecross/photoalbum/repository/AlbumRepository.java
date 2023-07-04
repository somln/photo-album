package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    //앨범 이름이 일치하는 Album 객체 반환
    Optional<Album> findByAlbumName(String albumName);
    List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword);
    List<Album> findByAlbumNameContainingOrderByCreatedAtAsc(String keyword);
    List<Album> findByAlbumNameContainingOrderByAlbumNameDesc(String keyword);
    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword);
}
