package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumDto getAlbumById(Long albumId){
        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isPresent()){
            return AlbumMapper.convertToDto(res.get());
        }else {
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다.", albumId));
        }
    }

    public AlbumDto getAlbumByName(String albumName){
        Optional<Album> res = albumRepository.findByAlbumName(albumName);
        if(res.isPresent()){
            return AlbumMapper.convertToDto(res.get());
        }else {
            throw new EntityNotFoundException(String.format("앨범 이름 %s로 조회되지 않았습니다.", albumName));
        }
    }

}
