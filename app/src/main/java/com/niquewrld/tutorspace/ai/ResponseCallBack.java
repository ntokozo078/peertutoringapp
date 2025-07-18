package com.niquewrld.tutorspace.ai;

public interface ResponseCallBack {
    void onResponse(String response);
    void onError(Throwable throwable);
}