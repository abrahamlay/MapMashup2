package com.dev.abrahamlay.mapmashup2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectUploadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView selectList;
    private Intent intent;
    private String select="select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_upload);
        selectList = (ListView) findViewById(R.id.list_select_upload_video);
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
}
