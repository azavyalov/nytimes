package com.azavyalov.nytimes.ui.details;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.room.NewsEntity;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.azavyalov.nytimes.util.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsFragment extends Fragment {

    private static final String NEWS_ITEM_ID_EXTRA = "extra:newsId";
    private static final String LOG_TAG = "NewsDetailsTag";

    private ImageView imageView;
    private TextView titleView;
    private TextView dateView;
    private TextView textView;
    private Button webViewButton;
    private FloatingActionButton deleteButton;

    private Activity activity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NewsItemRepository newsItemRepository;
    private NewsEntity mNewsEntity;
    private int newsId;

    public static NewsDetailsFragment newInstance(int newsId) {
        NewsDetailsFragment detailsFragment = new NewsDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(NEWS_ITEM_ID_EXTRA, newsId);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        newsId = getArguments().getInt(NEWS_ITEM_ID_EXTRA);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Call onCreateView");
        View view = inflater.inflate(R.layout.news_details_fragment, container, false);

        activity = getActivity();
        newsItemRepository = new NewsItemRepository(activity);
        findViews(view);
        loadNewsItemFromDb(newsId);
        return view;
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "Call onStop");
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Call onDestroy");
        super.onDestroy();
        Util.disposeSafe(compositeDisposable);
    }

    private void findViews(View view) {
        imageView = view.findViewById(R.id.news_details_image);
        titleView = view.findViewById(R.id.news_details_title);
        dateView = view.findViewById(R.id.news_details_date);
        textView = view.findViewById(R.id.news_details_text);
        webViewButton = view.findViewById(R.id.open_in_web_view_button);
        deleteButton = view.findViewById(R.id.delete_news_button);
    }

    private void loadNewsItemFromDb(int id) {
        Log.d(LOG_TAG, "Load news item from DB");
        Disposable disposable = newsItemRepository
                .getNewsById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(newsEntity -> {
                    mNewsEntity = newsEntity;
                    return mNewsEntity;
                })
                .subscribe(NewsDetailsFragment.this::setupViews,
                        throwable -> Log.d("Room", throwable.toString()));
        compositeDisposable.add(disposable);
    }

    private void setupViews(NewsEntity newsEntity) {
        Glide.with(activity)
                .load(newsEntity.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        titleView.setText(newsEntity.getTitle());
        textView.setText(newsEntity.getPreviewText());
        dateView.setText(Util.formatDateString(newsEntity.getPublishDate()));
        webViewButton.setOnClickListener(view -> {
            openWebViewActivity(newsEntity.getTextUrl());
        });
        deleteButton.setOnClickListener(view -> {
            deleteNews(mNewsEntity);
        });
    }

    private void deleteNews(NewsEntity newsEntity) {
        if (newsEntity != null) {
            Disposable disposable = newsItemRepository
                    .deleteNews(newsEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> activity.getFragmentManager().popBackStack());
            compositeDisposable.add(disposable);
            activity.onBackPressed();
        }
    }

    private void openWebViewActivity(String newsUrl) {
        NewsDetailsWebViewActivity.start(activity, newsUrl);
    }
}
