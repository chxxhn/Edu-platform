package termproject.studyroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureUser;
import termproject.studyroom.domain.LectureUserId;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.LectureUserDTO;
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.repos.LectureUserRepository;
import termproject.studyroom.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class LectureUserService {

    private final LectureUserRepository lectureUserRepository;

    public void enrollUserToLecture(User user, LectureList lectureList) {
        // 복합 키 생성
        LectureUserId lectureUserId = new LectureUserId(user.getStdId(), lectureList.getLectureId());

        // LectureUser 객체 생성
        LectureUser lectureUser = new LectureUser();
        lectureUser.setId(lectureUserId);
        lectureUser.setUser(user);
        lectureUser.setLecture(lectureList);

        // 데이터베이스에 저장
        lectureUserRepository.save(lectureUser);
    }
    public boolean existsLectureIdAndStdId(final Integer lectureId, final Integer stdId ) {
        return lectureUserRepository.existsByLecture_LectureIdAndUser_StdId(lectureId, stdId);
    }


}
