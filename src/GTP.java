import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public abstract class GTP
{
    Process process;
    InputStream output_stream;
    OutputStream input;
    BufferedReader output;
    PrintWriter in;
    Thread info_listener;
    Queue<String>info;
    public void get_info()
    {
        if(output==null)
        {
            System.err.println("The GTP has not been initialized.");
            return;
        }
        String information;
        while(true)
        {
            try
            {
                while((information=output.readLine())!=null)
                {
                    info.add(information);
                    //System.out.println(information);
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
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
    public void process_info(Queue<String>steps)
    {
        if(info==null)
        {
            System.err.println("The GTP has not been initialized.");
            return;
        }
        String information;
        while(true)
        {
            while(!info.isEmpty())
            {
                information=info.peek();
                info.poll();
                if(information.length()>2&&information.charAt(0)=='='&&information.charAt(1)==' ')
                {
                    steps.add(information.substring(2));
                }
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
    public void exe_command(String command)
    {
        if(in==null)
        {
            System.err.println("The GTP has not been initialized.");
            return;
        }
        System.out.println(command);
        in.println(command);
        in.flush();
    }
    public void init(List<String>list,int size)
    {
        try
        {
            ProcessBuilder processBuilder=new ProcessBuilder(list).redirectErrorStream(true);
            process=processBuilder.start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }
        output_stream=process.getInputStream();
        output=new BufferedReader(new InputStreamReader(output_stream));
        info=new LinkedList<>();
        info_listener=new Thread(()->get_info());
        info_listener.start();
        input=process.getOutputStream();
        in=new PrintWriter(input);
        exe_command("boardsize "+size);
        exe_command("clear_board");
    }
    public void kill()
    {
        if(info_listener!=null)
        {
            info_listener.stop();
        }
        if(in!=null)
        {
            in.close();
        }
        if(input!=null)
        {
            try
            {
                input.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        if(output!=null)
        {
            try
            {
                output.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        if(process!=null)
        {
            process.destroy();
        }
    }
}