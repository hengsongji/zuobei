package com.travis.zuobei;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.travis.zuobei.widget.ReboundScrollView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ReboundScrollView.BounceListener {
    private ImageView iv;
    private ReboundScrollView scrollView;
    private View placeholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        scrollView = (ReboundScrollView) findViewById(R.id.scrollView);
        placeholder = findViewById(R.id.placeholder);
        scrollView.setBounceListener(this);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = width * 9 / 16;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv.getLayoutParams();
        params.width = width;
        params.height = height;
        iv.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) placeholder.getLayoutParams();
        params1.width = width;
        params1.height = height;
        placeholder.setLayoutParams(params1);

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("Hello World!");
                e.onComplete();
            }
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.d("travis", "s=" + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        observable.subscribe(observer);


        //
        Observable.just("WHAT")
                .subscribe(s -> Log.d("travis", s));

        Observable.just("Hello,World!")
                .map(s -> s + " -Dan")
                .subscribe(s -> Log.d("travis", s));

        Observable.just("Hello,World!")
                .map(s -> s.hashCode())
                .subscribe(i -> Log.d("travis", "i=" + i));

        Observable.fromArray("url1", "url2", "url3")
                .subscribe(s -> Log.d("travis", s));

        List<String> datas = new ArrayList<>();
        datas.add("action1");
        datas.add("action2");
        datas.add("action3");
        Observable.fromArray(datas)
                .subscribeOn(Schedulers.io())
                .flatMap(actions -> {
                    Log.d("travis","flatMap::thread id="+Thread.currentThread().getId());
                    return Observable.fromIterable(actions);
                })
                .filter(action -> {
                    Log.d("travis","filter::thread id="+Thread.currentThread().getId());
                    return !"action2".equals(action);
                })
                .doOnNext(action -> {
                    Log.d("travis","doOnNext::thread id="+Thread.currentThread().getId());
                    System.out.println(action);
                })//没有起作用
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {}, e -> e.printStackTrace(),() -> {});
//                .subscribe(s -> {
//                    Log.d("travis","subscribe::thread id="+Thread.currentThread().getId());
//                    Log.d("travis",s);
//                });
    }



    @Override
    public void onOffsetChanged(int offset) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv.getLayoutParams();
        params.topMargin = offset;
        iv.setLayoutParams(params);
    }
}
