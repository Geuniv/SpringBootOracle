package kopo.poly.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.OcrDTO;
import kopo.poly.service.IOcrService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/ocr")
@RequiredArgsConstructor
@Controller
public class OcrController {

    private final IOcrService ocrService;

    // 업로드되는 파일이 저장되는 기본 폴더 설정 ( 자바에서 경로는 / 로 표현함 )
    final private String FILE_UPLOAD_SAVE_PATH = "c:/upload"; // C:\\upload 폴더에 저장

    final private String FILE_DOWNLOAD_SAVE_PATH = "c:/download";

    /**
     * 이미지 인식을 위한 파일 업로드 화면 호출
     */
    @GetMapping(value = "uploadImage")
    public String uploadImage() {

        log.info(this.getClass().getName() + ".uploadImage !");

        return "/ocr/uploadImage";

    }

   /* *//**
     * 파일업로드 및 이미지 인식
     *//*
    @PostMapping(value = "readImage")
    public String readImage(ModelMap model, @RequestParam(value = "fileUpload")MultipartFile mf)
        throws Exception {

        log.info(this.getClass().getName() + ".readImage Start !");

        // OCR 실행 결과
        String res = "";

        // 업로드하는 실제 파일명
        // 다운로드 기능 구현시, 임의로 정의된 파일명을 원래대로 만들어주기 위한 목적
        String originalFileName = mf.getOriginalFilename();

        // 파일 확장자 가져오기 ( 파일 확장자를 포함한 전체 이름 (myimage.jpg ) 에서 뒤쪽부터 .이 존재하는 위치 찾기
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1,
                originalFileName.length()).toLowerCase();

        // 이미지 파일만 실행되도록 함
        if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {

            //웹서버에 저장되는 파일 이름
            // 업로드하는 파일 이름에 한글, 특수 문자들이 저장될 수 있기 때문에 강제로 영어와 숫자로 구성된 파일명으로 변경해서 저장한다.
            // 리눅스나 유닉스 등 운영체제는 다국어 지원에 취약하기 때문이다.
            String saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;

            // 웹 서버에 업로드한 파일 저장하는 물리적 경로
            String saveFilePath = FileUtil.mkdirForDate(FILE_UPLOAD_SAVE_PATH);

            String fullFileInfo = saveFilePath + "/" + saveFileName;

            // 정상적으로 값이 생성 되었는지 로그 찍어서 확인
            log.info("ext" + ext);
            log.info("saveFileName" + saveFileName);
            log.info("saveFilePath" + saveFilePath);
            log.info("fullFileInfo" + fullFileInfo);

            // 업로드 되는 파일을 서버에 저장
            mf.transferTo(new File(fullFileInfo));

            OcrDTO pDTO = new OcrDTO();

            pDTO.setFileName(saveFileName); // 저장되는 파일명
            pDTO.setFilePath(saveFilePath); // 저장되는 경로
            pDTO.setExt(ext); // 확장자
            pDTO.setOrgFileName(originalFileName); // 원래 파일명
            pDTO.setRegId("admin");

            //ocrService.getReadforImageText(pDTO) 결과를 Null 값 체크하여 rDTO 객체에 저장하기
            OcrDTO rDTO = Optional.ofNullable(ocrService.getReadforImageText(pDTO)).orElseGet(OcrDTO::new);

            res = CmmUtil.nvl(rDTO.getTextFromImage()); // 인식 결과

            rDTO = null;
            pDTO = null;

        } else {
            res = "이미지 파일이 아니라서 인식이 불가능합니다.";

        }

        // 이미지로부터 인식된 문자를 JSP에 전달하기
        model.addAttribute("res", res);

        log.info(this.getClass().getName() + ".readImage End !");

        return "/ocr/readImage";
    }*/

    @GetMapping(value = "ocrList")
    public String OcrList(ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".OcrList Start!");

