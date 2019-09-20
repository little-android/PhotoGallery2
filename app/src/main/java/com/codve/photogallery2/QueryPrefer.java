package com.codve.photogallery2;

import android.content.Context;

public class QueryPrefer {

    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_ID = "LastResultId";

    // 存储与取出搜索历史
    public static String getStoredQuery(Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(PREF_SEARCH_QUERY, null);
        return context.getSharedPreferences(PREF_SEARCH_QUERY, Context.MODE_PRIVATE)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit()
//                .putString(PREF_SEARCH_QUERY, query)
//                .apply();
        context.getSharedPreferences(PREF_SEARCH_QUERY, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getLastResultId(Context context) {
        return context.getSharedPreferences(PREF_SEARCH_QUERY, Context.MODE_PRIVATE)
                .getString(PREF_LAST_ID, null);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        context.getSharedPreferences(PREF_SEARCH_QUERY, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_LAST_ID, lastResultId)
                .apply();
    }
}
