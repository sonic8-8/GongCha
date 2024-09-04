package com.gongcha.berrymatch.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gongcha.berrymatch.chatMessage.ChatMessage;
import com.gongcha.berrymatch.chatRoom.ChatRoom;
import com.gongcha.berrymatch.game.Game;

import com.gongcha.berrymatch.group.UserGroup;
import com.gongcha.berrymatch.match.domain.Match;
import com.gongcha.berrymatch.post.Post;
import com.gongcha.berrymatch.postLike.PostLike;
import com.gongcha.berrymatch.springSecurity.constants.ProviderInfo;
import com.gongcha.berrymatch.user.RequestDTO.UserSignupServiceRequest;
import com.gongcha.berrymatch.userActivity.UserActivity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String identifier;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private District district;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    private String phoneNumber;

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    private String introduction;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ProviderInfo providerInfo;

    @Enumerated(EnumType.STRING)
    private UserMatchStatus userMatchStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivity> userActivities;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;




    @ManyToOne(fetch = FetchType.EAGER) // 즉시 로딩으로 변경
    @JoinColumn(name = "match_id") // 외래 키 컬럼 추가
    private Match match;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER) // 지연 로딩을 즉시 로딩으로 변경
    @JoinColumn(name = "group_id")
    private UserGroup userGroup;

    @Enumerated(EnumType.STRING)
    private UserMatchStatus matchStatus;

    public void updateMatchStatus(UserMatchStatus status) {
        this.matchStatus = status;
    }



    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_like_id")
    private PostLike postLike;


    @Builder
    public User(String identifier, String nickname, City city, District district, Gender gender, LocalDate birthDate, String phoneNumber, String profileImageUrl, String introduction, String email, Role role, LocalDateTime createdAt, ProviderInfo providerInfo, UserMatchStatus userMatchStatus) {
        this.identifier = identifier;
        this.nickname = nickname;
        this.city = city;
        this.district = district;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.role = role;
        this.createdAt = createdAt;
        this.providerInfo = providerInfo;
        this.userMatchStatus = userMatchStatus;
    }

    public boolean isRegistered() {
        return this.role != Role.NOT_REGISTERED;
    }

    /**
     * 회원가입 비즈니스 로직에 사용됨. 회원정보를 업데이트해주는 메서드
     */
    public void update(UserSignupServiceRequest request) {
        this.nickname = request.getNickname();
        this.city = request.getCity();
        this.district = request.getDistrict();
        this.gender = request.getGender();
        this.birthDate = request.getBirthdate();
        this.phoneNumber = request.getPhoneNumber();
        this.profileImageUrl = request.getProfileImageUrl();
        this.introduction = request.getIntroduction();
        this.role = Role.USER;
    }

    public User(Long id, City city, District district) {
        this.id = id;
        this.city = city;
        this.district = district;
    }


}
