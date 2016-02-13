package com.example.dijkstra_demo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.dijkstra_demo.helper.DBManager;
import com.example.dijkstra_demo.helper.DataBaseHelper_distance;
import com.example.dijkstra_demo.helper.DatabaseHelper_road;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements OnGetPoiSearchResultListener{

    Polyline mPolyline;
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;
    private PoiSearch poiSearch = null;
    private EditText et_start;
    private EditText et_end;
    private Button bt_poi;
    private Button bt_route;
    public DatabaseHelper_road databaseHelperRoad;
    public DataBaseHelper_distance dataBaseHelperDistance;
    SQLiteDatabase database1;
    SQLiteDatabase database2;
    ListView lv_node ;
    ListView lv_fenlei;
    ListView lv_poi;
    public DBManager dbManager;
    private final static String TAG = MainActivity.class.getName();
    Dijkstra d;
    MapStatus mMapStatus;
    int sou = 1;
    int dest = 2;
    String start_p;
    String end_p;
    double dist = -1;
    ArrayList<HashMap<String, Object>> nodeItem;
    ArrayList<HashMap<String, Object>> poiItem;
    static ArrayList<HashMap<String, Object>> poiItem1;

    List<Integer> node;
    int item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String []fenlei = {"餐饮","购物","教育","娱乐","住宿"};
        //地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册搜索事件监听
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);

        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);
        bt_route = (Button) findViewById(R.id.bt_route);
        bt_poi = (Button) findViewById(R.id.bt_poi);
        lv_node = (ListView) findViewById(R.id.lv_node);
        lv_fenlei = (ListView) findViewById(R.id.lv_fenlei);
        lv_poi = (ListView) findViewById(R.id.lv_poi);
        databaseHelperRoad = new DatabaseHelper_road(this);
        databaseHelperRoad.getWritableDatabase();
        databaseHelperRoad.getReadableDatabase();
        dataBaseHelperDistance = new DataBaseHelper_distance(this);
        dataBaseHelperDistance.getWritableDatabase();
        dataBaseHelperDistance.getReadableDatabase();
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        lv_fenlei.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fenlei));


        if ((new File(DBManager.DB_PATH + "/" + DBManager.DB_NAME1).exists())) {
            Toast.makeText(MainActivity.this,"cunzai",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"bu  cunzai",Toast.LENGTH_SHORT).show();

        }
        database1= SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME1, null);
        database2= SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME2, null);


        //sqLiteDatabase = databaseHelperRoad.getReadableDatabase();

        bt_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.getMap().clear();
                d = new Dijkstra(MainActivity.this,database2);
                start_p = et_start.getText().toString();
                end_p = et_end.getText().toString();
                if(isNumber(start_p) && isNumber(end_p)){
                    sou = Integer.parseInt(start_p);
                    dest = Integer.parseInt(end_p);
                }else{
                    sou = getidByName(start_p);
                    dest = getidByName(end_p);
                }

                dist = d.shortPath(sou, dest);
                d.printPath(sou, dest);
                node = d.getNode();
                if (dist < 0)
                    Toast.makeText(MainActivity.this, "dest is unreachable==="+ dist, Toast.LENGTH_SHORT).show();

                else {

                    LatLng latLng = getlatlng(node.get(0));
                    //initMap(latLng);
                    drawRoute(node);
                    Toast.makeText(MainActivity.this, "The distance is : " + dist + "====" + node.size() + "个节点==="+latLng.toString(), Toast.LENGTH_SHORT).show();
                    mMapView.setVisibility(View.VISIBLE);
                    lv_node.setVisibility(View.GONE);
                }
            }
        });

        bt_poi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeItem = new ArrayList<HashMap<String,Object>>();
                for(int i = 0;i<node.size();i++){
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("point",getNameByid(node.get(i)));
                    map.put("loca",getlatlng(node.get(i)).toString());
                    nodeItem.add(map);
                }
                Toast.makeText(MainActivity.this, "The item is : "  + "====" + nodeItem.size() + "个===", Toast.LENGTH_SHORT).show();

                SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,nodeItem,R.layout.node_item,new String[]{"point","loca"},new int[]{R.id.tv_point,R.id.tv_loca});
                lv_node.setAdapter(simpleAdapter);
                mMapView.setVisibility(View.GONE);
                lv_node.setVisibility(View.VISIBLE);
            }
        });

        lv_node.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMapView.setVisibility(View.GONE);
                lv_node.setVisibility(View.GONE);
                lv_fenlei.setVisibility(View.VISIBLE);
                item = node.get(position);
            }
        });

        lv_fenlei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        poiSearch.searchNearby(new PoiNearbySearchOption()
                                .keyword("美食")
                                .location(getlatlng(item))
                                .radius(2000)
                                .pageNum(20));
                        break;
                    case 1:
                        poiSearch.searchNearby(new PoiNearbySearchOption()
                                .keyword("购物")
                                .location(getlatlng(item))
                                .radius(2000)
                                .pageNum(20));
                        break;
                    case 2:
                        poiSearch.searchNearby(new PoiNearbySearchOption()
                                .keyword("学校")
                                .location(getlatlng(item))
                                .radius(2000)
                                .pageNum(20));
                        break;
                    case 3:
                        poiSearch.searchNearby(new PoiNearbySearchOption()
                                .keyword("娱乐")
                                .location(getlatlng(item))
                                .radius(2000)
                                .pageNum(20));
                        break;
                    case 4:
                        poiSearch.searchNearby(new PoiNearbySearchOption()
                                .keyword("酒店")
                                .location(getlatlng(item))
                                .radius(2000)
                                .pageNum(20));
                        break;

                }
            }
        });

        lv_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng  loc = (LatLng) poiItem1.get(position).get("poiloca");
                Toast.makeText(MainActivity.this,  "==="+loc.toString(), Toast.LENGTH_SHORT).show();

