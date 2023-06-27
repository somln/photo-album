package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final AlbumMapper albumMapper;

    public AlbumDto getAlbum(Long albumId){
        Optional<Album> res=albumRepository.findById(albumId);
        if(res.isPresent()){
            AlbumDto albumDto= AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        }else{
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다.",albumId));
        }
    }

    public AlbumDto getAlbumByAlbumName(String albumName) {
        Optional<Album> res = albumRepository.findByAlbumName(albumName);
        if (res.isPresent()) {
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
            return albumDto;
        } else {
            throw new EntityNotFoundException();
        }
    }


}
