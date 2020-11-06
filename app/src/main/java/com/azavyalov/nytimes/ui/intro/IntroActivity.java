package com.azavyalov.nytimes.ui.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.ui.news.NewsListActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class IntroActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final String SHARED_PREF = "nytimes";
    private static final String SHARED_PREF_INTRO = "intro";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showIntro();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Destroy the activity to prevent showing intro after tap on return button
        finish();
    }

    private void showIntro() {
        if (needToShowIntro()) {
            setContentView(R.layout.activity_intro);
            Disposable disposable = Completable.complete()
                    .delay(5, TimeUnit.SECONDS)
                    .subscribe(this::startNewsListActivity);
            compositeDisposable.add(disposable);

            getSharedPreferences("nytimes", MODE_PRIVATE)
                    .edit()
                    .putBoolean("intro", false)
                    .apply();
        } else {
            startNewsListActivity();
        }
    }

    private boolean needToShowIntro() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        return prefs.getBoolean(SHARED_PREF_INTRO, true);
    }

    private void startNewsListActivity() {
        startActivity(new Intent(this, NewsListActivity.class));
    }

}
