package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.data.Ticket;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда обновления элемента по ID
 */
public class UpdateCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 116L;

    public UpdateCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "update id - обновить элемент по ID";
    }

    @Override
    public String execute(Request request) {
        String[] args = request.getArgs();
        if (args.length < 1) {
            return "Не указан ID элемента";
        }
        String result;
        try {
            int id = Integer.parseInt(args[0]);
            Ticket existing = collectionManager.getTickets().stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                return "Элемент с ID " + id + " не найден";
            }

            Ticket updated = (Ticket) request.getData();
            collectionManager.updateTicket(id, updated, request.getUser());
            result = "Элемент с ID " + id + " обновлен";
        } catch (NumberFormatException e) {
            result = "Неверный формат ID";
        }
        return result;
    }
}