package com.dev.abrahamlay.mapmashup2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

public class SelectUploadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ToolTipView.OnToolTipViewClickedListener {

    private ListView selectList;
    private Intent intent;
    private String select="select";
    private ToolTipView myToolTipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_upload);
        selectList = (ListView) findViewById(R.id.list_select_upload_video);
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_select_upload_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("Select your resource video.")
                .withColor(Color.CYAN)
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, selectList);
        myToolTipView.setOnToolTipViewClickedListener(SelectUploadActivity.this);

        String[] select = new String[] { "MyYoutubeChannel",
                "Other Youtube Channel",
                "Other Youtube Video"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, select);
       selectList.setAdapter(adapter);
        selectList.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (pos){
            case 0:
                intent=new Intent(getApplicationContext(),AccountVideoListActivity.class);
                intent.putExtra(select,"account");
                startActivity(intent);
                break;
            case 1:
                intent=new Intent(getApplicationContext(),ChannelListActivity.class);
                intent.putExtra(select,"channel");
                startActivity(intent);
                break;
            case 2:
                intent=new Intent(getApplicationContext(),SearchVideoListActivity.class);
                intent.putExtra(select,"video");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {

    }
}
