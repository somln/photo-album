package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AlbumService albumService;
    public PhotoDto getPhotoById(long albumId, long photoId) {
        Photo photo = photoRepository.findByAlbum_AlbumIdAndPhotoId(albumId, photoId).orElseThrow(() ->
                new EntityNotFoundException(String.format("albumId '%d', photoId '%d' 인 사진이 존재하지 않습니다.", albumId, photoId)));
        PhotoDto photoDto = PhotoMapper.convertToDto(photo);
        photoDto.setAlbumId(albumId);
        return photoDto;
    }

}
