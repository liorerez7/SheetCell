package CoreParts.api.controller;

import java.util.List;

public interface Command {
    void execute(List<String> inputForCommand) throws Exception;
}
