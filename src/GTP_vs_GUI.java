import com.melloware.jintellitype.JIntellitype;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
public class GTP_vs_GUI
{
    Robot robot;
    Queue<String>steps;
    boolean confirmed=false,end=true,black_or_white=true,first=true;
    int x[][]=new int[19][19];
    int y[][]=new int[19][19];
    Chessboard chessboard=new Chessboard();
    int times[][];
    int x_ind,y_ind,x_pos,y_pos;
    int inf=0x3f3f3f3f;
    final int dir[][]={{1,0},{-1,0},{0,1},{0,-1}};
    final int eps=10;
    BufferedImage store;
    Dimension dimension;
    Rectangle rectangle;
    int min_x,min_y,max_x,max_y,time;
    GTP gtp;
    public GTP_vs_GUI(GTP gtp)
    {
        this.gtp=gtp;
    }
    public void get_setting()
    {
        Scanner input=null;
        try
        {
            FileInputStream fileInputStream=new FileInputStream("setting.txt");
            BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
            input=new Scanner(bufferedInputStream);
        }
        catch(IOException e)
        {
            System.err.println("找不到或无法读取配置文件setting.txt");
            return;
        }
        try
        {
            min_x=input.nextInt();
            min_y=input.nextInt();
            max_x=input.nextInt();
            max_y=input.nextInt();
        }
        catch(Exception e)
        {
            System.err.println("setting.txt文件格式错误！");
        }
        finally
        {
            input.close();
        }
        if(Math.abs((max_x-min_x)-(max_y-min_y))>eps||max_x-min_x+1<300||max_y-min_y+1<300)
        {
            System.err.println("The width is "+(max_x-min_x)+".");
            System.err.println("The height is "+(max_y-min_y)+".");
            System.err.println("Cannot find the chessboard, please shoot more precisely.");
            return;
        }
        System.out.println("Chessboard is bounded by ("+min_x+","+min_y+") and ("+max_x+","+max_y+").");
        for(int i=0;i<19;++i)
        {
            int x_pos=min_x+(int)((max_x-min_x)/18.0*i+0.5);
            for(int j=0;j<19;++j)
            {
                int y_pos=min_y+(int)((max_y-min_y)/18.0*j+0.5);
                x[i][j]=x_pos;
                y[i][j]=y_pos;
            }
        }
        confirmed=true;
    }
    public void store_setting()
    {
        if(!confirmed)
        {
            System.out.println("GUI棋盘没有定义！");
            return;
        }
        PrintWriter out=null;
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream("setting.txt");
            out=new PrintWriter(fileOutputStream);
        }
        catch(IOException e)
        {
            System.err.println("无法写入根目录下setting.txt！");
            return;
        }
        out.println(min_x+" "+min_y);
        out.println(max_x+" "+max_y);
        out.close();
        System.out.println("配置已成功写入根目录下setting.txt。");
    }
    public void give_pos()
    {
        ++time;
        PointerInfo pointerInfo=MouseInfo.getPointerInfo();
        Point point=pointerInfo.getLocation();
        if((time&1)==1)
        {
            first=true;
        }
        if(confirmed)
        {
            confirmed=false;
            first=true;
        }
        if(first)
        {
            first=false;
            min_x=(int)point.getX();
            min_y=(int)point.getY();
        }
        else
        {
            max_x=(int)point.getX();
            max_y=(int)point.getY();
            if(Math.abs((max_x-min_x)-(max_y-min_y))>eps||max_x-min_x+1<300||max_y-min_y+1<300)
            {
                System.err.println("The width is "+(max_x-min_x)+".");
                System.err.println("The height is "+(max_y-min_y)+".");
                System.err.println("Cannot find the chessboard, please shoot more precisely.");
                return;
            }
            System.out.println("Chessboard is bounded by ("+min_x+","+min_y+") and ("+max_x+","+max_y+").");
            for(int i=0;i<19;++i)
            {
                int x_pos=min_x+(int)((max_x-min_x)/18.0*i+0.5);
                for(int j=0;j<19;++j)
                {
                    int y_pos=min_y+(int)((max_y-min_y)/18.0*j+0.5);
                    x[i][j]=x_pos;
                    y[i][j]=y_pos;
                }
            }
            confirmed=true;
        }
    }
    public boolean distinguish(int des_x[][],int des_y[][])
    {
        store=robot.createScreenCapture(rectangle);
        int width=store.getWidth();
        int height=store.getHeight();
        PointerInfo pointerInfo=MouseInfo.getPointerInfo();
        Point point=pointerInfo.getLocation();
        int x_ind=(int)point.getX();
        int y_ind=(int)point.getY();
        System.out.println("Central position is settled at ("+x_ind+","+y_ind+").");
        int std=store.getRGB(x_ind,y_ind);
        Color std_color=new Color(std,true);
        Queue<Pair<Integer,Integer>> q=new LinkedList<>();
        HashMap<Pair<Integer,Integer>,Boolean> have=new HashMap<>();
        q.add(new Pair<>(x_ind,y_ind));
        have.put(new Pair<>(x_ind,y_ind),true);
        int min_x=inf,min_y=inf,max_x=0,max_y=0;
        while(!q.isEmpty())
        {
            int cx=q.peek().getKey();
            int cy=q.peek().getValue();
            min_x=Math.min(min_x,cx);
            min_y=Math.min(min_y,cy);
            max_x=Math.max(max_x,cx);
            max_y=Math.max(max_y,cy);
            q.poll();
            for(int i=0;i<4;++i)
            {
                int nx=cx+dir[i][0];
                int ny=cy+dir[i][1];
                Pair np=new Pair(nx,ny);
                if(!have.containsKey(np)&&is_valid(nx,ny,width,height))
                {
                    Color next_color=new Color(store.getRGB(nx,ny),true);
                    if(similar(std_color,next_color,false))
                    {
                        q.add(np);
                        have.put(np,true);
                    }
                }
            }
        }
        if(Math.abs((max_x-min_x)-(max_y-min_y))>eps||max_x-min_x+1<300||max_y-min_y+1<300)
        {
            System.err.println("The width is "+(max_x-min_x)+".");
            System.err.println("The height is "+(max_y-min_y)+".");
            System.err.println("Cannot find the chessboard, please shoot more precisely.");
            return false;
        }
        System.out.println("Chessboard is bounded by ("+min_x+","+min_y+") and ("+max_x+","+max_y+").");
        for(int i=0;i<19;++i)
        {
            int x_pos=min_x+(int)((max_x-min_x)/18.0*i+0.5);
            for(int j=0;j<19;++j)
            {
                int y_pos=min_y+(int)((max_y-min_y)/18.0*j+0.5);
                des_x[i][j]=x_pos;
                des_y[i][j]=y_pos;
            }
        }
        confirmed=true;
        return true;
    }
    public void output(BufferedImage bufferedImage,String name)
    {
        try
        {
            File file=new File(name);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",fileOutputStream);
            fileOutputStream.close();
        }
        catch(IOException e)
        {
            System.err.println("Cannot output the screen capture.");
        }
    }
    public void chess_output(int status[][])
    {
        PrintWriter out=null;
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream("chessboard.txt");
            out=new PrintWriter(fileOutputStream);
        }
        catch(IOException e)
        {
            return;
        }
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                out.print(status[j][i]==-1?"○ ":(status[j][i]==1?"● ":"+ "));
            }
            out.println();
        }
        out.close();
    }
    public boolean is_valid(int x,int y,int width,int height)
    {
        return x>=0&&x<width&&y>=0&&y<height;
    }
    public boolean similar(Color c1,Color c2,boolean tight)
    {
        int R_1=c1.getRed();
        int G_1=c1.getGreen();
        int B_1=c1.getBlue();
        int R_2=c2.getRed();
        int G_2=c2.getGreen();
        int B_2=c2.getBlue();
        int R=R_1-R_2;
        int G=G_1-G_2;
        int B=B_1-B_2;
        int rmean=(R_1+R_2)/2;
        if(tight)
        {
            return Math.sqrt((2+rmean/256)*(R*R)+4*(G*G)+(2+(255-rmean)/256)*(B*B))<179.0;
        }
        return Math.sqrt((2+rmean/256)*(R*R)+4*(G*G)+(2+(255-rmean)/256)*(B*B))<300.0;
    }
    public void click(int x,int y)
    {
        for(int i=0;i<10;++i)
        {
            robot.mouseMove(x,y);
        }
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    public String get_name(int x,int y)
    {
        if(x+'A'>='I')
        {
            return ""+(char)(x+'A'+1)+(19-y);
        }
        return ""+(char)(x+'A')+(19-y);
    }
    public Pair<Integer,Integer>get_index(String step)
    {
        return new Pair<>(step.charAt(0)>='I'?step.charAt(0)-'A'-1:step.charAt(0)-'A',19-(new Integer(step.substring(1))));
    }
    public void play_black()
    {
        if(!confirmed)
        {
            System.err.println("Please shoot the GUI chessboard first.");
            return;
        }
        gtp.exe_command("clear_board");
        end=false;
        chessboard.black_or_white=black_or_white=true;
        chessboard.clear();
        steps=new LinkedList<>();
        Thread listen_gtp=new Thread(()->gtp.process_info(steps));
        listen_gtp.start();
        while(!judge_change())
        {
            robot.delay(200);
        }
        System.out.println("The position of the first turn is settled at ("+x_pos+","+y_pos+").");
        PrintWriter out=null;
        boolean can_write=true;
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream("Entries.txt");
            out=new PrintWriter(fileOutputStream);
        }
        catch(IOException e)
        {
            can_write=false;
            System.err.println("Cannot write the diary.");
        }
        while(!end)
        {
            if(can_write)
            {
                out.println("("+x_pos+","+y_pos+")");
                out.flush();
            }
            if(black_or_white)
            {
                gtp.exe_command("play black "+get_name(x_pos,y_pos));
                gtp.exe_command("genmove white");
            }
            else
            {
                click(x_ind,y_ind);
            }
            black_or_white=!black_or_white;
            while(!end)
            {
                robot.delay(200);
                if(black_or_white)
                {
                    if(judge_change())
                    {
                        break;
                    }
                }
                else
                {
                    if(!steps.isEmpty())
                    {
                        if(steps.peek().equalsIgnoreCase("PASS")||steps.peek().equalsIgnoreCase("resign"))
                        {
                            System.err.println("white "+steps.peek());
                            end=true;
                            break;
                        }
                        System.err.println("white moves at "+steps.peek());
                        Pair index=get_index(steps.peek());
                        steps.poll();
                        x_pos=(Integer)index.getKey();
                        y_pos=(Integer)index.getValue();
                        x_ind=x[x_pos][y_pos];
                        y_ind=y[x_pos][y_pos];
                        chessboard.move(index);
                        break;
                    }
                }
            }
        }
        if(can_write)
        {
            out.close();
        }
        listen_gtp.stop();
        System.out.println("转接已停止。");
    }
    public void play_white()
    {
        if(!confirmed)
        {
            System.err.println("Please shoot the GUI chessboard first.");
            return;
        }
        end=false;
        chessboard.black_or_white=black_or_white=true;
        chessboard.clear();
        steps=new LinkedList<>();
        Thread listen_gtp=new Thread(()->gtp.process_info(steps));
        listen_gtp.start();
        gtp.exe_command("clear_board");
        gtp.exe_command("genmove black");
        while(steps.isEmpty())
        {
            robot.delay(200);
        }
        Pair index=get_index(steps.peek());
        steps.poll();
        x_pos=(Integer)index.getKey();
        y_pos=(Integer)index.getValue();
        x_ind=x[x_pos][y_pos];
        y_ind=y[x_pos][y_pos];
        System.out.println("The position of the first turn is settled at ("+x_pos+","+y_pos+").");
        chessboard.move(index);
        PrintWriter out=null;
        boolean can_write=true;
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream("Entries.txt");
            out=new PrintWriter(fileOutputStream);
        }
        catch(IOException e)
        {
            can_write=false;
            System.err.println("Cannot write the diary.");
        }
        while(!end)
        {
            if(can_write)
            {
                out.println("("+x_pos+","+y_pos+")");
                out.flush();
            }
            if(black_or_white)
            {
                click(x_ind,y_ind);
            }
            else
            {
                gtp.exe_command("play white "+get_name(x_pos,y_pos));
                gtp.exe_command("genmove black");
            }
            black_or_white=!black_or_white;
            while(!end)
            {
                robot.delay(200);
                if(black_or_white)
                {
                    if(!steps.isEmpty())
                    {
                        if(steps.peek().equalsIgnoreCase("PASS")||steps.peek().equalsIgnoreCase("resign"))
                        {
                            System.err.println("black "+steps.peek());
                            end=true;
                            break;
                        }
                        System.err.println("black moves at "+steps.peek());
                        index=get_index(steps.peek());
                        steps.poll();
                        x_pos=(Integer)index.getKey();
                        y_pos=(Integer)index.getValue();
                        x_ind=x[x_pos][y_pos];
                        y_ind=y[x_pos][y_pos];
                        chessboard.move(index);
                        break;
                    }
                }
                else
                {
                    if(judge_change())
                    {
                        break;
                    }
                }
            }
        }
        if(can_write)
        {
            out.close();
        }
        listen_gtp.stop();
        System.out.println("转接已停止。");
    }
    public boolean judge_change()
    {
        store=robot.createScreenCapture(rectangle);
        int new_status[][]=get_status(store,min_x,min_y,max_x,max_y);
        int times=0,store_status[][]=new int[19][19];
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                store_status[i][j]=chessboard.status[i][j];
                if(new_status[i][j]==(black_or_white?1:-1)&&chessboard.status[i][j]==0)
                {
                    ++times;
                    x_pos=i;
                    y_pos=j;
                    x_ind=x[i][j];
                    y_ind=y[i][j];
                }
            }
        }
        if(times!=1)
        {
            return false;
        }
        chessboard.move(new Pair<>(x_pos,y_pos));
        boolean ok=true;
        for(int i=0;i<19&&ok;++i)
        {
            for(int j=0;j<19;++j)
            {
                if(chessboard.status[i][j]!=new_status[i][j])
                {
                    ok=false;
                    break;
                }
            }
        }
        if(!ok)
        {
            chessboard.status=store_status;
            chessboard.black_or_white=black_or_white;
            return false;
        }
        return true;
    }
    public int[][]get_status(BufferedImage image,int low_i,int low_j,int high_i,int high_j)
    {
        int status[][]=new int[19][19];
        int black_num[][]=new int[19][19];
        int white_num[][]=new int[19][19];
        double width=(high_i-low_i)/18.0;
        double length=(high_j-low_j)/18.0;
        low_i-=(int)(width/2.0+0.5);
        low_j-=(int)(length/2.0+0.5);
        high_i+=(int)(width/2.0+0.5);
        high_j+=(int)(length/2.0+0.5);
        for(int i=low_i;i<=high_i;++i)
        {
            for(int j=low_j;j<=high_j;++j)
            {
                Color color=new Color(image.getRGB(i,j),true);
                int x=(int)((i-low_i)/width);
                int y=(int)((j-low_j)/length);
                if(x==19)
                {
                    x=18;
                }
                if(y==19)
                {
                    y=18;
                }
                if(similar(Color.white,color,true))
                {
                    ++white_num[x][y];
                }
                else if(similar(Color.black,color,true))
                {
                    ++black_num[x][y];
                }
            }
        }
        int std=(int)(width*length/4+0.5);
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                if(white_num[i][j]>=std)
                {
                    status[i][j]=-1;
                }
                else if(black_num[i][j]>=std)
                {
                    status[i][j]=1;
                }
            }
        }
        chess_output(status);
        return status;
    }
    public static void main(String[]args)
    {
        System.out.println("欢迎来到蔡弈文的围棋转接器！\n" +
                "请不要最小化此对话框，可以将其放在对弈对话框之后。\n" +
                "请关闭所有杀毒软件和不必要的程序，否则转接器可能失灵。\n" +
                "对弈对话框尽量规避弹窗。\n" +
                "Ctrl+D - 鼠标放在GUI棋盘网格上（推荐天元和星，可放到初始子的黑色部分）\n" +
                "Ctrl+Alt+D - 第一次鼠标放到黑棋盘的左上角交叉点，第二次为右下角\n" +
                "Ctrl+B - 以GUI棋盘为黑棋盘开始游戏\n" +
                "Ctrl+W - 以GUI棋盘为白棋盘开始游戏\n" +
                "Ctrl+E - 结束转换\n" +
                "Ctrl+Alt+L - 读取根目录下的配置文件setting.txt\n" +
                "Ctrl+Alt+S - 将当前配置写入根目录下setting.txt\n" +
                "根目录下生成的文件：\n" +
                "chessboard.txt - 当前棋盘状况\n" +
                "Entries.txt - 当前局棋谱\n");
        GTP_vs_GUI automation=new GTP_vs_GUI(new PhoenixGoGTP());
        try
        {
            automation.robot=new Robot();
        }
        catch(AWTException e)
        {
            e.printStackTrace();
            return;
        }
        automation.dimension=Toolkit.getDefaultToolkit().getScreenSize();
        automation.rectangle=new Rectangle(automation.dimension);
        JIntellitype.getInstance().registerHotKey(1,JIntellitype.MOD_CONTROL,KeyEvent.VK_D);
        JIntellitype.getInstance().registerHotKey(2,JIntellitype.MOD_CONTROL,KeyEvent.VK_B);
        JIntellitype.getInstance().registerHotKey(3,JIntellitype.MOD_CONTROL,KeyEvent.VK_W);
        JIntellitype.getInstance().registerHotKey(4,JIntellitype.MOD_CONTROL,KeyEvent.VK_E);
        JIntellitype.getInstance().registerHotKey(5,JIntellitype.MOD_CONTROL+JIntellitype.MOD_ALT,KeyEvent.VK_D);
        JIntellitype.getInstance().registerHotKey(6,JIntellitype.MOD_CONTROL+JIntellitype.MOD_ALT,KeyEvent.VK_L);
        JIntellitype.getInstance().registerHotKey(7,JIntellitype.MOD_CONTROL+JIntellitype.MOD_ALT,KeyEvent.VK_S);
        JIntellitype.getInstance().registerHotKey(8,JIntellitype.MOD_CONTROL,KeyEvent.VK_T);
        JIntellitype.getInstance().addHotKeyListener(i->
        {
            switch(i)
            {
                case 1:
                    automation.confirmed=automation.distinguish(automation.x,automation.y);
                    automation.min_x=automation.x[0][0];
                    automation.min_y=automation.y[0][0];
                    automation.max_x=automation.x[18][18];
                    automation.max_y=automation.y[18][18];
                    break;
                case 2:
                    if(automation.end)
                    {
                        new Thread(()->automation.play_black()).start();
                    }
                    break;
                case 3:
                    if(automation.end)
                    {
                        new Thread(()->automation.play_white()).start();
                    }
                    break;
                case 4:
                    automation.end=true;
                    new Thread(()->
                    {
                        for(int i1=1;i1<9;++i1)
                        {
                            JIntellitype.getInstance().unregisterHotKey(i1);
                        }
                        JIntellitype.getInstance().cleanUp();
                    }).start();
                    automation.gtp.kill();
                    break;
                case 5:
                    automation.give_pos();
                    break;
                case 6:
                    automation.get_setting();
                    break;
                case 7:
                    automation.store_setting();
                    break;
                case 8:
                    automation.end=true;
            }
        });
    }
}