package com.example.crxzy.centertainment.models;

import com.example.crxzy.centertainment.views.CardBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureResource extends CardBox.ResourceManager.ResourceBase {
    public JSONArray recommend;
    public int thumbStatus;
    public JSONObject resource;
    public JSONObject thumb;
    public List <String> imageNames;
    public List <String> artists;
    public List <String> tags;
    public String thumbId;
    public String title;
    public String resourceId;
    public String language;
    public Integer clickedTimes;
    public String source;
    public int pageCount;
    public String thumbPath;
    public String resourceKind;

    public PictureResource(JSONObject resource) {
        super ( );
        this.resource = resource;
        initiation ( );
    }

    public PictureResource(String jsonString) {
        super ( );
        try {
            resource = new JSONObject (jsonString);
            initiation ( );
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }

    private void initiation() {
        try {
            recommend = new JSONArray ( );
            imageNames = new ArrayList <> ( );
            artists = new ArrayList <> ( );
            tags = new ArrayList <> ( );
            source = (String) getJsonValue (resource, "source", "");
            resourceId = resource.getJSONObject ("_id").getString ("$oid");
            thumb = resource.getJSONObject ("thumb");
            title = resource.getString ("name");
            pageCount = Integer.parseInt (resource.getString ("page_count"));
            thumbId = thumb.getString ("thumb_id");
            thumbStatus = thumb.getInt ("status");
            thumbPath = thumb.getString ("thumb_path");
            resourceKind = thumbPath.split ("/")[0].toLowerCase ();
            if (resource.has ("clicked")) {
                clickedTimes = resource.getInt ("clicked");
            } else {
                clickedTimes = 0;
            }
            if (resource.has ("languages")) {
                JSONArray languages = resource.getJSONArray ("languages");
                for (int index_2 = 0; index_2 < languages.length ( ); index_2++) {
                    language = (String) languages.get (index_2);
                    if (!language.equals ("translated")) {
                        break;
                    }
                }
            }

            JSONArray recommends;
            if (resource.has ("recommend")) {
                recommends = resource.getJSONArray ("recommend");
            } else {
                recommends = new JSONArray ( );
            }
            for (int index = 0; index < recommends.length ( ); index++) {
                JSONArray recommend = recommends.getJSONArray (index);
                this.recommend.put (recommend.getJSONObject (0).getString ("$oid"));
            }
            JSONArray imageNames = thumb.getJSONArray ("image_names");
            for (int index = 0; index < imageNames.length ( ); index++) {
                this.imageNames.add ((String) imageNames.get (index));
            }
            if (resource.has ("artists")) {
                JSONArray artists = resource.getJSONArray ("artists");
                for (int index = 0; index < artists.length ( ); index++) {
                    this.artists.add ((String) artists.get (index));
                }
            }
            JSONArray tags = resource.getJSONArray ("tags");
            for (int index = 0; index < tags.length ( ); index++) {
                this.tags.add ((String) tags.get (index));
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
