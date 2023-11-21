package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherDTO {

    private String collectTime; // 수집시간

    private String seq; // 수집된 데이터 순번

    private String region; // 지역

    private String temp; // 온도

    private String weather; // 현재 날씨


    // 아래에 있는 등록자 or 등록일자와 수정자 or 수정일자는 굳이 넣을 필요는 없지만 그냥 추가함
    private String regId;

    private String regDt;

    private String chgId;

    private String chgDt;

}
