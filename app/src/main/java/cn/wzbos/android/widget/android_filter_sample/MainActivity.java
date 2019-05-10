package cn.wzbos.android.widget.android_filter_sample;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wzbos.android.widget.filter.FilterView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.wzbos.android.widget.linked.IPickerData;
import cn.wzbos.android.widget.linked.LinkedView;
import cn.wzbos.android.widget.linked.PickerView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";


    FilterView mAreaFilter;
    FilterView mPriceFilter;
    FilterView mSortFilter;


    LinkedView mPriceView;
    LinkedView mAreaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAreaFilter = findViewById(R.id.area_filter);
        mPriceFilter = findViewById(R.id.price_filter);
        mSortFilter = findViewById(R.id.sort_filter);

        mAreaFilter.setExpandedView(getAreaView());
        mPriceFilter.setExpandedView(initPriceView());
        mSortFilter.setExpandedView(initSortFilterView());
    }


    private List<MyPickerBean> getData() {
        List<MyPickerBean> list = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            AssetManager assetManager = getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("area.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }

            list = new Gson().fromJson(stringBuilder.toString(), new TypeToken<List<MyPickerBean>>() {
            }.getType());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private LinkedView getAreaView() {
        mAreaView = new LinkedView(this);
        mAreaView.setLinkedMode(true);
        mAreaView.setDivider(true);
        mAreaView.setOnCreatePickerViewListener((prevPicker, prevPosition, nextPicker, nextPosition) -> {
            switch (nextPosition) {
                case 0:
                    nextPicker.setWidth(300);
                    nextPicker.setWeight(0);
                    break;
                case 1: {
                    List<IPickerData> values = prevPicker.getSelectedValues();
                    boolean isd = (values.size() > 0 && "附近".equals(values.get(0).getName()));
                    nextPicker.setShowIcon(isd);
                    nextPicker.setShowPickCount(!isd);
                }
                break;
                case 2:
                    nextPicker.setMultiSelect(true);
                    nextPicker.setShowIcon(true);
                    break;
            }
            nextPicker.setBackgroundColor(((nextPosition + 1) % 2) == 0 ? 0xFFF5F8FC : 0xFFFFFFFF);
        });
        mAreaView.setData(getData());
        mAreaView.setOnPickerViewItemClickedListener((pickerView, position, data) -> {
            if (position == 0) {
                PickerView option = mAreaView.getPickerView(1);
                if (option != null) {
                    boolean isd = "附近".equals(data.getName());
                    option.setShowIcon(isd);
                    option.setShowPickCount(!isd);
                }
            } else if (position == 1) {
                PickerView option = mAreaView.getPickerView(2);
                if (option != null) {
                    option.setShowIcon(true);
                    option.setMultiSelect(true);
                }
            }
        });
        mAreaView.setOnPickedListener((linkView, result) -> {
            Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
            mAreaFilter.setSelected(!TextUtils.isEmpty(result.getSplitIds(2, ",")));
            mAreaFilter.close();
        });

        return mAreaView;
    }

    LinkedView initPriceView() {
        mPriceView = new LinkedView(this);
        mPriceView.setBackgroundColor(0xFFFFFFFF);
        mPriceView.setResetButtonVisible(false);
        mPriceView.setDivider(true);
        mPriceView.setConfirmButtonVisible(false);

        PickerView pickerView = new PickerView(this)
                .setShowIcon(true)
                .setMultiSelect(false)
                .setShowDivider(true)
                .setWeight(1)
                .setData(getPickerData());

        mPriceView.addPickerView(pickerView);
        mPriceView.setOnCreatePickerViewListener((prevView, prevPosition, nextView, nextPosition) -> Log.d(TAG, "onCreatePickerView"));
        mPriceView.setOnPickedListener((linkView, result) -> {
            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
            mPriceFilter.setSelected(!TextUtils.isEmpty(result.getSplitIds(0, ",")));
            mPriceFilter.close();
        });

        return mPriceView;
    }

    LinkedView sortFilterView;

    LinkedView initSortFilterView() {
        sortFilterView = new LinkedView(this);
        sortFilterView.setBackgroundColor(0xFFFFFFFF);
        sortFilterView.setResetButtonVisible(false);
        sortFilterView.setDivider(true);
        sortFilterView.setConfirmButtonVisible(false);
        sortFilterView.addPickerView(new PickerView(this)
                .setShowIcon(true)
                .setMultiSelect(false)
                .setShowDivider(true)
                .setWeight(1)
                .setData(getPickerData()));
        sortFilterView.setOnCreatePickerViewListener((prevView, prevPosition, nextView, nextPosition) -> Log.d(TAG, "onCreatePickerView"));
        sortFilterView.setOnPickedListener((linkView, result) -> {
            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
            mSortFilter.setSelected(!TextUtils.isEmpty(result.getSplitIds(0, ",")));
            mSortFilter.close();
        });
        return sortFilterView;
    }

    private ArrayList<MyPickerBean> getPickerData() {
        ArrayList<MyPickerBean> list1 = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            list1.add(new MyPickerBean(i, String.valueOf((char) ('A' + i))));
        }
        return list1;
    }


}