        List<OcrDTO> rList = Optional.ofNullable(ocrService.getOcrList())
                .orElseGet(ArrayList::new);


        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".OcrList End!");

        return "ocr/ocrList";
    }

    @ResponseBody
    @PostMapping(value = "ocrDownload")
    public void fileDownload(HttpServletRequest request) throws IOException {
        log.info(this.getClass().getName() + ".fileDownload!");

        try {
            String filename = CmmUtil.nvl(request.getParameter("filename"));
            String filepath = CmmUtil.nvl(request.getParameter("filepath"));
            String ext = CmmUtil.nvl(request.getParameter("ext")).toUpperCase();
            String saveFilePath = FileUtil.mkdirForDate(FILE_DOWNLOAD_SAVE_PATH);
            String full = filepath.trim() + "/" + filename.trim();
            String full2 = full.replaceAll("/", "\\");
            log.info(full2);

            String savefull = saveFilePath + "/" + filename.trim();
            String savefull2 = savefull.replaceAll("/", "\\");
            log.info(savefull2);

            File file = new File(filename);
            BufferedImage image = ImageIO.read(new File(full2));
            if(image != null) {
                log.info("1");
            }
            if(file.exists()) {
                log.info("2");
            }
            ImageIO.write(image, ext, file);
            log.info(this.getClass().getName() + ".readImage End!");
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/download")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            // fileName 파라미터로 파일명을 가져온다.
            String fileName = request.getParameter("fileName").trim();
            log.info("0");
            String oriName = request.getParameter("orgFileName").trim();

            log.info("0");
//            String saveFilePath = FileUtil.mkdirForDate(FILE_UPLOAD_SAVE_PATH);
//            String savefull = saveFilePath + "/";
//            String savefull2 = savefull.replaceAll("/", "\\\\\\\\");
//            // 파일이 실제 업로드 되어있는(파일이 존재하는) 경로를 지정한다.
//            String filePath = savefull2;
            String filePath = request.getParameter("filePath").trim();

            // 경로와 파일명으로 파일 객체를 생성한다.
            File dFile = new File(filePath, fileName);

            log.info(filePath);
            log.info(fileName);

            // 파일 길이를 가져온다.
            int fSize = (int) dFile.length();

            // 파일이 존재
            if (fSize > 0) {
                log.info("1");

                // 파일명을 URLEncoder 하여 attachment, Content-Disposition Header로 설정
                String encodedFilename = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode(oriName, "UTF-8");

                // ContentType 설정
                response.setContentType("application/octet-stream; charset=utf-8");

                // Header 설정
                response.setHeader("Content-Disposition", encodedFilename);

                // ContentLength 설정
                response.setContentLengthLong(fSize);

                BufferedInputStream in = null;
                BufferedOutputStream out = null;

                /* BufferedInputStream
                 *
                java.io의 가장 기본 파일 입출력 클래스
                입력 스트림(통로)을 생성해줌
                사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
                속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음
                */
                in = new BufferedInputStream(new FileInputStream(dFile));

                /* BufferedOutputStream
                 *
                java.io의 가장 기본이 되는 파일 입출력 클래스
                출력 스트림(통로)을 생성해줌
                사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
                속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음
                */
                out = new BufferedOutputStream(response.getOutputStream());

                log.info("2");
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;

                    /*
                    모두 현재 파일 포인터 위치를 기준으로 함 (파일 포인터 앞의 내용은 없는 것처럼 작동)
                    int read() : 1byte씩 내용을 읽어 정수로 반환
                    int read(byte[] b) : 파일 내용을 한번에 모두 읽어서 배열에 저장
                    int read(byte[] b. int off, int len) : 'len'길이만큼만 읽어서 배열의 'off'번째 위치부터 저장
                    */
                    while ((bytesRead = in .read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    // 버퍼에 남은 내용이 있다면, 모두 파일에 출력
                    out.flush();
                    log.info("3");
                } finally {
                    /*
                    현재 열려 in,out 스트림을 닫음
                    메모리 누수를 방지하고 다른 곳에서 리소스 사용이 가능하게 만듬
                    */
                    in.close();
                    out.close();
                }
            } else {
                throw new FileNotFoundException("파일이 없습니다.");
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @PostMapping(value = "readImage")
    public String readImage(ModelMap model, @RequestParam(value = "fileUpload") MultipartFile mf) throws Exception {
        log.info(this.getClass().getName() + ".readImageStart!");

        String res = "";

        String originalFileName = mf.getOriginalFilename();

        log.info(originalFileName);

        String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1,
                originalFileName.length()).toLowerCase();

        log.info(ext);

        if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {

            String saveFileName = DateUtil.getDateTime("HHmmss") + "." + ext;

            String saveFilePath = FileUtil.mkdirForDate(FILE_UPLOAD_SAVE_PATH);

            String fullFileInfo = saveFilePath + "/" + saveFileName;

            log.info("ext : " + ext);
            log.info("saveFileName : " + saveFileName);
            log.info("saveFilePath : " + saveFilePath);
            log.info("fullFileInfo : " + fullFileInfo);

            mf.transferTo(new File(fullFileInfo));

            OcrDTO pDTO = new OcrDTO();

            pDTO.setFileName(saveFileName);
            pDTO.setFilePath(saveFilePath);
            pDTO.setExt(ext);
            pDTO.setOrgFileName(originalFileName);
            pDTO.setRegId("admin");
            pDTO.setChgId("admin");

            OcrDTO rDTO = Optional.ofNullable(ocrService.getReadforImageText(pDTO)).orElseGet(OcrDTO::new);

            res = CmmUtil.nvl(rDTO.getTextFormImage());

            rDTO = null;
            pDTO = null;

        } else {
            res = "이미지 파일이 아니라서 인식이 불가능합니다.";
        }
        model.addAttribute("res", res);
        log.info(this.getClass().getName() + ".readImage End!");

        return "ocr/readImage";
    }
}
