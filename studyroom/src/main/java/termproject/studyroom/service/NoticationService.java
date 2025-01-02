//package termproject.studyroom.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import termproject.studyroom.repos.EmitterRepository;
//import termproject.studyroom.repos.NotificationRepository;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
//    private final NotificationRepository notificationRepository;
//
//    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
//
//    public SseEmitter subscribe(Long memberId, String lastEventId) {
//        String emitterId = memberId + "_" + System.currentTimeMillis();
//        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
//
//        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
//        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
//
//        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");
//
//        if (!lastEventId.isEmpty()) {
//            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
//            events.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
//        }
//
//        return emitter;
//    }