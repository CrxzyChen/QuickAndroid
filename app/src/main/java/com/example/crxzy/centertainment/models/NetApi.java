package com.example.crxzy.centertainment.models;

import android.util.ArrayMap;

import com.example.crxzy.centertainment.activities.ArtistActivity;
import com.example.crxzy.centertainment.activities.PictureActivity;
import com.example.crxzy.centertainment.activities.SearchActivity;
import com.example.crxzy.centertainment.controllers.Subscribe;
import com.example.crxzy.centertainment.controllers.main.picture.Latest;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.tools.Network;

import java.util.Map;

public class NetApi {
    private static Network mNetwork = new Network ( );
    private final static String mHost = "http://10.0.0.2/CEntertainment/";

    static public void getLatest(int uid, int limit, int skip, Latest context, String success) {
        Network.Request request = new Network.Request (mHost + "Manga/Latest.json?uid=" + uid + "&limit=" + limit + "&skip=" + skip);
        request.setSuccess (context, success);
        mNetwork.send (request);
    }

    public static void addHistory(int uid, String resource_id) {
        Network.Request historyRequest = new Network.Request (mHost + "User/addHistory.json?uid=" + uid + "&resource_id=" + resource_id);
        mNetwork.send (historyRequest);
    }

    public static void addLike(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request likeRequest = new Network.Request (mHost + "User/addLike.json?uid=" + uid + "&resource_id=" + resource_id);
        likeRequest.setSuccess (context, success);
        mNetwork.send (likeRequest);
    }

    public static void removeLike(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request removeLikeRequest = new Network.Request (mHost + "User/removeLike.json?uid=" + uid + "&resource_id=" + resource_id);
        removeLikeRequest.setSuccess (context, success);
        mNetwork.send (removeLikeRequest);
    }

    public static void isLike(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request isLikeRequest = new Network.Request (mHost + "User/isLike.json?uid=" + uid + "&resource_id=" + resource_id);
        isLikeRequest.setSuccess (context, success);
        mNetwork.send (isLikeRequest);
    }

    public static void upClickedCount(String resource_id) {
        Network.Request clickedRequest = new Network.Request (mHost + "Manga/upClickedCount.json?resource_id=" + resource_id);
        mNetwork.send (clickedRequest);
    }

    public static void addSubscribe(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request addSubscribeRequest = new Network.Request (mHost + "User/addSubscribe.json?uid=" + uid + "&resource_id=" + resource_id);
        addSubscribeRequest.setSuccess (context, success);
        mNetwork.send (addSubscribeRequest);
    }

    public static void removeSubscribe(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request removeLikeRequest = new Network.Request (mHost + "User/removeSubscribe.json?uid=" + uid + "&resource_id=" + resource_id);
        removeLikeRequest.setSuccess (context, success);
        mNetwork.send (removeLikeRequest);
    }

    public static void isSubscribe(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request isLikeRequest = new Network.Request (mHost + "User/isSubscribe.json?uid=" + uid + "&resource_id=" + resource_id);
        isLikeRequest.setSuccess (context, success);
        mNetwork.send (isLikeRequest);
    }

    public static void getSubscribe(int uid, Subscribe context, String success, int limit, int skip) {
        Network.Request getSubscribeRequest = new Network.Request (mHost + "User/getSubscribe.json?uid=" + uid + "&limit=" + limit + "&skip=" + skip);
        getSubscribeRequest.setSuccess (context, success);
        mNetwork.send (getSubscribeRequest);
    }

    public static void getResourceByIds(String resource_ids, Object context, String success) {
        Network.Request getSubscribeRequest = new Network.Request (mHost + "Manga/getResourcesByIds.json?resource_ids=" + resource_ids);
        getSubscribeRequest.setSuccess (context, success);
        mNetwork.send (getSubscribeRequest);
    }

    public static void getArtistOpus(String artist, Object context, String success, int limit, int skip) {
        Network.Request getArtistOpusRequest = new Network.Request (mHost + "Manga/getArtistOpus.json?artist=" + artist + "&limit=" + limit + "&skip=" + skip);
        getArtistOpusRequest.setSuccess (context, success);
        mNetwork.send (getArtistOpusRequest);
    }

    public static void search(String searchContent, Object context, String success, int limit, int skip) {
        Network.Request searchRequest = new Network.Request (mHost + "Manga/search.json?search_content=" + searchContent + "&limit=" + limit + "&skip=" + skip);
        searchRequest.setSuccess (context, success);
        mNetwork.send (searchRequest);
    }

    public static void getAllLabels(Object context, String success) {
        Network.Request getAllLabelsRequest = new Network.Request (mHost + "Manga/getAllLabels.json");
        getAllLabelsRequest.setSuccess (context, success);
        mNetwork.send (getAllLabelsRequest);
    }

    public static void getUserConfig(int uid, Object context, String success) {
        Network.Request getUserConfigRequest = new Network.Request (mHost + "User/getUserConfig.json?uid=" + uid);
        getUserConfigRequest.setSuccess (context, success);
        mNetwork.send (getUserConfigRequest);
    }

    public static void setUserConfig(int uid, String key, String value) {
        Network.Request setUserConfigRequest = new Network.Request (mHost + "User/setUserConfig.json?uid=" + uid + "&key=" + key + "&value=" + value);
        mNetwork.send (setUserConfigRequest);
    }

    public static void getUserDefine(int uid, int limit, Object context, String success) {
        Network.Request getUserDefineRequest = new Network.Request (mHost + "Manga/getUserDefine.json?uid=" + uid + "&limit=" + limit);
        getUserDefineRequest.setSuccess (context, success);
        mNetwork.send (getUserDefineRequest);
    }

    public static void isFocusArtist(int uid, String artistName, Network.Callback callback) {
        Network.Request request = new Network.Request (mHost + "User/isFocusArtist.json?uid=" + uid + "&artist_name=" + artistName);
        request.setCallback (callback);
        mNetwork.send (request);
    }

    public static void addFocusArtist(int uid, String artistName, Network.Callback callback) {
        Network.Request request = new Network.Request (mHost + "User/addFocusArtist.json?uid=" + uid + "&artist_name=" + artistName);
        request.setCallback (callback);
        mNetwork.send (request);
    }

    public static void removeFocusArtist(int uid, String artistName, Network.Callback callback) {
        Network.Request request = new Network.Request (mHost + "User/removeFocusArtist.json?uid=" + uid + "&artist_name=" + artistName);
        request.setCallback (callback);
        mNetwork.send (request);
    }

    public static void getUserFocus(int uid, int limit, int skip, Network.Callback callback) {
        Network.Request request = new Network.Request (mHost + "Manga/getUserFocus.json?uid=" + uid + "&limit=" + limit + "&skip=" + skip);
        request.setCallback (callback);
        mNetwork.send (request);
    }
}
