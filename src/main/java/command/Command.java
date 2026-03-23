package command;

import request.Request;
import request.Response;

public interface Command {
    Response execute(Request request);
    String getDescription();
    String getName();
}