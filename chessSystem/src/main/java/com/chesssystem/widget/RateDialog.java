package com.chesssystem.widget;

import com.chesssystem.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 弹出搭子的评价选择框
 * @author lyg
 * @time 2016-6-30上午10:49:16
 */
public class RateDialog extends Dialog {
	private TextView tvGoodRate;
	private TextView tvMidRate;
	private TextView tvLowRate;
	public RateDialog(Context context,int theme) {
		super(context,theme);
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.pop_comment, null);
		tvGoodRate = (TextView) mView.findViewById(R.id.tv_comment_haoping);
		tvMidRate = (TextView) mView.findViewById(R.id.tv_comment_zhongping);
		tvLowRate = (TextView) mView.findViewById(R.id.tv_comment_chaping);
		super.setContentView(mView);
	}
	public View getGoodRate(){
		return tvGoodRate;
	}
	public View getMidRate(){
		return tvMidRate;
	}
	public View getLowRate(){
		return tvLowRate;
	}

}
