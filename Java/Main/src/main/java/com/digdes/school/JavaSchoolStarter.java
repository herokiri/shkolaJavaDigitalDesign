package com.digdes.school;

import com.digdes.school.Exceptions.IncorrectExecute;
import com.digdes.school.Exceptions.InsertSyntaxException;
import com.digdes.school.Exceptions.UpdateSyntaxException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class JavaSchoolStarter {
    static List<Map<String, Object>> data = new ArrayList<>();
    static List<Map<String, Object>> filteredData = new ArrayList<>();


    public JavaSchoolStarter(){

    }
    public List<Map<String,Object>> execute(String request) throws Exception {
        queryDefinition(request);
        return filteredData;
    }

    //обрабатываем синтаксическую ошибку для токенов
    private boolean tokenIsCorrect(String token) throws InsertSyntaxException {
        if (!Pattern.matches("(?i)(\\s*'id'\\s?=\\s?[0-9]+\\s*)|(\\s*'lastname'\\s?(=)\\s?'[a-zA-Zа-яА-Я]+'\\s*)|(\\s*'age'\\s?=\\s?[0-9]+\\s*)|(\\s*'cost'\\s?=\\s?[0-9]+(\\.[0-9]+)*\\s*)|(\\s*'active'\\s?=\\s?(true|false)\\s*)", token)) {

            throw new InsertSyntaxException("Ошибка ввода в : " + token);
        }
        else {
            return true;
        }
    }
    //обрабатываем синтаксическую ошибку для условий
    private static boolean contidionsIsCorrect(String contidion) throws InsertSyntaxException {
        if(!Pattern.matches("(?i)(\\s*'id'\\s?(=|!=|>=|<=|>|<)\\s?[0-9]+\\s*)|(\\s*'lastname'\\s?(=|!=|like|ilike)\\s?'%?[a-zA-Zа-яА-Я]+%?'\\s*)|(\\s*'age'\\s?(=|!=|>=|<=|>|<)\\s?[0-9]+\\s*)|(\\s*'cost'\\s?(=|!=|>=|<=|>|<)\\s?[0-9]+(\\.[0-9]+)?\\s*)|(\\s*'active'\\s?(=|!=)\\s?(true|false)\\s*)",contidion)) {
            throw new InsertSyntaxException("Ошибка в условие : " + contidion);
        } else {
            return true;
        }
    }
    private static boolean whereIsCorrect(Map<String, Object> mp, String criterion) throws InsertSyntaxException {
        //так как AND имеет приоритет, разбиваем строку по OR;
        String[] andCritetion = criterion.split("(?i)OR");

        for(String cr : andCritetion) {
            String[] condition = cr.split("(?i)AND");

            boolean isOk = false;
            for(String cond : condition) {
                if(!customOperation(mp, cond)) {
                    isOk = false;
                    break;
                }
                isOk = true;
            }
            if(isOk) {
                filteredData.add(mp);
                return true;
            }
        }


        return false;
    }

    private static boolean customOperation(Map<String, Object> mp, String cond) throws InsertSyntaxException {

            if(contidionsIsCorrect(cond)) {
                cond = cond.replaceAll("'|\\s","");

                String integerKey="";
                Pattern pattern = Pattern.compile("(id|age)");
                Matcher matcher =  pattern.matcher(cond);
                if(matcher.find()) {
                    integerKey = matcher.group();
                }

                if(integerKey != "") {
                    if(mp.get(integerKey) == null) {
                        return false;
                    }

                    String value = "";
                    pattern = Pattern.compile("[0-9]+");
                    matcher =  pattern.matcher(cond);
                    if(matcher.find()) {
                        value = matcher.group();
                    }

                    String operand = "";
                    pattern = Pattern.compile("(=|!=|>=|<=|>|<)");
                    matcher =  pattern.matcher(cond);
                    if(matcher.find()) {
                        operand = matcher.group();
                    }
                    if(Objects.equals(operand, "=")) {
                        return (((Integer) mp.get(integerKey)) == Integer.parseInt(value));
                    }
                    else if(Objects.equals(operand, "!=")) {
                        return (((Integer) mp.get(integerKey)) != Integer.parseInt(value));
                    }
                    else if(Objects.equals(operand, ">=")) {
                        return (((Integer) mp.get(integerKey)) >= Integer.parseInt(value));
                    }
                    else if(Objects.equals(operand, "<=")) {
                        return (((Integer) mp.get(integerKey)) <= Integer.parseInt(value));
                    }
                    else if(Objects.equals(operand, "<")) {
                        return (((Integer) mp.get(integerKey)) < Integer.parseInt(value));
                    }
                    else if(Objects.equals(operand, ">")) {
                        return (((Integer) mp.get(integerKey)) > Integer.parseInt(value));
                    }
                }

                else if(cond.toLowerCase().contains("lastname")) {
                    if(mp.get("lastname") == null) {
                        return false;
                    }

                    String operand = "";
                    pattern = Pattern.compile("(=|!=|like|ilike)");
                    matcher =  pattern.matcher(cond);
                    if(matcher.find()) {
                        operand = matcher.group();
                    }

                    cond = cond.substring(8);
                    if(Objects.equals(operand, "=")) {
                        cond = cond.substring(1);
                        String value = "";
                        pattern = Pattern.compile("%?[a-zA-Zа-яА-Я]+%?");
                        matcher =  pattern.matcher(cond);
                        if(matcher.find()) {
                            value = matcher.group();
                        }
                        return(Objects.equals((String) mp.get("lastname"), value));
                    }
                    else if(Objects.equals(operand, "!=")) {
                        cond = cond.substring(2);
                        String value = "";
                        pattern = Pattern.compile("%?[a-zA-Zа-яА-Я]+%?");
                        matcher =  pattern.matcher(cond);
                        if(matcher.find()) {
                            value = matcher.group();
                        }
                        return(!Objects.equals((String) mp.get("lastname"), value));
                    }
                    else if(Objects.equals(operand, "ilike")) {
                        cond = cond.substring(5);
                        String value = "";
                        pattern = Pattern.compile("%?[a-zA-Zа-яА-Я]+%?");
                        matcher =  pattern.matcher(cond);
                        if(matcher.find()) {
                            value = matcher.group();
                        }

                        if(value.charAt(0) == '%' && value.charAt(value.length() - 1) == '%') {
                            value = value.replaceAll("%","");

                            return (Pattern.matches(".*" + value.toLowerCase() + ".*", ((String) mp.get("lastname")).toLowerCase()));
                        }
                        else if(value.charAt(0) == '%') {
                            value = value.replaceAll("%","");

                            return (Pattern.matches(".*" + value.toLowerCase(), ((String) mp.get("lastname")).toLowerCase()));
                        }
                        else if(value.charAt(value.length() - 1) == '%') {
                            value = value.replaceAll("%","");


                            return (Pattern.matches(value.toLowerCase() + ".*", ((String) mp.get("lastname")).toLowerCase()));
                        }
                        else return(Objects.equals(((String) mp.get("lastname")).toLowerCase(), value.toLowerCase()));



                    }else if(Objects.equals(operand, "like")) {


                        cond = cond.substring(4);
                        String value = "";
                        pattern = Pattern.compile("%?[a-zA-Zа-яА-Я]+%?");
                        matcher =  pattern.matcher(cond);
                        if(matcher.find()) {
                            value = matcher.group();
                        }

                        // шаблон %value%
                        if(value.charAt(0) == '%' && value.charAt(value.length() - 1) == '%') {
                            value = value.replaceAll("%","");


                            return (Pattern.matches(".*" + value + ".*", (String) mp.get("lastname")));
                        }
                        // шаблон %value
                        else if(value.charAt(0) == '%') {

                            value = value.replaceAll("%","");

                            return (Pattern.matches(".*" + value, (String) mp.get("lastname")));
                        }
                        // шаблон value%
                        else if(value.charAt(value.length() - 1) == '%') {
                            value = value.replaceAll("%","");

                            return (Pattern.matches(value + ".*", (String) mp.get("lastname")));
                        } else {
                            return(Objects.equals((String) mp.get("lastname"), value));
                        }
                    }
                    return false;
                }

                else if(cond.toLowerCase().contains("active")) {
                    if(mp.get("active") == null) {
                        return false;
                    }

                    String value = "";
                    pattern = Pattern.compile("(?i)(true|false)");
                    matcher =  pattern.matcher(cond);
                    if(matcher.find()) {
                        value = matcher.group();
                    }

                    if(cond.contains("=")) {
                        return ((boolean) mp.get("active") == Boolean.parseBoolean(value));
                    }
                    else if(cond.contains("!=")) {
                        return ((boolean) mp.get("active") != Boolean.parseBoolean(value));
                    }
                }
                else if(cond.toLowerCase().contains("cost")) {
                    if(mp.get("cost") == null) {
                        return false;
                    }
                    String value = "";
                    pattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
                    matcher = pattern.matcher(cond);
                    if (matcher.find()) {
                        value = matcher.group();
                    }

                    String operand = "";
                    pattern = Pattern.compile("(=|!=|>=|<=|>|<)");
                    matcher = pattern.matcher(cond);
                    if (matcher.find()) {
                        operand = matcher.group();
                    }
                    if (Objects.equals(operand, "=")) {
                        return (((Double) mp.get("cost")) == Double.parseDouble(value));
                    } else if (Objects.equals(operand, "!=")) {
                        return (((Double) mp.get("cost")) != Double.parseDouble(value));
                    } else if (Objects.equals(operand, ">=")) {
                        return (((Double) mp.get("cost")) >= Double.parseDouble(value));
                    } else if (Objects.equals(operand, "<=")) {
                        return (((Double) mp.get("cost")) <= Double.parseDouble(value));
                    } else if (Objects.equals(operand, "<")) {
                        return (((Double) mp.get("cost")) < Double.parseDouble(value));
                    } else if (Objects.equals(operand, ">")) {
                        return (((Double) mp.get("cost")) > Double.parseDouble(value));
                    }
                }

            }
            return false;
    }

    private void queryDefinition(String request) throws InsertSyntaxException, IncorrectExecute, UpdateSyntaxException {
        filteredData.clear();
        String insertRegex = "^(?i)INSERT VALUES.*";
        String updateRegex = "^(?i)UPDATE VALUES.*";
        String selectRegex = "^(?i)SELECT.*";
        String deleteRegex = "^(?i)DELETE.*";
        if(Pattern.matches(insertRegex, request)) {
            //удаляем (INSERT VALUES), оставляем только аттрибуты
            request = request.substring(13);
            insert(request);

        }
        else if(Pattern.matches(updateRegex, request)) {
            //удаляем (UPDATE VALUES), оставляем только аттрибуты
            request = request.substring(13);
            update(request);
        }
        else if(Pattern.matches(selectRegex, request)) {
            //удаляем (SELECT), оставляем только аттрибуты
            request = request.substring(6);
            select(request);
        }
        else if(Pattern.matches(deleteRegex, request)) {
            //удаляем (DELETE), оставляем только аттрибуты
            request = request.substring(6);
            delete(request);
        }
        else throw new IncorrectExecute("Команда не определена");
        //filteredData.clear();
    }
    public static void selectFilteredData() {

            if (filteredData.isEmpty()) {
                System.out.println("Table is empty");
                return;
            }

            // Define column widths
            final int ID_WIDTH = 4;
            final int LASTNAME_WIDTH = 16;
            final int AGE_WIDTH = 6;
            final int COST_WIDTH = 16;
            final int ACTIVE_WIDTH = 6;

            // Print table header
            System.out.print("|");
            System.out.printf("%-" + ID_WIDTH + "s|", "id");
            System.out.printf("%-" + LASTNAME_WIDTH + "s|", "lastname");
            System.out.printf("%-" + AGE_WIDTH + "s|", "age");
            System.out.printf("%-" + COST_WIDTH + "s|", "cost");
            System.out.printf("%-" + ACTIVE_WIDTH + "s|", "active");
            System.out.println();

            // Print separator
            System.out.print("|");
            for (int i = 0; i < ID_WIDTH + LASTNAME_WIDTH + AGE_WIDTH + COST_WIDTH + ACTIVE_WIDTH + 4; i++) {
                System.out.print("-");
            }
            System.out.println("|");

            // Print table rows
            for (Map<String, Object> row : filteredData) {
                System.out.print("|");
                System.out.printf("%-" + ID_WIDTH + "s|", row.get("id"));
                System.out.printf("%-" + LASTNAME_WIDTH + "s|", row.get("lastname"));
                System.out.printf("%-" + AGE_WIDTH + "s|", row.get("age"));
                System.out.printf("%-" + COST_WIDTH + "s|", row.get("cost"));
                System.out.printf("%-" + ACTIVE_WIDTH + "s|", row.get("active"));
                System.out.println();
            }

            // Print separator
            System.out.print("|");
            for (int i = 0; i < ID_WIDTH + LASTNAME_WIDTH + AGE_WIDTH + COST_WIDTH + ACTIVE_WIDTH + 4; i++) {
                System.out.print("-");
            }
            System.out.println("|");

            System.out.println();System.out.println();System.out.println();

        }


    public static void select(String request) throws InsertSyntaxException {
        if (request.length() == 0) {
            if (data.isEmpty()) {
                System.out.println("Table is empty");
                return;
            }

            // Define column widths
            final int ID_WIDTH = 4;
            final int LASTNAME_WIDTH = 16;
            final int AGE_WIDTH = 6;
            final int COST_WIDTH = 16;
            final int ACTIVE_WIDTH = 6;

            // Print table header
            System.out.print("|");
            System.out.printf("%-" + ID_WIDTH + "s|", "id");
            System.out.printf("%-" + LASTNAME_WIDTH + "s|", "lastname");
            System.out.printf("%-" + AGE_WIDTH + "s|", "age");
            System.out.printf("%-" + COST_WIDTH + "s|", "cost");
            System.out.printf("%-" + ACTIVE_WIDTH + "s|", "active");
            System.out.println();

            // Print separator
            System.out.print("|");
            for (int i = 0; i < ID_WIDTH + LASTNAME_WIDTH + AGE_WIDTH + COST_WIDTH + ACTIVE_WIDTH + 4; i++) {
                System.out.print("-");
            }
            System.out.println("|");

            // Print table rows
            for (Map<String, Object> row : data) {
                System.out.print("|");
                System.out.printf("%-" + ID_WIDTH + "s|", row.get("id"));
                System.out.printf("%-" + LASTNAME_WIDTH + "s|", row.get("lastname"));
                System.out.printf("%-" + AGE_WIDTH + "s|", row.get("age"));
                System.out.printf("%-" + COST_WIDTH + "s|", row.get("cost"));
                System.out.printf("%-" + ACTIVE_WIDTH + "s|", row.get("active"));
                System.out.println();
            }

            // Print separator
            System.out.print("|");
            for (int i = 0; i < ID_WIDTH + LASTNAME_WIDTH + AGE_WIDTH + COST_WIDTH + ACTIVE_WIDTH + 4; i++) {
                System.out.print("-");
            }
            System.out.println("|");

            System.out.println();System.out.println();System.out.println();
        } else {
            String[] cond = request.split("(?i)WHERE");
            for(Map<String, Object> row : data) {

                if(cond.length == 1)
                    whereIsCorrect(row, cond[0]);
                else whereIsCorrect(row,cond[1]);
            }

            selectFilteredData();
        }
    }
    private void delete(String request) throws InsertSyntaxException {
        if(request.length() == 0) {
            data.clear();
        }
        else {
            String[] cond = request.split("(?i)WHERE");

            Iterator<Map<String, Object>> iterator = data.iterator();

            while (iterator.hasNext()) {
                Map<String, Object> row = iterator.next();

                if(cond.length == 1) {
                    if( whereIsCorrect(row, cond[0])) {
                        iterator.remove();
                    }
                }
                else {
                    if(whereIsCorrect(row, cond[1])) {
                        iterator.remove();
                    }
                }
            }

        }

    }

    private void insert(String input) throws InsertSyntaxException {
        Map<String, Object> row = new HashMap<>();

        String[] tokens = input.split(",");
        for (String token : tokens) {

            //обрабатываем ошибку и показываем в каком именно месте мы совершили ее
            tokenIsCorrect(token);

            //удаляем все лишние кавычки и пробелы
            token = token.replaceAll("\\s+|'", "");
            //разделяем на ключ - значение
            String[] keyValue = token.split("=");

            for(int i = 0; i < keyValue.length - 1; i++) {
                if(keyValue[i].equalsIgnoreCase("id")) {

                    row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                }
                else if(keyValue[i].equalsIgnoreCase("lastname")) {
                    row.put(keyValue[i], keyValue[i + 1]);
                }
                else if(keyValue[i].equalsIgnoreCase("age")) {
                    row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                }
                else if(keyValue[i].equalsIgnoreCase("cost")) {
                    row.put(keyValue[i], Double.parseDouble(keyValue[i + 1]));
                }
                else if(keyValue[i].equalsIgnoreCase("active")) {
                    row.put(keyValue[i], Boolean.parseBoolean(keyValue[i + 1]));
                }
            }
        }

        data.add(row);
    }

    private void update(String input) throws UpdateSyntaxException, InsertSyntaxException {


        String[] tokens_and_conditions = input.split("(?i)WHERE");
        if (tokens_and_conditions.length > 2) {
            throw new UpdateSyntaxException("Ошибка в количестве where");
        }


        if (tokens_and_conditions.length == 1) {
            for (Map<String, Object> row : data) {
                String[] tokens = input.split(",");
                for (String token : tokens) {

                    //обрабатываем ошибку и показываем в каком именно месте мы совершили ее
                    tokenIsCorrect(token);

                    //удаляем все лишние кавычки и пробелы
                    token = token.replaceAll("\\s+|'", "");
                    //разделяем на ключ - значение
                    String[] keyValue = token.split("=");

                    for (int i = 0; i < keyValue.length - 1; i++) {
                        if (keyValue[i].equalsIgnoreCase("id")) {

                            row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                        } else if (keyValue[i].equalsIgnoreCase("lastname")) {
                            row.put(keyValue[i], keyValue[i + 1]);
                        } else if (keyValue[i].equalsIgnoreCase("age")) {
                            row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                        } else if (keyValue[i].equalsIgnoreCase("cost")) {
                            row.put(keyValue[i], Double.parseDouble(keyValue[i + 1]));
                        } else if (keyValue[i].equalsIgnoreCase("active")) {
                            row.put(keyValue[i], Boolean.parseBoolean(keyValue[i + 1]));
                        }
                    }
                }

            }
        } else if (tokens_and_conditions.length == 2) {
            String[] tokens = tokens_and_conditions[0].split(",");
            String criterion = tokens_and_conditions[1];

            for(Map<String, Object> row : data) {
                if(whereIsCorrect(row, criterion)) {
                    for (String token : tokens) {

                        //обрабатываем ошибку и показываем в каком именно месте мы совершили ее
                        tokenIsCorrect(token);

                        //удаляем все лишние кавычки и пробелы
                        token = token.replaceAll("\\s+|'", "");
                        //разделяем на ключ - значение
                        String[] keyValue = token.split("=");

                        for (int i = 0; i < keyValue.length - 1; i++) {
                            if (keyValue[i].equalsIgnoreCase("id")) {

                                row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                            } else if (keyValue[i].equalsIgnoreCase("lastname")) {
                                row.put(keyValue[i], keyValue[i + 1]);
                            } else if (keyValue[i].equalsIgnoreCase("age")) {
                                row.put(keyValue[i], Integer.parseInt(keyValue[i + 1]));
                            } else if (keyValue[i].equalsIgnoreCase("cost")) {
                                row.put(keyValue[i], Double.parseDouble(keyValue[i + 1]));
                            } else if (keyValue[i].equalsIgnoreCase("active")) {
                                row.put(keyValue[i], Boolean.parseBoolean(keyValue[i + 1]));
                            }
                        }
                    }
                }


            }

            System.out.println("строки, которые были изменены : ");
            selectFilteredData();




        }

    }

}






