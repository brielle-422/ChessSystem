package com.chesssystem.ui.order;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chesssystem.R;
import com.chesssystem.fragment.OrderFragment;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:56
 */
public class OrderActivity extends FragmentActivity implements OnClickListener {
	private LinearLayout llBack;
	private TextView tvTitle;
	OrderFragment orderFragment = (OrderFragment) OrderFragment.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		initView();
		
	}
	private void initView() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.fragment_container_MainActivity, orderFragment).commit(); 
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.order);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;

		default:
			break;
		}
	}
}
