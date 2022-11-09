import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;

public class Game
{
    Music currentSong = new Music();
    Music nextSong = new Music();
    private Parser parser;
    private Room currentRoom;
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    private void createRooms()
    {
        Room courtyard, lobby, cave, dungeon, guestChamber, fireside, throneRoom, banquetHall, library, landing, kingChamber, meetingRoom;
      
        // create the rooms
        courtyard = new Room("in a courtyard. In front of you looms a foreboding castle.");
        lobby = new Room("in a grand entrance hall. There is an ornate stairway as well as rooms in every direction.");
        cave = new Room("in a cave. You can't see anything, but you may be able to find your way by feeling around.");
        dungeon = new Room("in a rotting dungeon. Abandoned cells surround you. Why are those stones so loose?");
        guestChamber = new Room("in some sort of furnished room. It looks like this was used to welcome guests.");
        fireside = new Room("in a comfortable looking room. There is an unlit fireplace to the side. Under different circumstances, you might enjoy spending time here.");
        throneRoom = new Room("in a grand room. At the end of a long carpet lies a dilapidated throne. What happened here?");
        banquetHall = new Room("in a room with a long dining table that seems to stretch forever. This must be a banquet hall.");
        library = new Room("in a beautiful library. There are rows and rows of dusty bookshelves. Clearly, nobody has been here for a while.");
        landing = new Room("You are on a landing for the second floor of the castle. You can see the entrance hall at the bottom of the stairs.");
        kingChamber = new Room("in a rather large bedchamber. There is a intricately designed bed central to the room. It must have cost a small fortune.");
        meetingRoom = new Room("in a surprisingly empty room. The only thing of note is a large round table in the middle. Perhaps this was used as a  meeting room.");

        // initialise room exits
        courtyard.exits.put("north", cave);
        courtyard.setExits(new String[]{"north", "east"}, new Room[]{lobby, cave});
        lobby.setExits(new String[]{"north", "east", "south", "west", "up", "down"}, new Room[]{throneRoom, guestChamber, courtyard, fireside, landing, dungeon});
        cave.setExits(new String[]{"east", "west"} ,new Room[]{dungeon, courtyard});
        dungeon.setExits(new String[]{"west", "up"}, new Room[]{cave, lobby});
        guestChamber.setExits(new String[]{"west"}, new Room[]{lobby});
        fireside.setExits(new String[]{"east"}, new Room[]{lobby});
        throneRoom.setExits(new String[]{"south", "west", "up"}, new Room[]{lobby, banquetHall, library});
        banquetHall.setExits(new String[]{"east"}, new Room[]{throneRoom});
        library.setExits(new String[]{"east", "down"}, new Room[]{landing, throneRoom});
        landing.setExits(new String[]{"north", "east", "west"}, new Room[]{meetingRoom, kingChamber, library, lobby});
        kingChamber.setExits(new String[]{"west"}, new Room[]{landing});
        meetingRoom.setExits(new String[]{"south"}, new Room[]{landing});

        courtyard.setMusicTrack("Sound/Music/1-01 - Overture.wav");
        lobby.setMusicTrack("Sound/Music/1-02 - The Star Festival.wav");
        cave.setMusicTrack("Sound/Music/1-03 - Attack of the Airships.wav");
        dungeon.setMusicTrack("Sound/Music/1-04 - Catastrophe.wav");
        guestChamber.setMusicTrack("Sound/Music/1-05 - Peach's Castle Stolen.wav");
        fireside.setMusicTrack("Sound/Music/1-06 - Enter the Galaxy.wav");
        throneRoom.setMusicTrack("Sound/Music/Shire.wav");

        currentRoom = courtyard;  // start game in the courtyard
    }

    /**
      *  Main play routine.  Loops until end of play.
      */

    public void play()
    {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.


        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        currentSong.playSound(currentRoom.getMusicTrack(), true, .5f);
        System.out.println();
        System.out.println("Welcome to the Wumpus World");
        System.out.println("Wumpus World is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("You are " + currentRoom.getDescription());
        showExits();
        System.out.println();
        try {
            Thread.sleep(100);
        } catch(InterruptedException e){}
    }
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            String commandWord = command.getCommandWord();
            System.out.println("I don't know what you mean...");
            return false;
        }
        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(chooseNextRoom(command), command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        return wantToQuit;
    }

    // implementations of user commands:

    private void printHelp()
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the abandoned castle grounds.");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println("   go quit help");
    }

    private void goRoom(Room nextRoom, Command command)
    {
        // This function used to be made up of three parts. A check for a second word in the command,
        // a block that chose the next room, and a block moved you there. I put the first part when the command is called.
        // I have separated the other parts into the "chooseNextRoom", and "goRoom" methods for readability.

        // Move (if possible)
        if (nextRoom == null)
            System.out.println("There is no door!");
        else if (!command.hasSecondWord())
            System.out.println("Go where?");
        else {
            nextSong.playSound(nextRoom.getMusicTrack(), true, 0);
            while (nextSong.currentTrack==null) {
                try {
                    if (nextSong.currentTrack == null)
                        Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            Music.mixTracks(currentSong, nextSong);
            currentRoom = nextRoom;
            System.out.println("You are " + currentRoom.getDescription());
            showExits();
        }


    }

    private boolean quit(Command command)
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }

    public void showExits(){
        StringBuilder exitMessage = new StringBuilder("Exits:");
        for (String direction : currentRoom.exits.keySet()){
            exitMessage.append(" "+ direction);
        }
        System.out.println(exitMessage);
    }
    public Room chooseNextRoom(Command command){
        String direction = command.getSecondWord();

        // Determines direction to move (if direction is valid).
        Room nextRoom = null;
        if (currentRoom.exits.get(direction)!=null) {
            nextRoom=currentRoom.exits.get(direction);
        }
        return nextRoom;
    }

}
