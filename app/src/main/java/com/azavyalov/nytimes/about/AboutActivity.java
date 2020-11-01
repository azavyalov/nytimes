package com.azavyalov.nytimes.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.azavyalov.nytimes.R;
import com.google.android.material.snackbar.Snackbar;

public class AboutActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
        setEmailSending();
        setLinkViews();
    }

    private void init() {
        mainLayout = findViewById(R.id.about_main_layout);
        getSupportActionBar().setTitle(R.string.about_title);
    }

    private void setEmailSending() {
        final EditText editText = findViewById(R.id.text_input);
        final Button sendEmailButton = findViewById(R.id.send_email_button);
        sendEmailButton.setOnClickListener(view -> openEmailApp(editText.getText().toString()));
    }

    private void openEmailApp(String message) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(String.format("mailto:%s", getString(R.string.email_address))));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(mainLayout, R.string.about_send_email_no_clients_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setLinkViews() {

        findViewById(R.id.telegram_icon).setOnClickListener(view
                -> openUrl(getResources().getString(R.string.telegram_url)));

        findViewById(R.id.instagram_icon).setOnClickListener(view
                -> openUrl(getResources().getString(R.string.instagram_url)));

        findViewById(R.id.facebook_icon).setOnClickListener(view
                -> openUrl(getResources().getString(R.string.facebook_url)));
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(mainLayout, R.string.about_send_open_app_error,
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
