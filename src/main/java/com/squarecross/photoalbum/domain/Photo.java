package com.squarecross.photoalbum.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "photo", schema = "photo_album", uniqueConstraints = {@UniqueConstraint(columnNames = "photo_id")} )
@Getter @Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id", unique = true, nullable = false)
    private Long photoId;

    @Column(name = "file_name", unique = false, nullable = true)
    private String fileName;

    @Column(name = "file_size", unique = false, nullable = true)
    private int fileSize;

    @Column(name="original_url", unique = false, nullable = true)
    private String originalUrl;

    @Column(name="thumb_url", unique = false, nullable = true)
    private String thumbUrl;

    @Column(name="uploaded_at", unique = false, nullable = true)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    //여러개의 photo가 하나의 album에 속해있기 때문에 ManyToOne
    //FetchType.LAZY: Album 정보가 필요할 때만 불러옴(사진 상세화면에서는 앨범정보 필요X)
    @JoinColumn(name="album_id")
    private Album album;

    @Builder
    public Photo(String fileName, int fileSize, String originalUrl, String thumbUrl, LocalDateTime uploadedAt, Album album) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.originalUrl = originalUrl;
        this.thumbUrl = thumbUrl;
        this.uploadedAt = uploadedAt;
        this.album =album;
    }

    public static Photo createPhoto(String fileName, int fileSize, String originalUrl, String thumbUrl, Album album) {
        return Photo.builder()
                .fileName(fileName)
                .fileSize(fileSize)
                .originalUrl(originalUrl)
                .thumbUrl(thumbUrl)
                .build();
    }

}
