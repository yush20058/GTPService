import javafx.util.Pair;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
public class GTP_vs_GTP extends JFrame
{
    GTP black_gtp,white_gtp;
    GibWriter gibWriter;
    Chessboard chessboard=new Chessboard();
    Queue<String>steps=new LinkedList<>();
    final int stars[][]={{9,9},{3,3},{15,3},{3,15},{15,15},{15,9},{9,15},{3,9},{9,3}};
    int length=700,upper_length=length/10;
    double arc;
    public GTP_vs_GTP(GTP black,GTP white)
    {
        super("GTP vs GTP");
        setVisible(true);
        setSize(length,length+upper_length);
        arc=length/19.0;
        setLocationRelativeTo(null);
        setResizable(false);
        black_gtp=black;
        white_gtp=white;
    }
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        paint_chessboard();
    }
    public void paint_chessboard()
    {
        Graphics graphics=getGraphics();
        graphics.setColor(Color.orange);
        graphics.fillRect(0,upper_length,length,length);
        graphics.setColor(Color.black);
        for(int i=0;i<19;++i)
        {
            int start_x=(int)(arc/2.0+i*arc+0.5);
            int start_y=(int)(arc/2.0+0.5);
            int end_x=start_x;
            int end_y=(int)(length-arc/2.0+0.5);
            graphics.drawLine(start_x,upper_length+start_y,end_x,upper_length+end_y);
            graphics.drawLine(start_y,upper_length+start_x,end_y,upper_length+end_x);
        }
        int radical=(int)(arc/3.0+0.5);
        for(int i=0;i<stars.length;++i)
        {
            int x=(int)(arc/2.0+stars[i][0]*arc+0.5)-(int)(radical/2.0+0.5);
            int y=(int)(arc/2.0+stars[i][1]*arc+0.5)-(int)(radical/2.0+0.5);
            graphics.fillOval(x,upper_length+y,radical,radical);
        }
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                if(chessboard.status[i][j]!=0)
                {
                    int x=(int)(arc/2.0+i*arc+0.5)-(int)(arc/2+0.5);
                    int y=(int)(arc/2.0+j*arc+0.5)-(int)(arc/2+0.5);
                    if(chessboard.status[i][j]==1)
                    {
                        graphics.setColor(Color.black);
                    }
                    else
                    {
                        graphics.setColor(Color.white);
                    }
                    graphics.fillOval(x,upper_length+y,(int)arc,(int)arc);
                }
            }
        }
    }
    public void paint_sign(int i,int j)
    {
        int radical=(int)(arc/3.0+0.5);
        Graphics graphics=getGraphics();
        int x=(int)(arc/2.0+i*arc+0.5)-(int)(radical/2.0+0.5);
        int y=(int)(arc/2.0+j*arc+0.5)-(int)(radical/2.0+0.5);
        graphics.setColor(Color.red);
        graphics.fillOval(x,upper_length+y,radical,radical);
    }
    public Pair<Integer,Integer>get_index(String step)
    {
        return new Pair<>(step.charAt(0)>='I'?step.charAt(0)-'A'-1:step.charAt(0)-'A',19-(new Integer(step.substring(1))));
    }
    public void move(String step,boolean black_or_white)
    {
        GTP gtp=(black_or_white?white_gtp:black_gtp);
        String modifier=(black_or_white?"black":"white");
        gtp.exe_command("play "+modifier+" "+step);
        System.err.println(modifier+" moves at "+step);
        Pair<Integer,Integer>index=get_index(step);
        gibWriter.add_step(index);
        chessboard.move(index);
        paint_chessboard();
        paint_sign(index.getKey(),index.getValue());
    }
    public void play_in_turns()
    {
        LocalTime start_time=LocalTime.now();
        gibWriter=new GibWriter();
        int pass_num=0;
        boolean resign=false;
        while(pass_num<2&&!resign)
        {
            black_gtp.exe_command("genmove black");
            while(true)
            {
                if(!steps.isEmpty())
                {
                    if(steps.peek().equalsIgnoreCase("PASS"))
                    {
                        ++pass_num;
                        steps.poll();
                        gibWriter.add_step(new Pair<>(-1,-1));
                        System.err.println("black PASS");
                        break;
                    }
                    else if(steps.peek().equalsIgnoreCase("resign"))
                    {
                        resign=true;
                        gibWriter.result="black resign";
                        System.err.println("black resign");
                        break;
                    }
                    pass_num=0;
                    move(steps.peek(),true);
                    steps.poll();
                    break;
                }
                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            if(pass_num<2&&!resign)
            {
                white_gtp.exe_command("genmove white");
            }
            while(pass_num<2&&!resign)
            {
                if(!steps.isEmpty())
                {
                    if(steps.peek().equalsIgnoreCase("PASS"))
                    {
                        ++pass_num;
                        steps.poll();
                        gibWriter.add_step(new Pair<>(-1,-1));
                        System.err.println("white PASS");
                        break;
                    }
                    else if(steps.peek().equalsIgnoreCase("resign"))
                    {
                        resign=true;
                        gibWriter.result="white resign";
                        System.err.println("white resign");
                        break;
                    }
                    pass_num=0;
                    move(steps.peek(),false);
                    steps.poll();
                    break;
                }
                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        LocalTime end_time=LocalTime.now();
        paint_chessboard();
        LocalDate date=LocalDate.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        gibWriter.date=formatter.format(date);
        formatter=DateTimeFormatter.ofPattern("HH:mm:ss");
        gibWriter.game_time=formatter.format(start_time)+" - "+formatter.format(end_time);
        gibWriter.black_name=black_gtp.getClass().getSimpleName();
        gibWriter.white_name=white_gtp.getClass().getSimpleName();
        if(gibWriter.result.equalsIgnoreCase(""))
        {
            gibWriter.result="unknown";
        }
        gibWriter.print_gib(new File(gibWriter.black_name+"_vs_"+gibWriter.white_name+"_"+gibWriter.date+"_"+formatter.format(end_time).replace(':','-')+".gib"));
    }
    public void play()
    {
        new Thread(()->black_gtp.process_info(steps)).start();
        new Thread(()->white_gtp.process_info(steps)).start();
        new Thread(()->play_in_turns()).start();
    }
    public static void main(String[]args)
    {
        GTP_vs_GTP player=new GTP_vs_GTP(new LeelaGTP(),new LeelaZeroGTP());
        player.play();
    }
}