//                定义Maker坐标点

//构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_gcoding);
//构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(loc)
                        .icon(bitmap);
//在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
                initMap(loc);
                mMapView.setVisibility(View.VISIBLE);
                lv_node.setVisibility(View.GONE);
                lv_fenlei.setVisibility(View.GONE);
                lv_poi.setVisibility(View.GONE);
            }
        });
    }

    private void initMap(LatLng latLng){
        //定义缩放
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));

    }

    //判断是否为纯数字
    public boolean isNumber(String et){
        return et.matches("[0-9]+");
    }

    //根据id获取地名

    public String getNameByid(int id){
        String name = null;
        Cursor cur = database1.rawQuery("select name from roadnode where id =" + id, null);
        if(cur  != null){
            int NUM_DIS = cur.getCount();
            if(cur.moveToNext()){
                do {
                    name = cur.getString(cur.getColumnIndex("name"));
                }while(cur.moveToNext());
            }
        }
        return name;
    }

    //根据地名获取对应id
    public int getidByName(String name){
        Cursor cur = database1.rawQuery("select id from roadnode where name =" + "'"+name+"'", null);
        int id = 0;
        if(cur  != null){
            int NUM_DIS = cur.getCount();
            if(cur.moveToNext()){
                do {
                    id = cur.getInt(cur.getColumnIndex("id"));
                }while(cur.moveToNext());
            }
        }
        return id;
    }
    //获取每个点
    public LatLng getlatlng(int n){
        LatLng latLng = null;
        Cursor cur = database1.rawQuery("select lon,lat from roadnode where id =" + n, null);
        if(cur  != null){
            int NUM_DIS = cur.getCount();
            if(cur.moveToNext()){
                do {
                    double lon = cur.getDouble(cur.getColumnIndex("lon"));
                    double lat = cur.getDouble(cur.getColumnIndex("lat"));
                    latLng = new LatLng(lat,lon);
                }while(cur.moveToNext());
            }
        }
        return latLng;
    }

    public void drawRoute(List<Integer> node){

        List<LatLng> points = new ArrayList<>();
        int len = node.size();
        for(int i = 0 ; i < len ; i++){
            LatLng latLng = getlatlng(node.get(i));
            points.add(latLng);
        }

        //构建用户绘制多边形的Option对象
//        OverlayOptions polygonOption = new PolygonOptions()
//                .points(points)
//                .stroke(new Stroke(node.size(), 0xAA00FF00))
//                .fillColor(0xAAFFFF00);
//        mBaiduMap.addOverlay(polygonOption);
                OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(R.color.line).points(points);
        mBaiduMap.addOverlay(ooPolyline);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        poiSearch.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null
                || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            poiItem = new ArrayList<>();
            mBaiduMap.clear();
            List<PoiInfo> poiInfos = new ArrayList<>();
            System.out.println("poinfos"+poiInfos.size() + "");
            poiInfos = poiResult.getAllPoi();

            for(int i = 0;i<poiInfos.size();i++){
                PoiInfo poiInfo = poiInfos.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("poiname", poiInfo.name);
                map.put("poiadd",poiInfo.address);
                map.put("poiloca",poiInfo.location);
                poiItem.add(map);
            }
            poiItem1 = poiItem;

        if(poiInfos == null)
            Toast.makeText(MainActivity.this, "poi空===="+ poiResult.getTotalPoiNum()+ "==="+poiResult.getTotalPageNum()+"===="+poiResult.isHasAddrInfo(), Toast.LENGTH_LONG).show();

            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,poiItem,R.layout.poi_item,new String[]{"poiname","poiadd","poiloca"},new int[]{R.id.tv_poiname,R.id.tv_poiadd,R.id.tv_poilocation});
            lv_poi.setAdapter(simpleAdapter);
            mMapView.setVisibility(View.GONE);
            lv_node.setVisibility(View.GONE);
            lv_fenlei.setVisibility(View.GONE);
            lv_poi.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }
}
