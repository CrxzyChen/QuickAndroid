package com.example.crxzy.centertainment.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MangaArtistInfo {
    public List <MangaResource> opus = new ArrayList <> ( );

    public MangaArtistInfo(JSONObject resource) {
        try {
            JSONArray opus = resource.getJSONArray ("opus");
            for (int index = 0; index < opus.length ( ); index++) {
                this.opus.add (new MangaResource (opus.getJSONObject (index)));
            }
        } catch (JSONException e) {
            e.printStackTrace ( );
        }
    }
}
