package com.squarecross.photoalbum.Service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

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

    public AlbumDto createAlbum(AlbumDto albumDto) throws IOException {
        Album album = AlbumMapper.convertToModel(albumDto);
        albumRepository.save(album);
        createAlbumDirectories(album);
        return AlbumMapper.convertToDto(album);
    }

    private void createAlbumDirectories(Album album) throws IOException {
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId()));
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId()));
        //Paths.get: 메서드 내 디렉토리 경로를 넣어줌으로써 Paths 객체를 만들고.
        // Files.createDirectories: 메서드 내 Paths 객체를 입력하면 폴더 생성
    }

    public List<AlbumDto> getAlbumList(String keyword, String sort, String orderBy) {
        List<Album> albums;
        switch (sort) {
            case "byDate":
                if (orderBy.equals("asc")) {
                   albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtAsc(keyword);
                } else if (orderBy.equals("desc")) {
                    albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
                } else throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
                break;

            case "byName":
                if (orderBy.equals("asc")) {
                    albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
                } else if (orderBy.equals("desc")) {
                    albums =  albumRepository.findByAlbumNameContainingOrderByAlbumNameDesc(keyword);
                } else throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
                break;
            default:
                throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
        }
        List<AlbumDto> albumDtos = AlbumMapper.convertToDtoList(albums);

        for(AlbumDto albumDto: albumDtos){
            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(albumDto.getAlbumId());
           albumDto.setThumbUrls( top4.stream().map(Photo::getThumbUrl).map(c -> Constants.PATH_PREFIX + c).collect(Collectors.toList()));
        }
        return albumDtos;
    }

}
