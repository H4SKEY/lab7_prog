package org.example.commands;

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
    public String execute(String[] args, Object data) {
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

            Ticket updated = (Ticket) data;
            collectionManager.updateTicket(id, updated);
            result = "Элемент с ID " + id + " обновлен";
        } catch (NumberFormatException e) {
            result = "Неверный формат ID";
        }
        return result;
    }
}