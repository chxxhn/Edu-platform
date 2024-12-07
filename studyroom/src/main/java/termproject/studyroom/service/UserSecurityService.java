//package termproject.studyroom.service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//import termproject.studyroom.config.auto.CustomUserDetails;
//import termproject.studyroom.config.auto.CustomUserDetailsService;
//import termproject.studyroom.model.Grade;
//import termproject.studyroom.repos.UserRepository;
//
//@Service
//@RequiredArgsConstructor
//public class UserSecurityService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<termproject.studyroom.domain.User> _siteUser = this.userRepository.findByEmailIgnoreCase(username);
//        if (_siteUser.isEmpty()) {
//            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
//        }
//
//        termproject.studyroom.domain.User siteUser = _siteUser.get();
////        List<GrantedAuthority> authorities = new ArrayList<>();
////        if ("admin@pusan.ac.kr".equals(username)) {
////            authorities.add(new SimpleGrantedAuthority(Grade.PROF.getValue()));
////        } else {
////            authorities.add(new SimpleGrantedAuthority(Grade.STD.getValue()));
////        }
//
//        return new CustomUserDetails(siteUser);
//    }
//}
//
