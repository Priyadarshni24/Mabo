package com.advengers.mabo.Cometchat.Base;

public interface BasePresenter<V> {

    void attach(V baseView);

    void detach();


}
