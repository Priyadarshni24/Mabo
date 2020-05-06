package helper.Base;

public interface BasePresenter<V> {

    void attach(V baseView);

    void detach();


}
