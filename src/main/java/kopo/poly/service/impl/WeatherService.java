package kopo.poly.service.impl;

import kopo.poly.dto.WeatherDTO;
import kopo.poly.persistance.mapper.IWeatherMapper;
import kopo.poly.service.IWeatherService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService implements IWeatherService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // movieMapper 변수에 이미 메모리에 올라간 Mapper 객체를 넣어줌
    // 예전에는 autowired 어노테이션을 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함

    private final IWeatherMapper weatherMapper;

    /**
     * JSOUP 라이브러리를 통한 네이버 날씨 정보 가져오기
     */
    @Transactional
    @Override
    public int toDayWeather() throws Exception {

        // 로그 찍기 ( 추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다. )
        log.info(this.getClass().getName() + ".toDayWeather Start !");

        String collectTime = DateUtil.getDateTime("yyyyMMdd"); // 수집 날짜 = 오늘 날짜

        WeatherDTO pDTO = new WeatherDTO();
        pDTO.setCollectTime(collectTime);

        // 기존에 수집된 날씨 데이터 삭제하기
        weatherMapper.deleteWeatherInfo(pDTO);

        pDTO = null; // 기존 등록된 날씨 삭제 후, pDTO 값 제거하기

        int res = 0; // 크롤링 결과 ( 0보다 크면 크롤링 성공 )

        // 네이버 날씨 정보 가져올 사이트 주소
        String url = "https://weather.naver.com/";

        // JSOUP 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null; //

        // 사이트 접속 ( Http 프로토콜만 가능, https 프로토콜은 보안상 안됨 )
        doc = Jsoup.connect(url).get();

        // 네이버 웹페이지의 전체 소스 중 일부 태그를 선택하기 위해 사용
        // <div class="is_today korean"> 이 태그 내에서 있는 HTML 소스만 element에 저장됨
        Elements element = doc.select("div.is_today");

        // Iterator 을 사용하여 영화 순위 정보를 가져오기
        // 날씨정보는 기본적으로 1개 이상의 영화가 존재하기 때문에 태그의 반복이 존재할 수 밖에 없음
        Iterator<Element> region = element.select("strong.location_name").iterator(); // 지역명
        Iterator<Element> temp = element.select("strong.current").iterator(); // 온도
        Iterator<Element> weather = element.select("span.weather").iterator(); // 날씨

        // 수집된 데이터 DB에 저장하기
        while (region.hasNext()) {

            pDTO = new WeatherDTO(); // 수집된 날씨정보를 DTO에 저장하기 위해 메모리에 올리기

            // 수집시간을 기본키(pk)로 사용
            pDTO.setCollectTime(collectTime);

            // 날씨 정보(trim 함수 추가 이유 : trim 함수는 글자의 앞뒤 공백 삭제 역할을 수행하여, 데이터 수집시,
            //  홈페이지 개발자들을 앞뒤 공백 집어넣을 수 있어서 추가 )
            String reg = CmmUtil.nvl(region.next().text()).trim(); // No.1 들어옴
            pDTO.setRegion(reg); // 지역명

            log.info("reg : " + reg);

            // 온도
            pDTO.setTemp(CmmUtil.nvl(temp.next().text()).trim());

            log.info("tem : " + temp);

            // 날씨
            pDTO.setWeather(CmmUtil.nvl(weather.next().text()).trim());

            log.info("wea : " + weather);

            // 등록자
            pDTO.setRegId("admin");

            // DTO에 들어온 값 확인하기
            log.info("pDTO : " + pDTO);

            // 날씨 정보 한개씩 추가
            res += weatherMapper.insertWeatherInfo(pDTO);

        }

        // 로그 찍기 ( 추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다. )
        log.info(this.getClass().getName() + ".toDayWeather End !");

        return res;

    }

    @Override
    public List<WeatherDTO> getWeatherInfo() throws Exception {

        // 로그 찍기 ( 추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다. )
        log.info(this.getClass().getName() + ".getWeatherInfo Start !");

        String collectTime = DateUtil.getDateTime("yyyyMMdd"); // 수집날짜 = 오늘 날짜

        WeatherDTO pDTO = new WeatherDTO();
        pDTO.setCollectTime(collectTime);

        log.info(pDTO.toString());

        // DB에서 조회하기
        List<WeatherDTO> rList = weatherMapper.getWeatherInfo(pDTO);

        // 로그 찍기 ( 추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다. )
        log.info(this.getClass().getName() + "getWeatherInfo End !");

        return rList;
    }
}