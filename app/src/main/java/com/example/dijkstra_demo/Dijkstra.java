package com.example.dijkstra_demo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FJS0420 on 2016/2/3.
 */
public class Dijkstra {

    Context context;


        private  List<Integer> node = new ArrayList<>();
        private double maxDouble = 9999999.99;
        private double [][]map = new double[1000][1000];
        private int  []path = new int[1000];

        public Dijkstra(Context context,SQLiteDatabase database2){
            this.readMapFromFile(database2);
        }

        private void readMapFromFile(SQLiteDatabase database2){
            for (int i = 0; i < 1000; ++i){
                for (int j = 0; j < 1000; ++j)
                    map[i][j] = -1;
            }

            Cursor cur = database2.rawQuery("select s_id,e_id,dis from distance",null);
            if(cur  != null){
                int NUM_DIS = cur.getCount();
                if(cur.moveToNext()){
                    do {
                        int i = cur.getInt(cur.getColumnIndex("s_id"));
                        int j = cur.getInt(cur.getColumnIndex("e_id"));
                        double dist = cur.getDouble(cur.getColumnIndex("dis"));
                        map[i][j] = dist;
                        map[j][i] = dist;
                    }while(cur.moveToNext());
                }
            }
        }


        public double shortPath(int v0, int dest){
            int i,j,k;
            double []dist = new double[1000];
            boolean[] visited= new boolean[1000];

            for(i=0;i<1000;i++)
            {
                if(map[v0][i]>0&&i!=v0)
                {
                    dist[i]=map[v0][i];path[i]=v0;//如果两节点有连接 dist[i]表示节点i到v0的距离
                }
                else
                {
                    dist[i]=maxDouble;path[i]=-1;

                }
                visited[i]=false;
                dist[v0]=0;
                path[v0]=v0;
            }
            visited[v0]=true;
            for(i=1;i<1000;i++)
            {
                double min=maxDouble;
                int u = -1;
                for(j=0;j<1000;j++)
                {
                    if (visited[j] == false && dist[j] < min) {
                        min = dist[j];
                        u = j;
                    }
                }

                if (u == dest)
                    return dist[u];
                if (u == -1)
                    return -1;
                visited[u]=true;

                for(k=0;k<1000;k++)
                {
                    if(visited[k]==false&&map[u][k] > 0&&min+map[u][k]<dist[k])
                    {
                        dist[k]=min+map[u][k];
                        path[k]=u;
                    }
                }
            }
            return dist[dest];
        }

        public void printPath(int sou, int dest){
            if (dest == sou){

                node.add(dest);
                return;
            }

            printPath(sou, path[dest]);

            node.add(dest);
        }

        public List<Integer> getNode(){
            return  node;
        }



    }


