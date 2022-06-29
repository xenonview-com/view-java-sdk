/**
 * Created by lwoydziak on 06/20/22.
 *
 * Api.java
 *
 * Interface for lazy instantiation of an API.
 *
 **/
package xenon.view.sdk.api;

public interface Api<T> {
    T instance(String _apiUrl);
}