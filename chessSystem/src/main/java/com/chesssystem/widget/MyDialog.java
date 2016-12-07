package com.chesssystem.widget;

import com.chesssystem.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 弹出提示框
 * @author lyg
 * @time 2016-6-30上午10:49:16
 */
public class MyDialog extends Dialog {
	private EditText editText;
	private TextView positiveButton, negativeButton;
	private TextView title;
	private TextView content;
	public MyDialog(Context context,int theme) {
		super(context,theme);
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.pop_join, null);
		editText = (EditText) mView.findViewById(R.id.edt_pop_contact);
		title = (TextView) mView.findViewById(R.id.ll_pop_title);
		content=(TextView) mView.findViewById(R.id.tv_pop_prompt);
		positiveButton = (TextView) mView.findViewById(R.id.tv_pop_confirm);
		negativeButton = (TextView) mView.findViewById(R.id.tv_pop_cancel);
		super.setContentView(mView);
	}
	public View getEditText(){
		return editText;
	}
	public View getContent(){
		return content;
	}
	public View getCancel(){
		return negativeButton;
	}
	
	 @Override
	public void setContentView(int layoutResID) {
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
	}

	@Override
	public void setContentView(View view) {
	}
	
	/** 
     * 确定键监听器 
     * @param listener 
     */  
    public void setOnPositiveListener(View.OnClickListener listener){  
    	positiveButton.setOnClickListener(listener);  
    }  
    /** 
     * 取消键监听器 
     * @param listener 
     */  
    public void setOnNegativeListener(View.OnClickListener listener){  
    	negativeButton.setOnClickListener(listener);  
    }

}
