import org.w3c.dom.ls.LSOutput;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

public class Game
{

    private Parser parser;
    private Room currentRoom;
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        playSound("Shire.wav");
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
        courtyard.setExits(new Room[]{lobby, cave, null, null, null, null});
        lobby.setExits(new Room[]{throneRoom, guestChamber, courtyard, fireside, landing, dungeon});
        cave.setExits(new Room[]{null, dungeon, null, courtyard, null, null});
        dungeon.setExits(new Room[]{null, null, null, cave, lobby, null});
        guestChamber.setExits(new Room[]{null, null, null, lobby, null, null});
        fireside.setExits(new Room[]{null, lobby, null, null, null, null});
        throneRoom.setExits(new Room[]{null, null, lobby, banquetHall, library, null});
        banquetHall.setExits(new Room[]{null, throneRoom, null, null, null, null});
        library.setExits(new Room[]{null, landing, null, null, null, throneRoom});
        landing.setExits(new Room[]{meetingRoom, kingChamber, null, library, null, lobby});
        kingChamber.setExits(new Room[]{null, null, null, landing, null, null});
        meetingRoom.setExits(new Room[]{null, null, landing, null, null, null});

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
        System.out.println();
        System.out.println("Welcome to the Wumpus World");
        System.out.println("Wumpus World is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("You are " + currentRoom.getDescription());
        showExits();
        System.out.println();
    }
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            if (command.hasSecondWord()) {
                Room next = chooseNextRoom(command);
                goRoom(next);
            }
            else
                System.out.println("Go where?");
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
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println("   go quit help");
    }

    private void goRoom(Room nextRoom)
    {
        // This function used to be made up of three parts. A check for a second word in the command,
        // a block that chose the next room, and a block moved you there. I put the first part when the command is called.
        // I have separated the other parts into the "chooseNextRoom", and "goRoom" methods for readability.

        // Move (if possible)
        if (nextRoom == null)
            System.out.println("There is no door!");
        else {
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
        for(int i =0; i < currentRoom.exits.length; i++){
            if(currentRoom.exits[i] != null) {
                switch (i) {
                    case 0:
                        exitMessage.append(" north");
                        break;
                    case 1:
                        exitMessage.append(" east");
                        break;
                    case 2:
                        exitMessage.append(" south");
                        break;
                    case 3:
                        exitMessage.append(" west");
                        break;
                    case 4:
                        exitMessage.append(" up");
                        break;
                    case 5:
                        exitMessage.append(" down");
                        break;
                    default:
                        System.out.print(" You shouldn't be seeing this");
                        break;
                }
            }
        }
        System.out.println(exitMessage);
    }
    public Room chooseNextRoom(Command command){
        String direction = command.getSecondWord();

        // Determines direction to move (if direction is valid).
        Room nextRoom = null;
        if(direction.equals("north"))
            nextRoom = currentRoom.exits[0];
        else if(direction.equals("east"))
            nextRoom = currentRoom.exits[1];
        else if(direction.equals("south"))
            nextRoom = currentRoom.exits[2];
        else if(direction.equals("west"))
            nextRoom = currentRoom.exits[3];
        else if(direction.equals("up"))
            nextRoom = currentRoom.exits[4];
        else if(direction.equals("down"))
            nextRoom = currentRoom.exits[5];
        return nextRoom;
    }
    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("/" + url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }


}
