package kopo.poly.service;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IUserInfoService {

    // 아이디 중복 체크
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 주소 중복 체크 및 인증 값
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 히원 가입하기 ( 회원정보 등록하기 )
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원정보 리스트
    List<UserInfoDTO> getUserList() throws Exception;

     // 회원 상세보기
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;
}
