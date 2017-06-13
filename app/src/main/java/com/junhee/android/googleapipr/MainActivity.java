package com.junhee.android.googleapipr;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.junhee.android.googleapipr.domain.Data;
import com.junhee.android.googleapipr.domain.Row;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskInterface {

    TextView textView;
    ListView listView;
    String url = "";

    // 데이터 상수화
    static final String URL_PREFIX = "http://openAPI.seoul.go.kr:8088/";
    static final String URL_HISTORY_CERT = "76484578446a686c383658635a4265/";
    static final String URL_HISTOY_MID = "json/PublicWiFiPlaceInfo/";

    static final int PAGE_OFFSET = 50;

    int pageBegin = 1;
    int pageEnd = 10;

    final List<String> datas = new ArrayList<>();
    final List<Double> latLng_X = new ArrayList<>();
    final List<Double> latLng_Y = new ArrayList<>();
    ArrayAdapter<String> adapter = null;

    GoogleMap mainMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng latLng = new LatLng(latLng_Y.get(position), latLng_X.get(position));
                mainMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(view.getContext(), "[ " + datas.get(position).toString() + " ] 으로 이동합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);

        // mapView 연결
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFrag = (SupportMapFragment) manager.findFragmentById(R.id.mapView);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mainMap = googleMap;
        setPage(1);
        setUrl(pageBegin, pageEnd);
        Remote.newTask(this);


    }

    private void setPage(int page) {
        pageEnd = page * PAGE_OFFSET;
        pageBegin = pageEnd - PAGE_OFFSET + 1;
    }

    // TODO 여기 추상화
    private void setUrl(int begin, int end) {
        // url 주소 구조를 이용한다.
        url = URL_PREFIX + URL_HISTORY_CERT + URL_HISTOY_MID + begin + "/" + end + "/강남구";

    }

    @Override
    public String getUrl() {
        return url;
    }

    // TODO 여기 추상화
    @Override
    public void postExecute(String jsonString) {

        Gson gson = new Gson();
        Data wifiData = gson.fromJson(jsonString, Data.class);
        textView.setText("총 wifi 개수 : " + wifiData.getPublicWiFiPlaceInfo().getList_total_count());
        Row[] rows = wifiData.getPublicWiFiPlaceInfo().getRow();

        for (Row row : rows) {
            setList(row.getPLACE_NAME(), row.getINSTL_Y(), row.getINSTL_X());
/*

            datas.add(row.getPLACE_NAME());
            latLng_Y.add(row.getINSTL_Y());
            latLng_X.add(row.getINSTL_X());

*/
            MarkerOptions mapMarker = new MarkerOptions();
            LatLng tempCoord = new LatLng(row.getINSTL_Y(), row.getINSTL_X());
            mapMarker.position(tempCoord);
            mapMarker.title(row.getPLACE_NAME());
            mainMap.addMarker(mapMarker);
        }
        LatLng sinsa = new LatLng(37.516292, 127.020014);
        mainMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sinsa, 15));

        // 그리고 adapter를 갱신해줌
        // 기존 데이터가 없기 떄문에 .clear(); 메소드를 통해 초기화 해주지 않아도 됨
        adapter.notifyDataSetChanged();

    }

    private void setList(String name, double y, double x) {
        datas.add(name);
        latLng_X.add(x);
        latLng_Y.add(y);
    }


}
