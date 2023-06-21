package com.squarecross.photoalbum.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "photo", schema = "photo_album", uniqueConstraints = {@UniqueConstraint(columnNames = "photo_id")} )
@Getter @Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id", unique = true, nullable = false)
    private Long photoId;

    @Column(name = "file_name", unique = false, nullable = false)
    private String fileName;

    @Column(name = "thumb_url", unique = false, nullable = false)
    private String thumbUrl;

    @Column(name = "original_url", unique = false, nullable = false)
    private String originalUrl;

    @Column(name = "file_size", unique = false, nullable = false)
    private int fileSize;

    @Column(name = "uploaded_at", unique = false, nullable = true)
    @CreationTimestamp
    private Date uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    //여러개의 photo가 하나의 album에 속해있기 때문에 ManyToOne
    //FetchType.LAZY: Album 정보가 필요할 때만 불러옴(사진 상세화면에서는 앨범정보 필요X)
    @JoinColumn(name="album_id")
    private Album album;

    public Photo(){};
}
