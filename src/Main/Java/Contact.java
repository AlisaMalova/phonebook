package Main;

import java.io.Serializable;
import java.util.List;

public class Contact implements Serializable {

    private String name;
    private String number;

    public Contact(String name, String number) {
        try {
            setName(name);
            setNumber(number);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws Exception {
        if (!name.matches("^.{2,50}$")) {
            throw new Exception("Имя должно быть в длину от 2 до 50 символов.");
        } else {
            this.name = name;
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) throws Exception {
        if (!number.matches("^\\+?[0-9 ]{3,25}$")) {
            throw new Exception("Число может содержать только '+', пробелы и цифры. Минимальная длина 3, максимальная длина 25.");
        } else {
            this.number = number;
        }
    }

    @Override
    public String toString() {
        return this.name + " : " + this.number;
    }

    // Возвращает значение true, если имя контакта уже присутствует в заданном списке контактов
    public boolean exists(List<Contact> contacts) {
        for (Contact entry : contacts) {
            if (entry.getName().equals(this.name)) {
                return true;
            }
        }
        return false;
    }

}
