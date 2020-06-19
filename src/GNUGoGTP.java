import java.util.Arrays;
import java.util.List;
public class GNUGoGTP extends GTP
{
    public GNUGoGTP()
    {
        init(19);
    }
    public GNUGoGTP(int size)
    {
        init(size);
    }
    public void init(int size)
    {
        List<String> list=Arrays.asList("GNUGo/gnugo.exe","--mode","gtp");
        init(list,size);
    }
    public void run()
    {
        exe_command("play black C13");
        exe_command("showboard");
        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        exe_command("genmove black");
    }
    public static void main(String[]args)
    {
        GNUGoGTP gtp=new GNUGoGTP();
        //new Thread(()->leelaGTP.process_info()).start();
        gtp.run();
    }
}