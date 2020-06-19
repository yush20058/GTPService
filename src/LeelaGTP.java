import java.util.Arrays;
import java.util.List;
public class LeelaGTP extends GTP
{
    public LeelaGTP()
    {
        this.init(19);
    }
    public LeelaGTP(int size)
    {
        this.init(size);
    }
    public void init(int size)
    {
        List<String>list=Arrays.asList("Leela/Leela0110GTP.exe","-g");
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
        LeelaGTP leelaGTP=new LeelaGTP();
        //new Thread(()->leelaGTP.process_info()).start();
        leelaGTP.run();
    }
}