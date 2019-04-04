package com.ivring.drag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ivring.drag.deskfloating.DragTableButton;
import com.ivring.drag.deskfloating.SuspendUtils;
import com.ivring.drag.infloating.DragFloatingActionButton;

public class MainActivity extends AppCompatActivity {
    DragFloatingActionButton mActionButton;
    public static final int REQUEST_CODE = 100;
    private DragTableButton mSys_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置应用内的悬浮按钮
        setInFloatButton();

        //设置桌面悬浮框
        setDeskFloatButton();
    }

    /**
     * 设置桌面悬浮框
     */
    private void setDeskFloatButton() {
        SuspendUtils.canDrawOverlays(this, REQUEST_CODE);

        mSys_view = new DragTableButton(this);
        mSys_view.setImageResource(R.mipmap.ic_launcher);
        mSys_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"桌面悬浮框",Toast.LENGTH_SHORT).show();
            }
        });
        SuspendUtils.showDragTableButton(mSys_view, this);
    }

    /**
     * 设置应用内的悬浮按钮
     */
    private void setInFloatButton() {
        mActionButton = findViewById(R.id.fb_main);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "应用内悬浮", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SuspendUtils.removeWindowView(mSys_view,this);
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CODE:
//                    drag();
//
//                break;
//        }
//    }
}
