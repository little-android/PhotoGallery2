package com.codve.photogallery2.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class NullStringToEmptyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class rawType = (Class) type.getRawType();
        if (rawType != String.class) {
            return null;
        }
        return (TypeAdapter) new StringNullAdapter();
    }
}
