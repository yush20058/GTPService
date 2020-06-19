import java.util.Arrays;
import java.util.List;
public class PhoenixGoGTP extends GTP
{
    public PhoenixGoGTP()
    {
        this.init(19);
    }
    public PhoenixGoGTP(int size)
    {
        this.init(size);
    }
    public void init(int size)
    {
        List<String>list=Arrays.asList("PhoenixGo/start.bat");
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
        exe_command("genmove white");
    }
    public static void main(String[]args)
    {
        PhoenixGoGTP gtp=new PhoenixGoGTP();
        //new Thread(()->leelaGTP.process_info()).start();
        gtp.run();
    }
}