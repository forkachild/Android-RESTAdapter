package com.suhel.rest;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class RESTAdapter extends RecyclerView.Adapter<RESTAdapter.RESTViewHolder> implements Response.Listener<JSONArray> {

    private enum ViewType {
        TEXT,
        IMAGE;

        static ViewType fromString(String type) {
            if (type.equalsIgnoreCase("text"))
                return TEXT;
            else if (type.equalsIgnoreCase("image"))
                return IMAGE;
            else
                return TEXT;
        }
    }

    private class ViewWithType {
        View view;
        ViewType type;

        ViewWithType(View view, ViewType type) {
            this.view = view;
            this.type = type;
        }
    }

    class RESTViewHolder extends RecyclerView.ViewHolder {

        private Map<String, ViewWithType> layoutMap = new HashMap<>();
        private Context mContext;

        RESTViewHolder(View itemView) {
            super(itemView);
        }

        RESTViewHolder(Context context, View itemView, Map<String, String> viewMap,
                       Map<String, String> dataTypeMap) {
            this(itemView);
            mContext = context;
            for (String field : viewMap.keySet()) {
                int id = context.getResources().getIdentifier(viewMap.get(field), "id", context.getPackageName());
                View view = itemView.findViewById(id);
                ViewType viewType = ViewType.fromString(dataTypeMap.get(field));
                layoutMap.put(field, new ViewWithType(view, viewType));
            }
        }

        void bind(JSONObject json) throws JSONException {
            for (String field : layoutMap.keySet()) {
                String value = json.getString(field);
                ViewWithType viewWithType = layoutMap.get(field);
                switch (viewWithType.type) {
                    case IMAGE:
                        bindImage(viewWithType.view, value);
                        break;

                    case TEXT:
                        bindText(viewWithType.view, value);
                }
            }
        }

        private void bindText(View view, String data) {
            if (view instanceof TextView)
                ((TextView) view).setText(data);
        }

        private void bindImage(View view, String url) {
            if (view instanceof ImageView)
                Glide.with(mContext).load(Uri.parse(url)).into((ImageView) view);
        }

    }

    private Context mContext;
    private int mJsonFileID;
    private String mProjection;

    private RequestQueue mRequestQueue;

    private String mBaseURL = "";
    private String mPath = "";
    private int mViewId = 0;
    private Map<String, String> mViewMap = new HashMap<>();
    private Map<String, String> mDataTypeMap = new HashMap<>();

    private JSONArray mDataSource;

    private RESTAdapter(Context context, int jsonFileID, String projection) {

        mContext = context;
        mJsonFileID = jsonFileID;
        mProjection = projection;
        mRequestQueue = Volley.newRequestQueue(mContext);

        try {
            JSONObject root = new JSONObject(readFromJsonRes(mJsonFileID));
            mBaseURL = root.getString("baseURL");
            JSONObject projectionObject = root.getJSONObject("projections").getJSONObject(mProjection);
            mPath = projectionObject.getString("path");
            mViewId = mContext.getResources().getIdentifier(projectionObject.getString("viewId"),
                    "layout", mContext.getPackageName());
            jsonToMap(mViewMap, projectionObject.getJSONObject("viewMap"));
            jsonToMap(mDataTypeMap, projectionObject.getJSONObject("dataTypeMap"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONArray response) {
        mDataSource = response;
        notifyDataSetChanged();
    }

    @Override
    public RESTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mViewId, null);
        return new RESTViewHolder(mContext, view, mViewMap, mDataTypeMap);
    }

    @Override
    public void onBindViewHolder(RESTViewHolder holder, int position) {
        try {
            holder.bind(mDataSource.getJSONObject(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSource == null)
            return 0;
        else
            return mDataSource.length();
    }

    public void fetch() {
        JsonArrayRequest request =
                new JsonArrayRequest(Request.Method.GET, getURL(), null, this, null);
        mRequestQueue.add(request);
    }

    private String getURL() {
        return mBaseURL + mPath;
    }

    private void jsonToMap(Map<String, String> map, JSONObject json) throws JSONException {
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            map.put(key, json.getString(key));
        }
    }

    private String readFromJsonRes(int resId) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(mContext.getResources().openRawResource(resId)));
        try {
            String line = br.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
                line = br.readLine();
            }
        } catch (Exception ignored) {

        }
        return stringBuilder.toString();
    }

    static class Builder {

        private Context context;
        private int jsonID = 0;
        private String projection = "";

        public Builder withContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder fromJSON(int jsonID) {
            this.jsonID = jsonID;
            return this;
        }

        public Builder loadProjection(String projection) {
            this.projection = projection;
            return this;
        }

        public RESTAdapter build() {
            return new RESTAdapter(context, jsonID, projection);
        }

    }

}
