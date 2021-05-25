package com.huangchao.asyncchanneltest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.asyncConnectBtn).setOnClickListener(v -> {
			startActivity(new Intent(this, AsyncConnectTest.class));
		});

		findViewById(R.id.syncConnectBtn).setOnClickListener(v -> {
			startActivity(new Intent(this, SyncConnectTest.class));
		});
	}
}
