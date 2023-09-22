package bubzki.vcard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactCard {
    private String fn;
    private String org;
    private Date bday;
    private final Map<String, String> tel = new HashMap<>();
    private enum g {M, F};
    private g gender;

    public ContactCard getInstance(Scanner scanner) throws NoSuchElementException {
        scanner.useDelimiter("\r\n");
        boolean requiredField_FN = false;
        boolean requiredField_ORG = false;
        boolean requiredField_END = false;
        if (scanner.nextLine().equals("BEGIN:VCARD")) {
            while (scanner.hasNextLine() || !requiredField_END) {
                StringBuilder s = new StringBuilder(scanner.nextLine());
                switch (s.substring(0, s.indexOf(":") + 1)) {
                    case "FN:":
                        fn = s.substring(s.indexOf(":") + 1);
                        requiredField_FN = true;
                        break;
                    case "ORG:":
                        org = s.substring(s.indexOf(":") + 1);
                        requiredField_ORG = true;
                    break;
                    case "GENDER:":
                        if (g.M.toString().equals(s.substring(s.indexOf(":") + 1))) {
                            gender = g.M;
                        } else if (g.F.toString().equals(s.substring(s.indexOf(":") + 1))) {
                            gender = g.F;
                        } else {
                            throw new InputMismatchException("Illegal \"GENDER\" format.");
                        }
                        break;
                    case "BDAY:":
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            bday = dateFormat.parse(s.substring(s.indexOf(":") + 1));
                        } catch (ParseException e) {
                            throw new InputMismatchException("Illegal \"BDAY\" format.");
                        }
                        break;
                    case "END:":
                        if ("VCARD".equals(s.substring(s.indexOf(":") + 1))) {
                            requiredField_END = true;
                        } else {
                            throw new InputMismatchException("Illegal \"END\" format.");
                        }
                        break;
                    default:
                        Pattern pattern = Pattern.compile("TEL;TYPE=[a-zA-Z]+:[0-9]{10}");
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.matches()) {
                            tel.put(s.substring(s.indexOf("=") + 1, s.indexOf(":")), s.substring(s.indexOf(":") + 1));
                        } else {
                            throw new InputMismatchException("Illegal argument.");
                        }
                }
            }
        } else {
            scanner.close();
            throw new NoSuchElementException();
        }
        if (!requiredField_FN || !requiredField_ORG || !requiredField_END) {
            scanner.close();
            throw new NoSuchElementException();
        }
        scanner.close();
        return this;
    }

    public ContactCard getInstance(String data) {
        Scanner scanner = new Scanner(data);
        scanner.useDelimiter("\r\n");
        return this.getInstance(scanner);
    }

    public String getFullName() {
        return fn;
    }

    public String getOrganization() {
        return org;
    }

    public boolean isWoman() {
        return gender == g.F;
    }

    public Calendar getBirthday() {
        if (bday != null) {
            Calendar birthday = Calendar.getInstance();
            birthday.setTime(bday);
            return birthday;
        } else {
            throw new NoSuchElementException();
        }
    }

    public Period getAge() {
        if (bday != null) {
            //return Period.between(LocalDate.ofInstant(bday.toInstant(), ZoneId.systemDefault()), LocalDate.now());
            return Period.between(bday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now());
        } else {
            throw new NoSuchElementException();
        }
    }

    public int getAgeYears() {
        if (bday != null) {
            return getAge().getYears();
        } else {
            throw new NoSuchElementException();
        }
    }

    public String getPhone(String type) {
        for (Map.Entry<String, String> entry : tel.entrySet()) {
            if (type.equals(entry.getKey())) {
                Pattern pattern = Pattern.compile("([0-9]{3})([0-9]{3})([0-9]{4})");
                return pattern.matcher(entry.getValue()).replaceAll("($1) $2-$3");
            }
        }
        throw new NoSuchElementException();
    }
}
