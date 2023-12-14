import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneBook {

    private static final String DATA_PATH = "src/contacts.dat";
    private static void saveContacts(Map<String, List<String>> contacts) {
        try (PrintWriter writer = new PrintWriter(DATA_PATH)) {
            if (!contacts.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : contacts.entrySet()) {
                    String line = String.format("%s,\"%s\"",
                            entry.getKey(), entry.getValue().toString().replaceAll("\\[|]", ""));
                    writer.println(line);
                }
            }

        } catch (IOException ioex) {
            System.err.println(ioex.getMessage());
        }
    }

    private static void loadContacts(Map<String, List<String>> contacts) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH))) {

            Pattern pattern = Pattern.compile("^([^,\"]{2,50}),\"([0-9+, ]+)\"$");

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] numbers = matcher.group(2).split(",\\s*");
                    contacts.put(matcher.group(1), Arrays.asList(numbers));
                }
            }

        } catch (IOException ioex) {
            System.err.println("Не удалось загрузить контакты, телефонная книга пуста!");
        }
    }

    private static void listCommands() {
        System.out.println("list   - перечисляет все сохраненные контакты в алфавитном порядке");
        System.out.println("show   - ищет контакт по имени");
        System.out.println("find   - ищет контакт по номеру");
        System.out.println("add    - сохраняет запись о новом контакте в телефонной книге");
        System.out.println("edit   - изменяет существующий контакт");
        System.out.println("delete - удаляет контакт из телефонной книги");
        System.out.println("help   - список всех команд");
        System.out.println("---------------------------");
    }

    private static void listContacts(Map<String, List<String>> contacts) {
        if (!contacts.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : contacts.entrySet()) {
                System.out.println(entry.getKey());
                for (String number : entry.getValue()) {
                    System.out.println(number);
                }
                System.out.println();
            }
        } else {
            System.out.println("Нет записей, телефонная книга пуста!");
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    private static void showContact(Map<String, List<String>> contacts, Scanner input) {
        System.out.println("Введите имя, которое вы ищете:");
        String name = input.nextLine().trim();

        if (contacts.containsKey(name)) {
            System.out.println(name);
            for (String number : contacts.get(name)) {
                System.out.println(number);
            }
        } else {
            System.out.println("Извините, ничего не найдено!");
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    private static void findContact(Map<String, List<String>> contacts, Scanner input) {
        System.out.println("Введите номер, чтобы узнать, кому он принадлежит:");
        String number = input.nextLine().trim();

        while (!number.matches("^\\+?[0-9 ]{3,25}$")) {
            System.out.println("Недопустимый номер! Может содержать только цифры, пробелы и '+'. Минимальная длина 3, максимальная длина 25.");
            System.out.println("Введите номер:");
            number = input.nextLine().trim();
        }

        for (Map.Entry<String, List<String>> entry : contacts.entrySet()) {
            if (entry.getValue().contains(number)) {
                System.out.println(entry.getKey());
                System.out.println(number);
            }
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    private static void addContact(Map<String, List<String>> contacts, Scanner input) {
        System.out.println("Вы собираетесь добавить новый контакт в телефонную книгу.");
        String name;
        String number;

        while (true) {
            System.out.println("Введите имя контакта:");
            name = input.nextLine().trim();
            if (name.matches("^.{2,50}$")) {
                break;
            } else {
                System.out.println("Имя должно содержать от 2 до 50 символов.");
            }
        }

        while (true) {
            System.out.println("Введите номер контакта:");
            number = input.nextLine().trim();
            if (number.matches("^\\+?[0-9 ]{3,25}$")) {
                break;
            } else {
                System.out.println("Номер может содержать только '+', пробелы и цифры. Минимальная длина 3, максимальная длина 25.");
            }
        }

        if (contacts.containsKey(name)) {
            System.out.printf("'%s' уже существует в телефонной книге!\n", name);

            if (contacts.get(name).contains(number)) {
                System.out.printf("Номер %s уже доступен для контакта '%s'.\n", number, name);
            } else {
                contacts.get(name).add(number);
                saveContacts(contacts);
                System.out.printf("Номер %s успешно добавлен для контакта '%s'.\n", number, name);
            }

        } else {
            List<String> numbers = new ArrayList<>();
            numbers.add(number);
            contacts.put(name, numbers);
            saveContacts(contacts);
            System.out.printf("Контакт '%s' успешно добавлен!\n", name);
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    private static void editContact(Map<String, List<String>> contacts, Scanner input) {
        System.out.println("Введите имя контакта, который вы хотите изменить:");
        String name = input.nextLine().trim();

        if (contacts.containsKey(name)) {
            List<String> numbers = new ArrayList<>(contacts.get(name));
            System.out.printf("Текущий(е) номер(а) для %s:\n", name);
            for (String number : numbers) {
                System.out.println(number);
            }
            System.out.println();
            System.out.println("Хотели бы вы добавить новый номер или удалить существующий номер для этого контакта? [add/delete/cancel]");
            String editOption = input.nextLine().trim().toLowerCase();
            boolean addNumber = false;
            boolean delNumber = false;

            option:
            while (true) {
                switch (editOption) {
                    case "add":
                        addNumber = true;
                        break option;
                    case "delete":
                        delNumber = true;
                        break option;
                    case "cancel":
                        System.out.println("Контакт не был изменен!");
                        break option;
                    default:
                        System.out.println("Используйте 'add', чтобы сохранить новый номер, 'delete', чтобы удалить существующий номер, или 'cancel', чтобы вернуться назад.");
                        editOption = input.nextLine().trim().toLowerCase();
                        break;
                }
            }

            if (addNumber) {
                while (true) {
                    System.out.println("Введите новый номер:");
                    String number = input.nextLine().trim();
                    if (number.matches("^\\+?[0-9 ]{3,25}$")) {
                        contacts.get(name).add(number);
                        saveContacts(contacts);
                        System.out.printf("Номер %s успешно добавлен, запись обновлена!\n", number);
                        break;
                    } else {
                        System.out.println("Номер может содержать только '+', пробелы и цифры. Минимальная длина 3, максимальная длина 25.");
                    }
                }
            }

            if (delNumber) {
                while (true) {
                    System.out.println("Введите номер, который вы хотите удалить:");
                    String number = input.nextLine().trim();
                    if (numbers.contains(number)) {
                        numbers.remove(number);
                        contacts.put(name, numbers);
                        saveContacts(contacts);
                        System.out.printf("Номер %s был удален из записи для '%s'\n", number, name);
                        break;
                    } else {
                        System.out.printf("Номер не существует! Текущий(е) номер(а) для %s:\n", name);
                        for (String num : numbers) {
                            System.out.println(num);
                        }
                    }
                }
            }

        } else {
            System.out.println("Извините, имя не найдено!");
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    private static void deleteContact(Map<String, List<String>> contacts, Scanner input) {
        System.out.println("Введите имя контакта, который должен быть удален:");
        String name = input.nextLine().trim();

        if (contacts.containsKey(name)) {
            System.out.printf("Контакт '%s' будет удален. Вы уверены? [Y/N]:\n", name);
            String confirmation = input.nextLine().trim().toLowerCase();
            confirm:
            while (true) {
                switch (confirmation) {
                    case "y":
                        contacts.remove(name);
                        saveContacts(contacts);
                        System.out.println("Контакт успешно удален!");
                        break confirm;
                    case "n":
                        break confirm;
                    default:
                        System.out.println("Удалить контакт? [Y/N]:");
                        break;
                }
                confirmation = input.nextLine().trim().toLowerCase();
            }

        } else {
            System.out.println("Извините, имя не найдено!");
        }

        System.out.println();
        System.out.println("Введите команду или 'exit', чтобы выйти. Для просмотра списка доступных команд используйте 'help':");
    }

    public static void main(String[] args) {

        System.out.println("ТЕЛЕФОННАЯ КНИГА");
        System.out.println("===========================");
        System.out.println("Введите команду или 'exit', чтобы выйти:");
        listCommands();
        System.out.print("> ");

        Map<String, List<String>> contacts = new TreeMap<>();
        loadContacts(contacts);

        Scanner input = new Scanner(System.in);
        String line = input.nextLine().trim();

        while (!line.equals("exit")) {

            switch (line) {
                case "list":
                    listContacts(contacts);
                    break;
                case "show":
                    showContact(contacts, input);
                    break;
                case "find":
                    findContact(contacts, input);
                    break;
                case "add":
                    addContact(contacts, input);
                    break;
                case "edit":
                    editContact(contacts, input);
                    break;
                case "delete":
                    deleteContact(contacts, input);
                    break;
                case "help":
                    listCommands();
                    break;
                default:
                    System.out.println("Неверная команда!");
                    break;
            }


            System.out.print("\n> ");
            line = input.nextLine().trim();
        }

        System.out.println("'Телефонная книга' закрыта.");
    }
}