package com.yunfei.yunfeiapiclientsdk.model.response;

import lombok.Data;

import java.util.List;

/**
 * @author PYW
 */
@Data
public class WeiboHotSearchResponse {
    private List<WeiboHot> weibohotSearch;

    public static class WeiboHot {
        private Integer hotNum;

        private Integer index;

        private String hotType;

        private String title;

        private String url;
    }

}

