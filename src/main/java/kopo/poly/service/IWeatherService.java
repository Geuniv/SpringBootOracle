package kopo.poly.service;

import kopo.poly.dto.WeatherDTO;

import java.util.List;

public interface IWeatherService {

    // 웹상 ( 네이버 날씨 ) 에서 현재 날씨 가져오기
    int toDayWeather() throws Exception;

    // 수집된 내용을 조회하기
    List<WeatherDTO> getWeatherInfo() throws Exception;

}