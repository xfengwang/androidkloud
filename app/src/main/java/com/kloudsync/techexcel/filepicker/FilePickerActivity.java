package com.kloudsync.techexcel.filepicker;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.ArrayList;


public class FilePickerActivity extends AppCompatActivity implements View.OnClickListener,OnUpdateDataListener {
    private Button btn_common,btn_all;
    private TextView tv_size,tv_confirm;
    private Fragment commonFileFragment,allFileFragment;
    private boolean isConfirm = false;
	TextView titleText;
	private RelativeLayout backLayout;

	private int fileType=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        fileType=getIntent().getIntExtra("fileType",0);
        initView();
        initEvent();
        setFragment(1);
    }

    private void initEvent() {
        btn_common.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initView() {
        btn_common = (Button) findViewById(R.id.btn_common);
        btn_all = (Button) findViewById(R.id.btn_all);
        tv_size = (TextView) findViewById(R.id.tv_size);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
	    backLayout = findViewById(R.id.layout_back);
	    titleText = findViewById(R.id.tv_title);
	    titleText.setText("选择一个文件");
	    backLayout.setOnClickListener(this);
	    PickerManager.getInstance().files = new ArrayList<>();
    }

    private void setFragment(int type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        switch (type){
            case 1:
                if(commonFileFragment==null){
                    commonFileFragment = FileCommonFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fileType",fileType);
                    commonFileFragment.setArguments(bundle);//数据传递到fragment中
                    ((FileCommonFragment)commonFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content,commonFileFragment);
                }else {
                    fragmentTransaction.show(commonFileFragment);
                }
                break;
            case 2:
                if(allFileFragment==null){
                    allFileFragment = FileAllFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fileType",fileType);
                    allFileFragment.setArguments(bundle);//数据传递到fragment中
                    ((FileAllFragment)allFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content,allFileFragment);
                }else {
                    fragmentTransaction.show(allFileFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction) {
        if (commonFileFragment != null) {
            transaction.hide(commonFileFragment);
        }
        if (allFileFragment != null) {
            transaction.hide(allFileFragment);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_common:
                setFragment(1);
                btn_common.setBackgroundResource(R.mipmap.no_read_pressed);
                btn_common.setTextColor(ContextCompat.getColor(this,R.color.white));
                btn_all.setBackgroundResource(R.mipmap.already_read);
                btn_all.setTextColor(ContextCompat.getColor(this,R.color.blue));
                break;
            case R.id.btn_all:
                setFragment(2);
                btn_common.setBackgroundResource(R.mipmap.no_read);
                btn_common.setTextColor(ContextCompat.getColor(this,R.color.blue));
                btn_all.setBackgroundResource(R.mipmap.already_read_pressed);
                btn_all.setTextColor(ContextCompat.getColor(this,R.color.white));
                break;
            case R.id.tv_confirm:
                isConfirm = true;
                setResult(RESULT_OK);
                finish();
                break;
	        case R.id.layout_back:
		        finish();
		        break;
        }
    }
    private long currentSize;
    @Override
    public void update(long size) {
        currentSize+=size;
        tv_size.setText(getString(R.string.already_select,FileUtils.getReadableFileSize(currentSize)));
        String res = "("+PickerManager.getInstance().files.size()+"/"+PickerManager.getInstance().maxCount+")";
        tv_confirm.setText(getString(R.string.file_select_res,res));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isConfirm){
            PickerManager.getInstance().files.clear();
        }
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
