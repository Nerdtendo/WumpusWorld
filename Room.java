import java.util.HashMap;

public class Room
{
    protected String description;
    HashMap<String, Room> exits = new HashMap<String, Room>();

    protected Room(String description)
    {
        this.description = description;
    }
    protected String musicTrack;

    public void setExits(String[] s, Room[] r)
    {
        for (int i = 0; i < s.length; i++){
            exits.put(s[i], r[i]);
        }
    }

    public String getDescription()
    {
        return description;
    }
    public void setMusicTrack(String track){musicTrack=track;}

    public String getMusicTrack(){return musicTrack;}
}
