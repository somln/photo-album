package com.squarecross.photoalbum.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Getter @Setter
@Table(name="album", schema = "photo_album", uniqueConstraints = {@UniqueConstraint(columnNames = "album_id")})
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id", unique = true, nullable = false)
    private Long albumId;

    @Column(name = "album_name", unique = false, nullable = false)
    private String albumName;

    @Column(name = "created_at", unique = false, nullable = true)
    @CreationTimestamp  //새로운 앨범을 생성해 DB INSERT할 때 자동으로 현재 시간을 입력
    private Date createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = CascadeType.ALL)
    //FetchType.LAZY: photo 정보가 필요할 때만 불러옴
    //mappedBy="album": 반대쪽에 자신이 매핑되어 있는 필드명은 album
    //ascade = CascadeType.ALL: 부모 엔티티(album)에 대해 수행된 작업이 있을 때, 동일한 작업이 photo 엔터티로 자동 전파
    private List<Photo> photos;

    public Album(){}

}
