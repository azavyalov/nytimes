package com.azavyalov.nytimes.ui.about;

import androidx.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface AboutView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
    void openEmailApp(@NonNull String message, @NonNull Integer email);

    @StateStrategyType(SkipStrategy.class)
    void showEmptyMessageError(@NonNull Integer errorMessage);

    void showEmptyEmailClientError(@NonNull Integer errorMessage);

    @StateStrategyType(SkipStrategy.class)
    void openUrl(@NonNull Integer url);

    void showUrlClientError(@NonNull Integer errorMessage);
}
