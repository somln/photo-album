package com.squarecross.photoalbum.Service;

import com.fasterxml.jackson.databind.ObjectReader;
import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.dto.PhotoIdsDto;
import com.squarecross.photoalbum.dto.PhotoMoveDto;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    public PhotoDto getPhotoById(long albumId, long photoId) {
        Photo photo = photoRepository.findByAlbum_AlbumIdAndPhotoId(albumId, photoId).orElseThrow(() ->
                new EntityNotFoundException(String.format("albumId '%d', photoId '%d' 인 사진이 존재하지 않습니다.", albumId, photoId)));
        return PhotoMapper.convertToDto(photo);
    }

    public PhotoDto uploadPhoto(Long albumId, MultipartFile file) {

        //해당 앨범이 존재하는 지 먼저 확인
        Album album = albumRepository.findById(albumId).orElseThrow(()
                -> new EntityNotFoundException("앨범이 존재하지 않습니다"));

        //파일의 이름과 크기 가져오기
        String fileName = file.getOriginalFilename();
        int fileSize = (int) file.getSize();

        //저장할 파일 이름 얻기
        fileName = getNextFileName(fileName, albumId);
        //원본 이미지와 썸네일 이미지 저장하기
        saveFile(file, albumId, fileName);

        //photo 객체 생성하기
        Photo photo = Photo.builder()
                .fileName(fileName)
                .fileSize(fileSize)
                .originalUrl(("/photos/original/" + albumId + "/" + fileName))
                .thumbUrl("/photos/thumb/" + albumId + "/" + fileName)
                .album(album)
                .build();
        //db에 저장하기
        Photo createdPhoto = photoRepository.save(photo);
        return PhotoMapper.convertToDto(createdPhoto);
    }

    private String getNextFileName(String fileName, Long albumId){

        //해당 앨범에 같은 이름의 파일이 있는지 확인
        Optional<Photo> res = photoRepository.findByAlbum_AlbumIdAndFileName(albumId, fileName);

        //파일 이름에서 확장자 제거
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        //파일 이름에서 확장자 추출
        String ext = StringUtils.getFilenameExtension(fileName);

        //이름이 같은 파일이 있을 경우 이름 뒤에 카운트를 붙임
        int count = 2;
        while(res.isPresent()){
            fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
            res = photoRepository.findByAlbum_AlbumIdAndFileName(albumId, fileName);
            count++;
        }

        return fileName;
    }

    private void saveFile(MultipartFile file, Long AlbumId, String fileName) {
        try {
            //원본 이미지를 original 사진 경로에 저장
            String filePath = AlbumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            //file 정보를 불러와서 BufferedImage 객체를 반환하고, 사이즈를 가로 300 세로 300으로 설정
            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
            //썸네일 이미지를 저장하기 위한 파일
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            //확장자가 null인 경우 잘못된 파일
            if (ext == null) {
                throw new IllegalArgumentException("No Extention");
            }
            //썸네일 이미지 저장
            ImageIO.write(thumbImg, ext, thumbFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
    /**
     Files.copy(): 파일의 데이터를 복사하는 일반적인 파일 복사 메서드
     ImageIO.write(): BufferedImage를 사용하여 이미지를 저장하는 특수한 메서드
     */

    public File getImageFile(Long photoId){
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new EntityNotFoundException(String.format("사진을 ID %d를 찾을 수 없습니다", photoId)));
        return new File(Constants.PATH_PREFIX+photo.getOriginalUrl());
    }


    public List<PhotoDto> getPhotoList(Long albumId, String sort) {
        List<Photo> photos;
        if (sort.equals("byDate")) {
            photos = photoRepository.findAllByAlbum_AlbumIdOrderByUploadedAt(albumId);
        } else if (sort.equals("byName")) {
            photos = photoRepository.findAllByAlbum_AlbumIdOrderByFileName(albumId);
        } else {
            throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
        }
        return PhotoMapper.converToDtoList(photos);
    }

    @Transactional
    public void movePhotos(PhotoMoveDto photoMoveDto) throws IOException {

        //이동할 앨범이 존재하는 지 확인
        Album album = albumRepository.findById(photoMoveDto.getToAlbumId())
                .orElseThrow(() -> new EntityNotFoundException("이동할 앨범이 존재하지 않습니다."));

        //이동할 photoId로 부터 photo 객체 가져오기
        List<Photo> photos = photoRepository.findAllById(photoMoveDto.getPhotoIds());

        for(Photo photo : photos){
            String fileName = getNextFileName(photo.getFileName(), photoMoveDto.getToAlbumId());
            String filePath = photoMoveDto.getToAlbumId() +"/"+ fileName;
            //파일 이동하기
            moveFile(photo.getOriginalUrl(), original_path, filePath);
            moveFile(photo.getThumbUrl(), thumb_path, filePath);
            //DB 수정하기
           photo.updateAlbum(album, filePath);
        }
    }

    private void moveFile(String toUrl, String path, String filePath) throws IOException {
        String srcPath = Constants.PATH_PREFIX + toUrl;
        String dstPath = path + "/" + filePath;
        Files.move(Paths.get(srcPath), Paths.get(dstPath));
    }


    public void deletePhotos(PhotoIdsDto photoIdsDto) throws IOException {
        List<Photo> photos = photoRepository.findAllById(photoIdsDto.getPhotoIds());

        for(Photo photo: photos){
            Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX+photo.getOriginalUrl()));
            Files.deleteIfExists(Paths.get(Constants.PATH_PREFIX+photo.getThumbUrl()));
        }

        photoRepository.deleteAllByIdInBatch(photoIdsDto.getPhotoIds());
    }

    public void validateFiles(MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            if (ImageIO.read(file.getInputStream()) == null){
                throw new IllegalArgumentException("이미지 파일만 올려주세요");
            }

            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if(!Constants.EXT_LIST.contains(ext)) {
                throw new IllegalArgumentException("이미지 파일만 올려주세요");
            }
        }
    }
}
