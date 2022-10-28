public class Room
{
    protected String description;
    protected Room[] exits = new Room[6];//You should probably make this ArrayList later if you want to have the player "discover" new exits (i.e. examined bookshelf in library and found hidden stairwell down could be easily achieved by replacing null in that direction using set
    protected Room(String description)
    {
        this.description = description;
    }

    public void setExits(Room[] rooms)
    {
        for (int i = 0; i < rooms.length; i++){
            exits[i] = (rooms[i] != null) ? rooms[i] : null;
        }
    }

    public String getDescription()
    {
        return description;
    }

}
