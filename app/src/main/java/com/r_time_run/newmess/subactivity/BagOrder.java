package com.r_time_run.newmess.subactivity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.r_time_run.newmess.R;
import com.r_time_run.newmess.utils.PopupWindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单详情
 */
public class BagOrder extends Activity {
    private TextView bt_bagorder_back;
    private ListView lv_bagorder;
    private View popup_bagOrderView;
    private PopupWindow popup_bagOrder;
    private String[] titles;    //标题
    private int[] images;       //图片
    private String[] dirs;      //地址
    private String[] times;     //打包时间
    private String[] praises;      //点赞数
    private String[] buys;         //购买数
    private String[] prices;        //价钱
    private List<Map<String, Object>> listItems;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag_order);

        getDataFromServlet();           //从服务器中获取数据并填充到数组中
        initActivity();     //初始化订单详情页面


        //为listView添加适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.simple_item2,
                new String[]{"image", "title", "dir", "time"},
                new int[]{R.id.iv_mycollection_image, R.id.tv_mycollection_title, R.id.tv_mycollection_dir, R.id.tv_mycollection_time});
        lv_bagorder.setAdapter(simpleAdapter);
        //为listView添加点击事件
        lv_bagorder.setOnItemClickListener(new ItemClickListener());

        //该popupWindow"页面"能接受点击事件
        popup_bagOrderView.setFocusable(true);
        popup_bagOrderView.setFocusableInTouchMode(true);
        popup_bagOrderView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popup_bagOrder != null) {
                        popup_bagOrder.dismiss();
                    }
                }
                return false;
            }
        });


    }

    /**
     * 初始化订单详情界面
     */
    private void initActivity() {
        bt_bagorder_back = (TextView) findViewById(R.id.bt_bagorder_back);
        //返回键点击事件
        bt_bagorder_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BagOrder.this.finish();
            }
        });
        lv_bagorder = (ListView) findViewById(R.id.lv_bagorder);
        //没有订单时显示
        lv_bagorder.setEmptyView(findViewById(R.id.tv_empty_item));
        //SimpleAdapter中的list数据
        listItems = new ArrayList<>();
        if (titles != null){
            for (int i = 0; i < titles.length; i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("image", images[i]);
                item.put("title", titles[i]);
                item.put("dir", dirs[i]);
                item.put("time", times[i]);     //测试
                listItems.add(item);
            }
        }
        //订单视图
        popup_bagOrderView = this.getLayoutInflater().inflate(R.layout.popup_bag_order, null);
    }

    /**
     * listView的item点击事件
     */
    private class ItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            popup_bagOrder = new PopupWindow(popup_bagOrderView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Bundle data = new Bundle();
            data.putString("title", titles[position]);
            data.putString("dir", dirs[position]);
            data.putString("time", times[position]);
            data.putString("praise", praises[position]);
            data.putString("buy", buys[position]);
            data.putString("price", prices[position]);
            PopupWindowManager popupWindowManager = new PopupWindowManager(BagOrder.this, popup_bagOrderView, data);
            popupWindowManager.setDataToView();

            ScrollView sv_bagDialog = (ScrollView) popup_bagOrderView.findViewById(R.id.sv_bagDialog);
            sv_bagDialog.startAnimation(AnimationUtils.loadAnimation(BagOrder.this, R.anim.my_info_in));
            popup_bagOrder.setFocusable(true);
            popup_bagOrder.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            popup_bagOrderView.findViewById(R.id.bt_bagOrder_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup_bagOrder.dismiss();
                }
            });

        }
    }

    private void getDataFromServlet() {
        /**
         * 从服务器获取数据填写入该数组中
         */
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/newmessfoodbag.db3", null);
        int cursorNum = 0;
        try {
            Cursor cursor = db.rawQuery("select * from food_info", null);
            cursorNum = cursor.getCount();
            titles = new String[cursorNum];
            images = new int[cursorNum];
            dirs = new String[cursorNum];
            times = new String[cursorNum];
            praises = new String[cursorNum];
            prices = new String[cursorNum];
            buys = new String[cursorNum];
            while (cursor.moveToNext()) {
                titles[index] = cursor.getString(0);
                dirs[index] = cursor.getString(1);
                buys[index] = cursor.getString(2);
                prices[index] = cursor.getString(3);
                times[index] = cursor.getString(5)+"月"+cursor.getString(6)+"日 "+cursor.getString(7)+":"+cursor.getString(8);
                images[index] = R.drawable.food;
                System.out.println("!!!" + titles[index] + "." + dirs[index] + ":" + buys[index] + ":");
                index++;
            }
        } catch (SQLiteException se) {

        }
    }
}
