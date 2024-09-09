package com.gongcha.berrymatch.match.Service;

import com.gongcha.berrymatch.exception.BusinessException;
import com.gongcha.berrymatch.exception.ErrorCode;
import com.gongcha.berrymatch.match.Repository.MatchingQueueRepository;
import com.gongcha.berrymatch.match.domain.MatchQueueStatus;
import com.gongcha.berrymatch.match.domain.MatchUser;
import com.gongcha.berrymatch.match.domain.MatchingQueue;
import com.gongcha.berrymatch.notification.NotificationService;
import com.gongcha.berrymatch.user.User;
import com.gongcha.berrymatch.user.UserMatchStatus;
import com.gongcha.berrymatch.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 여기는 유저 매칭대기열 상태를 관리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class UserStatusUpdateService {

    private final MatchingQueueRepository matchingQueueRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 매치에 참여한 유저들의 상태를 MATCHED로 업데이트합니다.
     *
     * @param matchUsers 매치에 포함된 유저 리스트
     */
    @Transactional
    public void updateUserStatusToMatched(List<MatchUser> matchUsers) {
        for (MatchUser matchUser : matchUsers) {
            Optional<MatchingQueue> matchingQueueOpt = matchingQueueRepository.findByUserId(matchUser.getUser().getId());

            matchingQueueOpt.ifPresent(matchingQueue -> {
                if (matchingQueue.getStatus() == MatchQueueStatus.PENDING) {
                    matchingQueue.setStatus(MatchQueueStatus.MATCHED);
                    matchingQueueRepository.save(matchingQueue);

                    User user = userRepository.findById(matchUser.getUser().getId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

                    user.updateMatchStatus(UserMatchStatus.MATCHED); // User의 매칭 상태 업데이트
                    userRepository.save(user);

                    notificationService.createSseEmitter(matchUser.getUser().getId());
                    notificationService.sendMatchStatus(matchUser.getUser().getId()); // SSE 알림

                }
            });
        }
    }
}

// 매칭된 유저 상태 업데이트
