import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
public class GibWriter
{
    String game_name="";
    String date="";
    String game_place="";
    String game_time="";
    String white_name="";
    String white_level="";
    String black_name="";
    String black_level="";
    String result="";
    int tot_num=0;
    String comment="";
    final String model="\\HS\n" +
            "\\[TYPE=0\\]\n" +
            "\\[SZAUDIO=0\\]\n" +
            "\\[GAMENAME=game_name\\]\n" +
            "\\[GAMEDATE=date\\]\n" +
            "\\[GAMEPLACE=game_place\\]\n" +
            "\\[GAMECONDITION=\\]\n" +
            "\\[GAMETIME=game_time\\]\n" +
            "\\[GAMEWHITENAME=white_name(white_level)\\]\n" +
            "\\[GAMEBLACKNAME=black_name(black_level)\\]\n" +
            "\\[GAMERESULT=result\\]\n" +
            "\\[GAMETOTALNUM=tot_num\\]\n" +
            "\\[GAMECOMMENT=comment\\]\n" +
            "\\[GAMEWHITENICK=\\]\n" +
            "\\[GAMEWHITECOUNTRY=\\]\n" +
            "\\[GAMEBLACKNICK=\\]\n" +
            "\\[GAMEBLACKCOUNTRY=\\]\n" +
            "\\[GAMETAG=,I:white_name,M:black_name\\]\n" +
            "\\HE\n" +
            "\\GS\n" +
            "content" +
            "\\GE\n";
    final String content_model="STO 0 0 player x_pos y_pos";
    LinkedList<Pair<Integer,Integer>>steps=new LinkedList<>();
    public void add_step(Pair<Integer,Integer>index)
    {
        steps.add(index);
        ++tot_num;
    }
    public void print_gib(File file)
    {
        String content="";
        boolean black_or_white=true;
        for(Pair<Integer,Integer>p:steps)
        {
            content+=content_model.replace("player",black_or_white?"1":"2").replace("x_pos",p.getKey().toString()).replace("y_pos",p.getValue().toString());
            content+='\n';
            black_or_white=!black_or_white;
        }
        String output=model;
        output=output.replace("game_name",game_name);
        output=output.replace("date",date);
        output=output.replace("game_place",game_place);
        output=output.replace("game_time",game_time);
        output=output.replace("white_name",white_name);
        output=output.replace("white_level",white_level);
        output=output.replace("black_name",black_name);
        output=output.replace("black_level",black_level);
        output=output.replace("result",result);
        output=output.replace("tot_num",String.valueOf(tot_num));
        output=output.replace("comment",comment);
        output=output.replace("content",content);
        PrintWriter out=null;
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            out=new PrintWriter(fileOutputStream);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            System.err.println("Cannot write the gib file.");
            System.err.println("Gib file content:\n"+output);
            return;
        }
        out.print(output);
        out.close();
    }
}