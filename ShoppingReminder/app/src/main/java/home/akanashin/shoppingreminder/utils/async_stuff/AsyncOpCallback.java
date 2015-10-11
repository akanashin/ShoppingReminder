package home.akanashin.shoppingreminder.utils.async_stuff;

/**
 * Callback interface to be used with Asynchronous home.akanashin.shoppingreminder.operations
 * Created by akana_000 on 6/20/2015.
 */
public interface AsyncOpCallback<Params> {
    /*
     * This method should be executed when async op finished its job
     * In case of error Params will ne null
     */
    void doStuff(Params param);
}
