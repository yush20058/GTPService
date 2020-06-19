import java.util.Arrays;
import java.util.List;
public class LeelaZeroGTP extends GTP
{
    public LeelaZeroGTP()
    {
        this.init(19);
    }
    public LeelaZeroGTP(int size)
    {
        this.init(size);
    }
    public void init(int size)
    {
        List<String> list= Arrays.asList("LeelaZero/leelaz.exe","-g","-w","LeelaZero/networks/86fa6e9897785c5583de41a5cef4132eacb167c85e68e0f0bd063b75ae15ca58.gz");
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
        LeelaZeroGTP gtp=new LeelaZeroGTP();
        //new Thread(()->leelaGTP.process_info()).start();
        gtp.run();
    }
}