package com.globalclasses;

import org.json.JSONException;

/**
 * Created by Singsys-043 on 10/26/2015.
 */
public interface CallBackListenar {
    public void callBackFunction(String result, String action) throws JSONException;
}
