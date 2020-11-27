package com.azavyalov.nytimes.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.arellomobile.mvp.MvpActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.azavyalov.nytimes.R;
import com.google.android.material.snackbar.Snackbar;

import static com.azavyalov.nytimes.ui.about.SocialType.FACEBOOK;
import static com.azavyalov.nytimes.ui.about.SocialType.GITHUB;
import static com.azavyalov.nytimes.ui.about.SocialType.INSTAGRAM;
import static com.azavyalov.nytimes.ui.about.SocialType.TELEGRAM;

public class AboutActivityMvp extends MvpActivity implements AboutView {

    private RelativeLayout mainLayout;
    private EditText editText;
    private Button sendMessageButton;
    private ImageButton telegramIcon;
    private ImageButton instagramIcon;
    private ImageButton facebookIcon;
    private ImageButton githubIcon;

    @InjectPresenter
    AboutPresenter aboutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViews();
        setupSocialIcons();
        sendMessageButton.setOnClickListener(view -> aboutPresenter.sendMessage(
                editText.getText().toString()));
    }

    @Override
    public void openEmailApp(@NonNull String message, @NonNull Integer email) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(String.format("mailto:%s", getString(email))));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            aboutPresenter.onEmptyEmailError();
        }
    }

    @Override
    public void showEmptyMessageError(@NonNull Integer errorMessage) {
        Snackbar.make(mainLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyEmailClientError(@NonNull Integer errorMessage) {
        Snackbar.make(mainLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void openUrl(@NonNull Integer url) {
        Uri uri = Uri.parse(getString(url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            aboutPresenter.onUrlError();
        }
    }

    @Override
    public void showUrlClientError(@NonNull Integer errorMessage) {
        Snackbar.make(mainLayout, R.string.about_send_open_app_error,
                Snackbar.LENGTH_SHORT).show();
    }

    private void findViews() {
        mainLayout = findViewById(R.id.about_main_layout);
        editText = findViewById(R.id.text_input);
        sendMessageButton = findViewById(R.id.send_email_button);
        telegramIcon = findViewById(R.id.telegram_icon);
        instagramIcon = findViewById(R.id.instagram_icon);
        facebookIcon = findViewById(R.id.facebook_icon);
        githubIcon = findViewById(R.id.github_icon);
    }

    private void setupSocialIcons() {
        telegramIcon.setOnClickListener(view -> aboutPresenter.onOpenUrl(TELEGRAM));
        instagramIcon.setOnClickListener(view -> aboutPresenter.onOpenUrl(INSTAGRAM));
        facebookIcon.setOnClickListener(view -> aboutPresenter.onOpenUrl(FACEBOOK));
        githubIcon.setOnClickListener(view -> aboutPresenter.onOpenUrl(GITHUB));
    }
}
