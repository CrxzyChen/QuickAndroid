package com.example.crxzy.centertainment.models;

import com.example.crxzy.centertainment.PictureActivity;
import com.example.crxzy.centertainment.controllers.main.picture.Latest;
import com.example.crxzy.centertainment.tools.Network;

public class NetApi {
    private static Network mNetwork = new Network ( );
    private final static String mHost = "http://10.0.0.2/CEntertainment/";

    static public void getLatest(int limit, int skip, Latest context, String success) {
        Network.Request request = new Network.Request (mHost + "Manga/Latest.json?limit=" + limit + "&skip=" + skip);
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
        Network.Request likeRequest = new Network.Request (mHost + "User/removeLike.json?uid=" + uid + "&resource_id=" + resource_id);
        likeRequest.setSuccess (context, success);
        mNetwork.send (likeRequest);
    }

    public static void isLike(int uid, String resource_id, PictureActivity context, String success) {
        Network.Request likeRequest = new Network.Request (mHost + "User/isLike.json?uid=" + uid + "&resource_id=" + resource_id);
        likeRequest.setSuccess (context, success);
        mNetwork.send (likeRequest);
    }

    public static void upClickedCount(String resource_id) {
        Network.Request clickedRequest = new Network.Request ("http://10.0.0.2/CEntertainment/Manga/upClickedCount.json?resource_id=" + resource_id);
        mNetwork.send (clickedRequest);
    }
}
