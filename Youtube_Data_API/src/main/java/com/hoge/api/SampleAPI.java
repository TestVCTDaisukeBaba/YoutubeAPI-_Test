package com.hoge.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.SearchListResponse;
import com.hoge.model.VideoBean;

public class SampleAPI {
	/** Global instance of YouTube object to make all API requests. */
	private static YouTube youtube;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String PROPERTIES_FILENAME = "youtube.properties";
    
    /** HTTPリクエストの初期化クラス */
    private static final HttpRequestInitializer HTTP_REQUEST_INITIALIZER = new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {}
    };
    
    // 検索したい動画情報の取得
    public List<VideoBean> getSearchList() throws Exception {

        List<VideoBean> list = new ArrayList<>();
        
        try {
			// youtubeオブジェクト生成
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HTTP_REQUEST_INITIALIZER).setApplicationName("youtube-web2023").build();
			  
			// apikeyをセットする
			Properties properties = new Properties();
   
			// プロパティファイルに定義したAPIキーを設定する
	        InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
	        properties.load(in);
            String apiKey = properties.getProperty("youtube.apikey");
            
            // 検索結果のbean
        	SearchListResponse res = new SearchListResponse();
            YouTube.Search.List search = youtube.search().list("snippet")
            		.setQ("三毛猫") // 検索したいキーワードを入力する
            		.setKey(apiKey);
            
            // VideoBeanにデータを格納する
		    do {
		    	res = search
			    	.setMaxResults(50L) // 最大50件
			    	.setPageToken(res.getNextPageToken())
			    	.execute();
		    	
	            for (int i = 0; i < res.getItems().size(); i++) {
	            	VideoBean bean = new VideoBean();
	            	bean.setVideoTitle(res.getItems().get(i).getSnippet().getTitle()); // 動画タイトル
	            	bean.setThumbnail((res.getItems().get(i).getSnippet().getThumbnails().getDefault().getUrl())); // サムネイル画像)
	            	list.add(bean);
            }
		    } while (res.getNextPageToken() != null); // 51件以降のデータも格納する
		    
        } catch (GoogleJsonResponseException e) {
        	e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //登録しているチャンネルのidを返却する
		return list;
    }
}