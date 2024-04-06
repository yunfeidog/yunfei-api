package com.yunfei.yunfeiapiinterface.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunfei.yunfeiapiclientsdk.model.params.HoroscopeParams;
import com.yunfei.yunfeiapiclientsdk.model.params.UserParams;
import com.yunfei.yunfeiapiclientsdk.model.params.WebFaviconIconParams;
import com.yunfei.yunfeiapiclientsdk.model.response.*;
import com.yunfei.yunfeiapiclientsdk.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询名称 api
 * @author houyunfei
 */
@RestController
@RequestMapping("/")
@Slf4j
public class InterfaceController {

    @GetMapping()
    public String getNameByGet(String name) {
        return "Get 你的名字是" + name;
    }

    @PostMapping()
    public String getNameByPost(@RequestParam String name) {
        return "Post 你的名字是" + name;
    }

    /**
     * 通过POST请求获取用户名
     *
     * @param user 用户信息
     * @return 用户名
     */
    @PostMapping("/name")
    public UserResponse getUsernameByPost(@RequestBody UserParams user) {
        log.info("访问成功 user:{}", user);
        UserResponse userResponse = BeanUtil.copyProperties(user, UserResponse.class);
        return userResponse;
    }

    /**
     * 获取毒鸡汤
     *
     * @return 返回毒鸡汤
     */
    @GetMapping("/poisonousChickenSoup")
    public PoisonousChickenSoupResponse getPoisonousChickenSoup() {
        String poisonousChickenSoupJson = RequestUtils.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
        PoisonousChickenSoupResponse poisonousChickenSoupResponse = JSON.parseObject(poisonousChickenSoupJson, PoisonousChickenSoupResponse.class);
        return poisonousChickenSoupResponse;
    }

    /**
     * 获取微博热搜
     *
     * @return 返回微博热搜
     */
    @GetMapping("/weiboHotSearch")
    public WeiboHotSearchResponse getWeiboHotSearch() {
        // 1. 访问微博热搜接口
        String responseJson = RequestUtils.get("https://weibo.com/ajax/side/hotSearch");
        // 解析 JSON
        JSONObject jsonObject = JSONObject.parseObject(responseJson);

        // 获取微博的realtime数组
        JSONArray realtimeArray = jsonObject.getJSONObject("data").getJSONArray("realtime");
        // 遍历realtime数组并只保留note、label_name和num字段

        List<WeiboHotSearchResponse.WeiboHot> weiboHotList = new ArrayList<>();
        for (int i = 0; i < realtimeArray.size(); i++) {
            JSONObject realtimeObject = realtimeArray.getJSONObject(i);
            JSONObject filteredObject = new JSONObject();
            String note = realtimeObject.getString("note");
            filteredObject.put("index", i + 1);
            filteredObject.put("title", note);
            filteredObject.put("hotType", realtimeObject.getString("label_name"));
            filteredObject.put("hotNum", realtimeObject.getInteger("num"));
            filteredObject.put("url", "https://s.weibo.com/weibo?q=%23" + URLUtil.encode(note) + "%23");
            WeiboHotSearchResponse.WeiboHot weiboHot = filteredObject.toJavaObject(WeiboHotSearchResponse.WeiboHot.class);
            weiboHotList.add(weiboHot);
        }
        WeiboHotSearchResponse weiboHotSearchResponse = new WeiboHotSearchResponse();
        weiboHotSearchResponse.setWeibohotSearch(weiboHotList);
        return weiboHotSearchResponse;
    }

    /**
     * 获取星座运势
     *
     * @return 返回星座运势
     */
    @PostMapping("/horoscope")
    public HoroscopeResponse getHoroscope(@RequestBody HoroscopeParams horoscope) {
        String type = horoscope.getType();
        String time = horoscope.getTime();
        String responseJson = RequestUtils.get("https://api.vvhan.com/api/horoscope?type=" + type + "&time=" + time);
        // 2. 处理返回结果
        JSONObject data = com.alibaba.fastjson.JSONObject.parseObject(responseJson).getJSONObject("data");
        HoroscopeResponse horoscopeResponse = data.toJavaObject(HoroscopeResponse.class);
        return horoscopeResponse;
    }

    /**
     * 获取随机壁纸
     *
     * @return
     */
    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse getRandomWallpaper() {
        String wallpaperJson = RequestUtils.get("https://btstu.cn/sjbz/api.php?lx=dongman&format=json");
        return com.alibaba.fastjson.JSONObject.parseObject(wallpaperJson).toJavaObject(RandomWallpaperResponse.class);
    }

    /**
     * 获取网站图标
     *
     * @return
     */
    @GetMapping("/webFaviconIcon")
    public String getWebFaviconIcon(WebFaviconIconParams webFaviconIcon) {
        String url = webFaviconIcon.getUrl();
        // todo 响应为图片处理
        return RequestUtils.get("https://btstu.cn/getfav/api.php?url=" + url);
    }

    /**
     * 土味情话
     *
     * @return
     */
    @GetMapping("/loveTalk")
    public LoveTalkResponse getLoveTalk() {
        String loveTalk = RequestUtils.get("https://api.vvhan.com/api/love");
        LoveTalkResponse loveTalkResponse = new LoveTalkResponse();
        loveTalkResponse.setText(loveTalk);
        return loveTalkResponse;
    }

    /**
     * 天气信息
     *
     * @return
     */
    @GetMapping("/weather")
    public WeatherResponse getWeather() {
        String weatherJson = RequestUtils.get("https://api.vvhan.com/api/weather");
        return JSON.parseObject(weatherJson, WeatherResponse.class);
    }
}
