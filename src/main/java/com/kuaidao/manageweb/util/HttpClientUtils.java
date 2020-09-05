package com.kuaidao.manageweb.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpClientUtils {


    private static RequestConfig requestConfig = null;
    private static String charset = "utf-8";


    static {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
    }


    /**
     * @param url
     * @param obj 1. json字符串   2. map  3.JSONObject
     * @return JSONObject
     */
    public static JSONObject httpPost(String url, Object obj) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        // 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        try {
            if (null != obj) {
                StringEntity entity = null;
                if (obj instanceof String) {
                    entity = new StringEntity(obj.toString(), charset);
                } else {
                    entity = new StringEntity(JSON.toJSONString(obj), charset);
                }
                entity.setContentEncoding(charset);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            log.debug(" {} - {} ", url, obj);
            CloseableHttpResponse response = httpClient.execute(httpPost);

            return convertResponse(response);
        } catch (Exception e) {
            log.error("error HttpClientUtils {} - {} - {}" + url, obj, e);
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }


    /**
     * post请求传输String参数 例如：name=Jack&sex=1&type=2
     * Content-type:application/x-www-form-urlencoded
     *
     * @param url url地址
     * @param
     * @return
     */
    public static JSONObject httpPostForm(String url, Map<String, String> params) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        try {
            if (null != params) {
                //组织请求参数
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                if (params != null && params.size() > 0) {
                    Set<String> keySet = params.keySet();
                    for (String key : keySet) {
                        paramList.add(new BasicNameValuePair(key, params.get(key)));
                    }
                }

                httpPost.setEntity(new UrlEncodedFormEntity(paramList, charset));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return convertResponse(response);
        } catch (IOException e) {
            log.error("post请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static JSONObject httpGet(String url) {
        // get请求返回结果
        JSONObject jsonResult = null;
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);

            jsonResult = convertResponse(response);
        } catch (Exception e) {
            log.error("get请求提交失败:" + url, e);
        } finally {
            request.releaseConnection();
        }
        return jsonResult;
    }

    private static JSONObject convertResponse(CloseableHttpResponse response) throws IOException{
        // 请求发送成功，并得到响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 读取服务器返回过来的json字符串数据
            HttpEntity entity = response.getEntity();
            String strResult = EntityUtils.toString(entity, "utf-8");
            // 把json字符串转换成json对象
            return JSONObject.parseObject(strResult);
        } else {
            log.error(" {} ", response);
        }

        return null;
    }

}
