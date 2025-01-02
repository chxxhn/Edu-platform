//package termproject.studyroom.controller.Member;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import termproject.studyroom.config.auto.CustomUserDetails;
//import termproject.studyroom.service.NoticationService;
//
//@RestController
//@RequestMapping("/api/user")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NoticationService noticationService;
//
//    @GetMapping(value = "/connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity<SseEmitter> subscribe(@parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails,
//                                                @RequestHeader(value = "Last-Event-ID" , required = false, defaultValue = "") String lastEventId)
//    {
//        return ResponseEntity.ok(noticationService.subscribe(customUserDetails.getUser().getStdId(),lastEventId));
//    }
//
//}
