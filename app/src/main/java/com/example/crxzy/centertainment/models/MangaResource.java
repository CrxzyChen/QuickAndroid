package com.example.crxzy.centertainment.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MangaResource {
    public JSONArray Recommend;
    public int ThumbStatus;
    public JSONObject Resource;
    public JSONObject Thumb;
    public List <String> ImageNames;
    public List <String> Artists;
    public List <String> Tags;
    public String ThumbId;
    public String Title;
    public String ResourceId;
    public JSONObject Info;
    public String Language;

    public MangaResource(JSONObject resource) {
        Resource = resource;
        initiation ( );
    }

    public MangaResource(String jsonString) {
        try {
            Resource = new JSONObject (jsonString);
            initiation ( );
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    private void initiation() {
        try {
            Recommend = new JSONArray ( );
            ImageNames = new ArrayList <> ( );
            Artists = new ArrayList <> ( );
            Tags = new ArrayList <> ( );
            ResourceId = Resource.getJSONObject ("_id").getString ("$oid");
            Thumb = Resource.getJSONObject ("thumb");
            Info = Resource.getJSONObject ("info");
            Title = !Info.getString ("original_name").equals ("null") ? Info.getString ("original_name") : Info.getString ("name");
            ThumbId = Thumb.getString ("thumb_id");
            ThumbStatus = Thumb.getInt ("status");
            JSONArray languages = Info.getJSONArray ("languages");
            for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                Language = (String) languages.get (index_2);
                if (!Language.equals ("translated")) {
                    break;
                }
            }
            JSONArray recommends;
            if (Resource.has ("recommend")) {
                recommends = Resource.getJSONArray ("recommend");
            } else {
                recommends = new JSONArray ( );
            }
            for (int index = 0; index < recommends.length ( ); index++) {
                JSONArray recommend = recommends.getJSONArray (index);
                Recommend.put (recommend.getJSONObject (0).getString ("$oid"));
            }
            JSONArray imageNames = Thumb.getJSONArray ("image_names");
            for (int index = 0; index < imageNames.length ( ); index++) {
                ImageNames.add ((String) imageNames.get (index));
            }
            JSONArray artists = Info.getJSONArray ("artists");
            for (int index = 0; index < artists.length ( ); index++) {
                Artists.add ((String) artists.get (index));
            }
            JSONArray tags = Info.getJSONArray ("tags");
            for (int index = 0; index < tags.length ( ); index++) {
                Tags.add ((String) tags.get (index));
            }
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }


}
