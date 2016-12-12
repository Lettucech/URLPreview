package com.gmail.brianbridge.urlpreview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();

	private EditText urlEditText;
	private Button generateButton;
	private TextView urlTextView;
	private RelativeLayout previewContainer;
	private SimpleDraweeView draweeView;
	private TextView titleTextView;
	private TextView descriptionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(this);

		setContentView(R.layout.activity_main);

		urlEditText			= (EditText) findViewById(R.id.editText_url);
		generateButton		= (Button) findViewById(R.id.btn_generate);
		urlTextView			= (TextView) findViewById(R.id.textView_clickableUrl);
		previewContainer	= (RelativeLayout) findViewById(R.id.relativeLayout_previewContainer);
		draweeView			= (SimpleDraweeView) findViewById(R.id.draweeView);
		titleTextView		= (TextView) findViewById(R.id.textView_title);
		descriptionTextView	= (TextView) findViewById(R.id.textView_description);

		generateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				generateUrlElements(urlEditText.getText().toString());
			}
		});
		previewContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlEditText.getText().toString()));
				startActivity(browserIntent);
			}
		});
	}

	private void generateUrlElements(final String url) {
		urlTextView.setText(url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Document doc = Jsoup.connect(url).get();
					final Elements title = doc.select("meta[property=og:title]");
					final Elements description = doc.select("meta[property=og:description]");
					final Elements image = doc.select("meta[property=og:image]");
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							titleTextView.setText(title.attr("content"));
							descriptionTextView.setText(description.attr("content"));
							draweeView.setImageURI(image.attr("content"));
						}
					});
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}).start();
	}
}
