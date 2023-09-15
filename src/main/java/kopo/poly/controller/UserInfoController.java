package kopo.poly.controller;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.java2d.cmm.PCMM;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + "./user/userRegForm");

        return "/user/userRegForm";
    }

    /**
     * 회원 가입 전 아이디 중복체크하기 ( Ajax를 통해 입력한 아이디 정보 받음 )
     */
    @ResponseBody
    @PostMapping("getUserIdExists")
    public UserInfoDTO getUserExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start !");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        // 회원아이디를 통해 중복된 아이디인지 조히
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists End !");

        return rDTO;
    }

    /**
     * 회원 가입 전 이메일 중복체크하기 ( Ajax를 통해 입력한 아이디 정보 받음 )
     * 유효한 이메일인지 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     */
    @ResponseBody
    @PostMapping("getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start !");

        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End !");

        return rDTO;
    }

    /**
     * 회원가입 로직 처리
     */
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start !");

        int res = 0; // 회원가입 결과
        String msg = ""; // 회원가입 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조

        // 웹 ( 회원정보 입력화면 ) 에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            /*
            * ########################################
            *               웹 ( 회원정보 입력화면 ) 에서 받는 정보를 String 변수에 저장 시작 !
            *
            * 무조건 웹으로 받는 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
            * ########################################
            */
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일
            String addr1 = CmmUtil.nvl(request.getParameter("addr1")); // 주소
            String addr2 = CmmUtil.nvl(request.getParameter("addr2")); // 상세주소
            /*
             * ########################################
             *               웹 ( 회원정보 입력화면 ) 에서 받는 정보를 String 변수에 저장 끝 !
             *
             * 무조건 웹으로 받는 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * ########################################
             */

            /*
             * ########################################
             *      반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             *                                  반드시 작성할 것
             * ########################################
             */
            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("password : " + password);
            log.info("email : " + email);
            log.info("addr1 : " + addr1);
            log.info("addr2 : " + addr2);


            /*
             * ########################################
             *         웹 ( 회원정보 입력화면 ) 에서 받는 정보를 DTO에 저장하기 시작 !
             *
             *         무조건 웹으로 받는 정보는 DTO에 저장해야 한다고 이해하길 권함
             * ########################################
             */

            // 웹 ( 회원정보 입력화면 )에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);
            pDTO.setUserName(userName);

            // 비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            /*
             * ########################################
             *         웹 ( 회원정보 입력화면 ) 에서 받는 정보를 DTO에 저장하기 끝 !
             *
             *         무조건 웹으로 받는 정보는 DTO에 저장해야 한다고 이해하길 권함
             * ########################################
             */

            /*
             * 회원가입
             * */
            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res)" + res);

            if (res == 1) {
                msg = "회원가입되었습니다.";

                        // 추후 회원가입 입력화면에서 Ajax를 활용해서 아이디 중복, 이메일 중복을 체크하길 바람
            } else if (res == 2) {
                msg = "이미 가입된 아이디입니다.";
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
            }
        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertUserInfo End !");
        }

        return dto;
    }

    // 회원정보 리스트
    @GetMapping(value = "userList")
    public String userList(HttpSession session, ModelMap model)
            throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".userList Start!");

        // 로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트 출력하는 함수에서 로그인 한 것처럼 Session 값을 생성함
//        session.setAttribute("SESSION_USER_ID", "USER01");

        // 공지사항 리스트 조회하기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<UserInfoDTO> rList = Optional.ofNullable(userInfoService.getUserList())
                .orElseGet(ArrayList::new);

//        List<MailDTO> rList = mailService.getMailList();
//
//        if (rList == null) {
//            rList = new ArrayList<>();
//        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".userList End!");

        // 함수 처리가 끝나고 보여줄 JSP 파일명
        // webapp/WEB-INF/views/Mail/MailList.jsp
        return "user/userList";

    }

    @GetMapping(value = "userInfo")
    public String userInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".userInfo Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("userId : " + userId);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        UserInfoDTO rDTO = Optional.ofNullable(
                userInfoService.getUserInfo(pDTO)
        ).orElseGet(UserInfoDTO::new);


        rDTO.setEmail(
                EncryptUtil.decAES128CBC(
                        CmmUtil.nvl(rDTO.getEmail())
                )
        );

        log.info("rDTO : " + rDTO.toString());

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".userInfo End!");

        // 함수 처리가 끝나고 보여줄 JSP 파일명
        // webapp/WEB-INF/views/user/userInfo.jsp
        return "user/userInfo";
    }
}
