import java.util.Scanner;

public class Parser
{
    private CommandWords commands;  // holds all valid command words
    private Scanner reader;         // source of command input

    public Parser()
    {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    //getCommand() now uses the String method "split" to greatly simplify this function.
    public Command getCommand()
    {
        String inputLine = reader.nextLine();   // will hold the full input line

        System.out.print("> ");
        String[] words = inputLine.toLowerCase().split(" ");

        // Now check whether this word is known. If so, create a command
        // with it. If not, create a "null" command (for unknown command).
        if(commands.isCommand(words[0])) {
            if (words.length < 2)
                return new Command(words[0], null);
            return new Command(words[0], words[1]);
        }
        else
            return new Command(null, null);
    }
}
