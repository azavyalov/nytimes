package com.azavyalov.nytimes.ui.about;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.azavyalov.nytimes.R;

@InjectViewState
public class AboutPresenter extends MvpPresenter<AboutView> {

    public void sendMessage(String message) {
        if (message != null && !message.isEmpty()) {
            getViewState().openEmailApp(message, R.string.email_address);
        } else {
            getViewState().showEmptyMessageError(R.string.email_error);
        }
    }

    public void onEmptyEmailError() {
        getViewState().showEmptyEmailClientError(R.string.about_send_email_no_clients_error);
    }

    public void onOpenUrl(SocialType type) {
        switch (type) {
            case TELEGRAM:
                getViewState().openUrl(R.string.telegram_url);
                break;
            case INSTAGRAM:
                getViewState().openUrl(R.string.instagram_url);
                break;
            case FACEBOOK:
                getViewState().openUrl(R.string.facebook_url);
                break;
            case GITHUB:
                getViewState().openUrl(R.string.github_url);
                break;
            default:
                throw new IllegalArgumentException("Unexpected social type: " + type);
        }
    }

    public void onUrlError() {
        getViewState().showUrlClientError(R.string.about_send_open_app_error);
    }
}
