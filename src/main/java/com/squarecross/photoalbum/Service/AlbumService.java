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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    public AlbumDto getAlbum(Long albumId){
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("id " + albumId + "인 앨범이 없습니다."));
        AlbumDto albumDto = AlbumMapper.convertToDto(album);
        albumDto.setPhotoCount(photoRepository.countByAlbum_AlbumId(albumId));
        return AlbumMapper.convertToDto(album);
    }

    public AlbumDto getAlbumByAlbumName(String albumName) {
        Album album = albumRepository.findByAlbumName(albumName).
                orElseThrow(() -> new EntityNotFoundException());
        AlbumDto albumDto = AlbumMapper.convertToDto(album);
        albumDto.setPhotoCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
        return AlbumMapper.convertToDto(album);
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

    public AlbumDto updateAlbum(long albumId, AlbumDto albumDto){

        Optional<Album> album = albumRepository.findById(albumId);
        if(album.isEmpty()) {
            throw new EntityNotFoundException(String.format("album ID '%d'가 존재하지 않습니다", albumId));
        }

        Album updateAlbum = album.get();
        updateAlbum.setAlbumName(albumDto.getAlbumName());
        Album savedAlbum = albumRepository.save(updateAlbum);
        return AlbumMapper.convertToDto(savedAlbum);
    }

    public void deleteAlbum(long albumId) throws IOException {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 앨범입니다."));
        albumRepository.deleteById(albumId);
        try {
            deleteAlbumDirectories(album);
        } catch (IOException e) {
            log.error(album + "앨범 디렉토리 삭제 실패", e);
            throw new IOException("앨범 디렉토리 삭제 실패");
        }
    }

    public void deleteAlbumDirectories(Album album) throws IOException {

        //디렉토리의 전체 경로 저장하기
        String originalPath = Constants.PATH_PREFIX+"/photos/original/"+album.getAlbumId();
        String thumbPath = Constants.PATH_PREFIX+"/photos/thumb/"+album.getAlbumId();

        Stream.concat(Files.walk(Paths.get(originalPath)), Files.walk(Paths.get(thumbPath)))
                //originalPath 아래 모든 파일목록과 thumbPath 아래 모든 파일목록 합치기
                .filter(Files::isRegularFile)
                //디렉토리가 아니라 일반 파일이 맞는지 확인
                .forEach( path -> {
                    try {
                        Files.deleteIfExists(path); //파일 삭제
                    } catch (IOException e) {
                        log.error("파일 삭제 실패: " + path, e);
                    }
                        });

        Files.deleteIfExists(Paths.get(originalPath));  //original 앨범 디렉토리 삭제
        Files.deleteIfExists(Paths.get(thumbPath));  //thumb 앨범 디렉토리 삭제
    }
}
