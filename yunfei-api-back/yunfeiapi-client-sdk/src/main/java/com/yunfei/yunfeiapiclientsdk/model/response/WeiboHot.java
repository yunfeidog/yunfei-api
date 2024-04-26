package com.yunfei.yunfeiapiclientsdk.model.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeiboHot implements Serializable {
    private Integer hotNum;

    private Integer index;

    private String hotType;

    private String title;

    private String url;
}
