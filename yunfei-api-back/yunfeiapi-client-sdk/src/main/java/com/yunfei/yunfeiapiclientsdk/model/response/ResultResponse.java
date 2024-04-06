package com.yunfei.yunfeiapiclientsdk.model.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResultResponse {

    private List<Map<String, Object>> data;
}
