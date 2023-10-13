package kopo.poly.persistance.mapper;

import kopo.poly.dto.WeatherDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IWeatherMapper {

    // 수집된 영화 순위 DB에 등록하기
    int insertWeatherInfo(WeatherDTO pDTO) throws Exception;

    // DB에 저장된 영화 순위 삭제하기
    int deleteWeatherInfo(WeatherDTO pDTO) throws Exception;

    // 수집된 내용을 조회하기
    List<WeatherDTO> getWeatherInfo(WeatherDTO pDTO)throws Exception;
}
