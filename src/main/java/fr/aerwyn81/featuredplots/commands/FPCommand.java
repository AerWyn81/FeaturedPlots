package fr.aerwyn81.featuredplots.commands;

public class FPCommand {
    private final Cmd cmdClass;
    private final String command;
    private final String permission;
    private final boolean isPlayerCommand;
    private final String[] args;
    private final boolean visible;

    public FPCommand(Cmd command) {
        this.cmdClass = command;
        this.command = cmdClass.getClass().getAnnotation(FPAnnotations.class).command();
        this.permission = cmdClass.getClass().getAnnotation(FPAnnotations.class).permission();
        this.isPlayerCommand = cmdClass.getClass().getAnnotation(FPAnnotations.class).isPlayerCommand();
        this.args = cmdClass.getClass().getAnnotation(FPAnnotations.class).args();
        this.visible = cmdClass.getClass().getAnnotation(FPAnnotations.class).isVisible();
    }

    public Cmd getCmdClass() {
        return cmdClass;
    }

    public String getCommand() {
        return command;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerCommand() {
        return isPlayerCommand;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isVisible() {
        return visible;
    }
}
