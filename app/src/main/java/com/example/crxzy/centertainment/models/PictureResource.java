package com.example.crxzy.centertainment.models;

import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureResource extends CardBox.ResourceManager.ResourceBase {
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
    public String Language;
    public Integer clickedTimes;
    public String Source;
    public int PageCount;

    public PictureResource(JSONObject resource) {
        super ( );
        Resource = resource;
        initiation ( );
    }

    public PictureResource(String jsonString) {
        super ( );
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
            Source = (String) getJsonValue (Resource, "source", "");
            ResourceId = Resource.getJSONObject ("_id").getString ("$oid");
            Thumb = Resource.getJSONObject ("thumb");
            Title = Resource.getString ("name");
            PageCount = Integer.parseInt (Resource.getString ("page_count"));
            ThumbId = Thumb.getString ("thumb_id");
            ThumbStatus = Thumb.getInt ("status");
            if (Resource.has ("clicked")) {
                clickedTimes = Resource.getInt ("clicked");
            } else {
                clickedTimes = 0;
            }
            if (Resource.has ("languages")) {
                JSONArray languages = Resource.getJSONArray ("languages");
                for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                    Language = (String) languages.get (index_2);
                    if (!Language.equals ("translated")) {
                        break;
                    }
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
            if (Resource.has ("artists")) {
                JSONArray artists = Resource.getJSONArray ("artists");
                for (int index = 0; index < artists.length ( ); index++) {
                    Artists.add ((String) artists.get (index));
                }
            }
            JSONArray tags = Resource.getJSONArray ("tags");
            for (int index = 0; index < tags.length ( ); index++) {
                Tags.add ((String) tags.get (index));
            }
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    private Object getJsonValue(JSONObject object, String key, Object defValue) throws JSONException {
        if (object.has (key)) {
            if (defValue instanceof String) {
                return object.getString (key);
            }
        }
        return defValue;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getSpanCount() {
        return 1;
    }
}
