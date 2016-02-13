import java.io.FileInputStream;
import java.io.FileReader;
import java.util.DoubleSummaryStatistics;
import java.util.Scanner;

public class Dijkstra {
    private double maxDouble = 9999999.99;
    private double [][]map = new double[1000][1000];
    private int  []path = new int[1000];

    public Dijkstra(){
        this.readMapFromFile();
    }

    private void readMapFromFile(){
        for (int i = 0; i < 1000; ++i){
            for (int j = 0; j < 1000; ++j)
                map[i][j] = -1;
        }
        try{
            Scanner scanner = new Scanner(new FileInputStream("data.txt"));
            while(scanner.hasNext()){
                int i = scanner.nextInt();


                int j = scanner.nextInt();
                double dist = scanner.nextDouble();
                //System.out.println(i + " " + j + " " + dist);
                map[i][j] = dist;
                map[j][i] = dist;
            }

        }catch(Exception e){
            e.printStackTrace();
            return;
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
                dist[i]=map[v0][i];path[i]=v0;

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

            for(k=0;k<1000;k++)   //����dist�����ֵ��·����ֵ
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
            System.out.print(dest + " ");
            return;
        }

        printPath(sou, path[dest]);
        System.out.print(dest + " ");
    }

    public static void main(String []argv){
        Dijkstra d = new Dijkstra();
        System.out.println("Please input the source point and destination point:");
        Scanner sc = new Scanner(System.in);
        int sou = sc.nextInt();
        int dest = sc.nextInt();
        double dist = d.shortPath(sou, dest);

        if (dist < 0)
            System.out.println("dest is unreachable");
        else {
            System.out.println("The distance is : " + dist);
            d.printPath(sou, dest);
        }

    }
}
