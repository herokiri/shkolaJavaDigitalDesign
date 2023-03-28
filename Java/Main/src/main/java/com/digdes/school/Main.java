package com.digdes.school;

import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        starter.execute("INSERT VALUES  'id' = 1, 'lastname' = 'Шеряев', 'cost' = 234, 'age' = 19, 'active' = true");
        starter.execute("INSERT VALUES  'id' = 2, 'lastname' = 'Шипова', 'age' = 21, 'active' = false");
        starter.execute("INSERT VALUES  'id' = 3,'lastname' = 'Подобный',  'cost' = 764.65, 'age' = 34, 'active' = true");
        starter.execute("INSERT VALUES  'id' = 4, 'cost' = 1000.01, 'age' = 45, 'active' = false");

        starter.execute("select");

        starter.execute("UPDATE VALUES 'age' = 99, 'active'= false WHERE 'lastname' ilike 'Мак%' and 'id' != 4 or 'active' = true");

        starter.execute("UPDATE VALUES 'cost' = 345.3 WHERE 'lastname' like '%пова'");

        starter.execute("select");

        starter.execute("delete where 'age'=99");
        starter.execute("select");
        starter.execute("select WHERE 'cost' >= 346.75");




    }
}