package kopo.poly.controller;

import kopo.poly.dto.WeatherDTO;
import kopo.poly.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/weather")
@RequiredArgsConstructor
@Controller
public class WeatherController {

    private final IWeatherService weatherService; // 날씨 서비스 객체 주입하기

    /**
     * 네이버 날씨 수집을 위한 URL 호출
     */
    @GetMapping(value = "toDayWeather")
    public String toDayWeather(ModelMap model)
            throws Exception {

        log.info(this.getClass().getName() + ".toDayWeather Start !");

        int res = weatherService.toDayWeather();

        // 크롤링 결과를 넣어주기
        model.addAttribute("msg", "네이버 날씨 홈페이지로부터 수집된 날씨는 총 " + res + "건입니다.");

        return "/weather/toDayWeather";
    }

    @GetMapping(value = "getWeatherInfo")
    public String getWeatherInfo(HttpServletRequest request, ModelMap model)
            throws Exception {

        log.info(this.getClass().getName() + ".getWeatherInfo Start !");

        // 수집된 영화 정보 조회하기
        List<WeatherDTO> rList = Optional.ofNullable(weatherService.getWeatherInfo()).orElseGet(ArrayList::new);

        // 조회 결과를 JSP에 전달하기
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".getWeatherInfo End !");

        return "/weather/weatherList";
    }
}