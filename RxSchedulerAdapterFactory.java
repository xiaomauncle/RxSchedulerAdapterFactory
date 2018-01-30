import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * @author xiaomauncle
 * @desc Network IO Scheduler Controller
 * @date 2018/1/30 下午9:37.
 */

public class RxSchedulerAdapterFactory extends CallAdapter.Factory{

    public static RxSchedulerAdapterFactory create() {
        return new RxSchedulerAdapterFactory();
    }

    @Override
    public CallAdapter<Object, Object> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        if (getRawType(returnType) != Observable.class) {
            return null; // Ignore non-Observable types.
        }

        // Look up the next call adapter which would otherwise be used if this one was not present.
        //noinspection unchecked returnType checked above to be Observable.
        final CallAdapter<Object, Observable<Object>> delegate = (CallAdapter<Object, Observable<Object>>) retrofit.nextCallAdapter(this, returnType, annotations);

        return new CallAdapter<Object, Object>() {

            @Override
            public Type responseType() {
                return delegate.responseType();
            }

            @Override
            public Object adapt(Call<Object> call) {
                Observable<Object> observable = delegate.adapt(call);
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

            }
        };
    }

}