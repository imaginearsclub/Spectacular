package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public class CommandAction extends ShowAction {
    private static final int MIN_ARGS = 3;
    private final String command;

    /**
     * Constructs a CommandAction that represents an action to execute a specified command.
     *
     * @param show the show instance this action is associated with. Must not be null.
     * @param time the time in milliseconds at which to execute the action.
     * @param command the command string to be executed. Cannot be null or empty.
     * @throws NullPointerException if the command is null.
     * @throws IllegalArgumentException if the command is empty.
     */
    public CommandAction(Show show, long time, String command) {
        super(show, time);
        this.command = Objects.requireNonNull(command, "Command cannot be null").trim();
        if (this.command.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }
    }

    /**
     * Executes a predefined command associated with this action.
     *
     * @param nearPlayers An array of players near the action. This parameter is not used in this implementation.
     */
    @Override
    public void play(Player[] nearPlayers) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    /**
     * Loads a new instance of {@link ShowAction} based on the input parameters.
     *
     * @param line the raw input line representing the action data.
     * @param args the array of arguments parsed from the input line.
     *             Must contain at least {@code MIN_ARGS} elements.
     * @return a constructed {@link CommandAction} initialized with the processed data.
     * @throws ShowParseException if the input line or arguments are invalid,
     *                            or if the required number of arguments are not provided.
     */
    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args == null || args.length < MIN_ARGS) {
            throw new ShowParseException("Invalid input: " + line + ". Expected at least " + MIN_ARGS + " arguments, got: " + Arrays.toString(args));
        }
        String commandFromArgs = getCommandFromArgs(args);
        return new CommandAction(getShow(), getTime(), commandFromArgs);
    }

    /**
     * Creates a copy of the {@code ShowAction} with the specified {@code Show} instance and execution time.
     *
     * @param show the {@code Show} instance associated with the copied action. Must not be null.
     * @param time the time in milliseconds at which the copied action will execute.
     * @return a new {@code CommandAction} instance with the same command string,
     *         but associated with the given {@code Show} and execution time.
     * @throws ShowParseException if copying the action fails due to an invalid show or other issues.
     */
    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new CommandAction(show, time, command);
    }

    /**
     * Extracts a command string from the provided arguments array, starting from the third element
     * and concatenating the remaining elements with a space delimiter.
     *
     * @param args the array of command arguments. Must contain at least three elements, where the actual
     *             command string begins at index 2.
     * @return a single concatenated command string derived from the arguments starting at index 2.
     */
    // Extracted logic for argument processing
    private String getCommandFromArgs(String[] args) {
        return String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    }
}