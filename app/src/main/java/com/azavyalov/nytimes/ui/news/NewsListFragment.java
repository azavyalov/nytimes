package com.azavyalov.nytimes.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.network.RestApi;
import com.azavyalov.nytimes.room.ConverterDbToNewsItem;
import com.azavyalov.nytimes.room.ConverterDtoToDb;
import com.azavyalov.nytimes.room.NewsItemRepository;
import com.azavyalov.nytimes.ui.FragmentActionListener;
import com.azavyalov.nytimes.ui.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.azavyalov.nytimes.network.NewsCategories.HOME;
import static com.azavyalov.nytimes.ui.news.State.HAS_DATA;
import static com.azavyalov.nytimes.ui.news.State.HAS_NO_DATA;
import static com.azavyalov.nytimes.ui.news.State.LOADING;
import static com.azavyalov.nytimes.util.Util.disposeSafe;
import static com.azavyalov.nytimes.util.Util.setVisibility;

public class NewsListFragment extends Fragment {

    @Nullable
    private RecyclerView recycler;
    @Nullable
    private NewsAdapter adapter;
    @Nullable
    private ProgressBar progress;
    @Nullable
    private View error;
    @Nullable
    private Button errorAction;
    @Nullable
    private FloatingActionButton updateAction;

    private NewsItemRepository newsItemRepository;
    private CompositeDisposable compositeDisposable;

    private FragmentActionListener fragmentActionListener;
    private final NewsAdapter.OnItemClickListener clickListener =
            new NewsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull NewsItem newsItem) {
                    if (fragmentActionListener != null) {
                        fragmentActionListener.onNewsSelected(newsItem);
                    }
                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NewsListTag", "Call onCreate");
        compositeDisposable = new CompositeDisposable();
        newsItemRepository = new NewsItemRepository(getActivity());
        storeNewsFromApiToDb();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("NewsListTag", "Call onCreateView");
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);

        findViews(view);
        setupRecycler(view);
        setupRetryButton();
        setupUpdateButton();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("NewsListTag", "Call onResume");
        subscribeToNewsFromDb();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("NewsListTag", "Call onStop");
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        Log.d("NewsListTag", "Call onDestroy");
        super.onDestroy();
        disposeSafe(compositeDisposable);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentActionListener = (MainActivity) getActivity();
    }

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    private void findViews(View view) {
        progress = view.findViewById(R.id.progress);
        recycler = view.findViewById(R.id.news_list_recycler);
        error = view.findViewById(R.id.error_container);
        errorAction = view.findViewById(R.id.error_action_button);
        updateAction = view.findViewById(R.id.floating_action_button);
    }

    private void setupRecycler(View view) {
        adapter = new NewsAdapter(view.getContext(), clickListener);
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(new NewsItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.spacing_micro)));
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupRetryButton() {
        errorAction.setOnClickListener(view -> subscribeToNewsFromDb());
    }

    private void setupUpdateButton() {
        updateAction.setOnClickListener(view -> storeNewsFromApiToDb());
    }

    private void storeNewsFromApiToDb() {
        showState(LOADING);
        Log.d("NewsListTag", "Store news from API to DB");
        Disposable disposable = RestApi.getInstance()
                .getNewsService()
                .searchNews(HOME.toString())
                .map(response -> ConverterDtoToDb.map(response.getNews()))
                .flatMapCompletable(newsEntities -> newsItemRepository.saveNewsToDb(newsEntities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> showState(HAS_DATA), throwable -> showState(HAS_NO_DATA));
        compositeDisposable.add(disposable);
    }

    private void subscribeToNewsFromDb() {
        showState(LOADING);
        Log.d("NewsListTag", "Subscribe to news from DB");
        Disposable disposable = newsItemRepository
                .getNewsFromDb()
                .map(newsEntities -> ConverterDbToNewsItem.map(newsEntities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsItems -> updateItems(newsItems),
                        throwable -> Log.d("Room", throwable.toString()));
        compositeDisposable.add(disposable);
    }

    private void updateItems(List<NewsItem> news) {
        if (news == null || news.isEmpty()) {
            showState(HAS_NO_DATA);
            return;
        }
        if (adapter != null) {
            adapter.replaceItems(news);
        }
        showState(HAS_DATA);
    }

    public void showState(@NonNull State state) {

        switch (state) {
            case HAS_DATA:
                setVisibility(recycler, true);
                setVisibility(progress, false);
                setVisibility(error, false);
                setVisibility(updateAction, true);
                break;
            case LOADING:
                setVisibility(progress, true);
                setVisibility(recycler, false);
                setVisibility(error, false);
                setVisibility(updateAction, false);
                break;
            case HAS_NO_DATA:
                setVisibility(error, true);
                setVisibility(recycler, false);
                setVisibility(progress, false);
                setVisibility(updateAction, true);
                break;
            default:
                throw new IllegalArgumentException("Unexpected state: " + state);
        }
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
}
