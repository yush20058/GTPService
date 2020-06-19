import javafx.util.Pair;
import java.util.LinkedList;
import java.util.Queue;
public class Chessboard
{
    final int dir[][]={{1,0},{-1,0},{0,1},{0,-1}};
    boolean black_or_white=true;
    int status[][]=new int[19][19];
    public void modify()
    {
        have=new boolean[19][19];
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                if(!have[i][j]&&status[i][j]==(black_or_white?-1:1))
                {
                    if(need_to_remove(i,j))
                    {
                        remove(i,j);
                    }
                }
            }
        }
    }
    boolean have[][];
    public boolean need_to_remove(int x,int y)
    {
        Queue<Pair<Integer,Integer>> q=new LinkedList<>();
        q.add(new Pair<>(x,y));
        have[x][y]=true;
        boolean remove=true;
        while(!q.isEmpty())
        {
            int cx=q.peek().getKey();
            int cy=q.peek().getValue();
            q.poll();
            for(int i=0;i<4;++i)
            {
                int nx=cx+dir[i][0];
                int ny=cy+dir[i][1];
                if(nx>=0&&nx<19&&ny>=0&&ny<19&&!have[nx][ny])
                {
                    if(status[nx][ny]==0)
                    {
                        remove=false;
                    }
                    else if(status[nx][ny]==(black_or_white?-1:1))
                    {
                        q.add(new Pair<>(nx,ny));
                        have[nx][ny]=true;
                    }
                }
            }
        }
        return remove;
    }
    public void remove(int x,int y)
    {
        status[x][y]=0;
        for(int i=0;i<4;++i)
        {
            int nx=x+dir[i][0];
            int ny=y+dir[i][1];
            if(nx>=0&&nx<19&&ny>=0&&ny<19&&status[nx][ny]==(black_or_white?-1:1))
            {
                remove(nx,ny);
            }
        }
    }
    public void move(Pair<Integer,Integer>index)
    {
        status[index.getKey()][index.getValue()]=(black_or_white?1:-1);
        modify();
        black_or_white=!black_or_white;
    }
    public void clear()
    {
        status=new int[19][19];
        black_or_white=true;
    }
}