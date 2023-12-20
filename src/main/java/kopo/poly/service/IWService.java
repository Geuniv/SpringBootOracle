package kopo.poly.service;

import kopo.poly.dto.WDTO;

public interface IWService {
    String apiURL = "https://api.openweathermap.org/data/3.0/onecall";

    // 날씨 API를 호출하여 날씨 결과 받아오기
    WDTO getWeather(WDTO pDTO) throws Exception;
}