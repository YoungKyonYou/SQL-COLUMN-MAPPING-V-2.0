import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlMapping {

    //sql를 단어로 쪼갠 리스트
    private static List<String> words = new ArrayList<>();

    private static List<WordEntity> wordEntities = new ArrayList<>();

    //sql이 사용하고 있는 테이블과 컬럼들을 저장
    private static HashMap<String, TableEntity> sqlContainsTableMap = new HashMap<>();

    //엑셀에 있는 모든 테이블과 컬럼들을 저장
    private static HashMap<String, TableEntity> tableMap = new HashMap<>();

    //컬럼명 Camel Case와 물리명 같이 저장하는 셋, TO-BE 논리명으로 정렬
    private static Set<ColumnEntity> camelCaseColNmSet2 = new TreeSet<>((o1, o2) -> o1.getToBeLogicalColName().compareTo(o2.getToBeLogicalColName()));

    //SQL KEYWORD SET를 구성, KEYWORD는 대문자로 표시
    private static Set<String> keyWordSet = new HashSet<>(Arrays.asList("select", "from", "where", "as", "and", "or", "union", "left", "outer", "join", "right", "inner"));

    private static Set<String> asIsColumnSet = new HashSet<>();

    private static Stack<String> bracketStack = new Stack<>();

    private static String sqlForSubQuery = "";


    public static void main(String[] args) {
        //메모장에서 sql 추출
        String sql = getSqlByTxt();
        //extractWord에서 마지막 단어를 추출하기 위해서 한 인덱스를 추가
        sql += ";";

        //엑셀에서 매핑 정보 가져오기
        List<String> excelList = getTableColumnMappingString2();

        //엑셀에서 가져온 매핑 정보를 tableEntityMap에 세팅하기
        sql = setSqlMappingMap(excelList, sql);

        //메모장에서 추출한 sql를 단어로 분리해서 리스트에 저장
        extractWordEntity(sql);

//        for(int i=0;i<wordEntities.size();i++){
//            System.out.println(wordEntities.get(i).getWord());
//        }

        //sql에 존재하는 테이블을 sqlContainsTableMap에 따로 저장
        findTableEntityAndInsert(wordEntities);
        // printHashMapTableEntity(sqlContainsTableMap);


        //매핑 시작
        executeAliasColumnMappingAndNonAliasColumnMapping(sql, sqlContainsTableMap);

        String mappedSql = mapColumn(sql);

        //이후에 재귀 함수를 사용하기 위해서 따로 전역 변수를 선언하고 초기화
//        sqlForSubQuery = mappedSql;
//        sqlForSubQuery = executeSubQueryMapping(sqlForSubQuery);
        //메모장에 매핑한 sql 저장
        saveTxtFile(mappedSql);

        String title = " __       _     ___      _                                                            \n" +
                "/ _\\ __ _| |   / __\\___ | |_   _ _ __ ___  _ __     /\\/\\   __ _ _ __  _ __   ___ _ __ \n" +
                "\\ \\ / _` | |  / /  / _ \\| | | | | '_ ` _ \\| '_ \\   /    \\ / _` | '_ \\| '_ \\ / _ \\ '__|\n" +
                "_\\ \\ (_| | | / /__| (_) | | |_| | | | | | | | | | / /\\/\\ \\ (_| | |_) | |_) |  __/ |   \n" +
                "\\__/\\__, |_| \\____/\\___/|_|\\__,_|_| |_| |_|_| |_| \\/    \\/\\__,_| .__/| .__/ \\___|_|   \n" +
                "       |_|                                                     |_|   |_|              \n";
        String str = "            _____ _____          _   _          _____ _____ _______ \n" +
                "     /\\    / ____|_   _|   /\\   | \\ | |   /\\   |_   _|  __ |__   __|\n" +
                "    /  \\  | (___   | |    /  \\  |  \\| |  /  \\    | | | |  | | | |   \n" +
                "   / /\\ \\  \\___ \\  | |   / /\\ \\ | . ` | / /\\ \\   | | | |  | | | |   \n" +
                "  / ____ \\ ____) |_| |_ / ____ \\| |\\  |/ ____ \\ _| |_| |__| | | |   \n" +
                " /_/    \\_|_____/|_____/_/    \\_|_| \\_/_/    \\_|_____|_____/  |_|   \n";

        String ver = "+-+-+-+-+-+-+-+ +-+-+-+\n" +
                "|V|e|r|s|i|o|n| |2|.|1|\n" +
                "+-+-+-+-+-+-+-+ +-+-+-+\n";

        JTextArea tArea = new JTextArea(22, 87);
        tArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        tArea.setText(title + "\n" + ver + "\n Mapped Successfully!\n Made By : 유영균 AsianaIDT \n\n\n " + str);
        tArea.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(tArea);
        //!! JOptionPane.showMessageDialog(null, sb.toString());
        //  JOptionPane.showMessageDialog(null, scrollPane, "SQL Column Mapper Version 2.1", JOptionPane.INFORMATION_MESSAGE);
        //   JOptionPane.showMessageDialog(null, "SQL Mapping Version 2.1 \n Mapped Successfully!\n Made By : 유영균 AsianaIDT \n\n\n "+str ,"SQL Column Mapper Version 2.0",JOptionPane.INFORMATION_MESSAGE,null);
    }

    public static String mapColumn(String sql) {
        for (int i = 0; i < wordEntities.size(); i++) {
            //매핑되는 것이 딱 하나일 때 1:1 매핑
            if (wordEntities.get(i).getColumnEntitySet().size() == 1) {
                String[] aliasColumn1 = wordEntities.get(i).getWord().split("\\.");

                //엘리어스 없는 컬럼일 경우
                if(aliasColumn1==null){
                    sql = sql.replaceFirst(wordEntities.get(i).getWord(), wordEntities.get(i).getColumnEntitySet().iterator().next().getToBeLogicalColName());
                }else if(aliasColumn1.length == 2){
                    //엘리어스 있는 컬럼일 경우
                    sql = sql.replaceFirst("\\b[a-zA-Z0-9]+\\." + aliasColumn1[1], aliasColumn1[0]+"\\."+wordEntities.get(i).getColumnEntitySet().iterator().next().getToBeLogicalColName());
                    System.out.println("기존:"+aliasColumn1[1]);
                    System.out.println("change:"+wordEntities.get(i).getColumnEntitySet().iterator().next().getToBeLogicalColName());
                }
                //1:N 매핑 즉 하나의 as-is 컬럼이 여러 개의 to-be 컬럼으로 매핑될 경우
            } else if (wordEntities.get(i).getColumnEntitySet().size() > 1) {
                String[] aliasColumn2 = wordEntities.get(i).getWord().split("\\.");
                String msg = "/*선택 : \n";
                for(ColumnEntity columnEntity : wordEntities.get(i).getColumnEntitySet()){
                      msg += "As-Is Table:" + columnEntity.getAsIsTableName()+" ColumnName:"+columnEntity.getAsIsLogicalColName()+"\nTo-Be Table:"+columnEntity.getToBeTableName()+" ColumnName:"+columnEntity.getToBeLogicalColName()+"\n\n";
                }
                msg+="*/\n";

                //엘리어스 없는 컬럼일 경우
                if(aliasColumn2==null){
                    sql = sql.replaceFirst(wordEntities.get(i).getWord(),wordEntities.get(i).getWord()+"\n"+msg);
                }else if(aliasColumn2.length == 2){
                    //엘리어스 있는 컬럼일 경우
                    sql = sql.replaceFirst("\\b[a-zA-Z0-9]+\\." + aliasColumn2[1], aliasColumn2[0]+"\\."+"["+aliasColumn2[1]+"]"+"\n"+msg);
                    System.out.println("기존:"+aliasColumn2[1]);
                    System.out.println("change:"+wordEntities.get(i).getColumnEntitySet().iterator().next().getToBeLogicalColName());
                }

            }
        }
        return sql;
    }

    public static String executeSubQueryMapping(String sqlForSubQuery) {

        String word = "";
        for (int i = 0; i < sqlForSubQuery.length() - 1; i++) {
            if (Character.isLetter(sqlForSubQuery.charAt(i)) || sqlForSubQuery.charAt(i) == 45 || Character.isDigit(sqlForSubQuery.charAt(i)) || sqlForSubQuery.charAt(i) == 95 || sqlForSubQuery.charAt(i) == '.') {
                word += Character.toString(sqlForSubQuery.charAt(i));
            } else {
                String[] separateAlias = word.split("\\.");
                if (separateAlias != null && separateAlias.length == 2 && asIsColumnSet.contains(separateAlias[1])) {
                    /**
                     * 매핑이 안된 컬럼은 subquery에서 파생된 컬럼이라는 것임으로 무조건 alias를 가지고 있다.
                     */
                    //alias를 가지고 있는 컬럼일 경우 (원래 무조건 가지고 있지만 혹시 모를 에러가 나는 상황 방지 위해)
                    if (separateAlias.length == 2) {
                        String mappedColumnNm = findColumnMapping(separateAlias[1], separateAlias[0]);
                        int idx = findColumnStartIdx(word, wordEntities);
                        String replacedSql = sqlForSubQuery.substring(wordEntities.get(idx).getStartIdx(), sqlForSubQuery.length()).replaceFirst("\\b" + word + "\\b", separateAlias[0] + "." + mappedColumnNm);
                        sqlForSubQuery = sqlForSubQuery.substring(0, wordEntities.get(idx).getStartIdx()) + replacedSql;
                    }
                }
                word = "";
            }
        }
        return sqlForSubQuery;
    }

    public static int findColumnStartIdx(String columnNm, List<WordEntity> wordEntities) {
        for (int i = 0; i < wordEntities.size(); i++) {
            if (columnNm.equals(wordEntities.get(i).getWord())) {
                return i;
            }
        }
        return 0;
    }

    public static String findColumnMapping(String word, String alias) {
        int idx = 0;
        //WordEntity를 순회하면서 sql에서 파라미터로 넘어온 alias의 위치 인덱스를 찾는다.
        for (int i = 0; i < wordEntities.size(); i++) {
            //wordEntities를 순회하다가 만약 alias와 일치한다면
            if (wordEntities.get(i).getWord().equals(alias)) {
                //역으로 순회한다.
                for (int j = i; j >= 0; j--) {
                    //word는 매핑이 안된 컬럼명
                    if (word.equals(wordEntities.get(j).getAsIsNm())) {
                        return wordEntities.get(j).getWord();
                    }
                }
            }
            /**
             * 여기선 재귀로 들어간다.
             * 만약 위에서 역으로 순회하는데 나타나는 단어가 as-is컬럼명이라면 다시 해당 함수를 들어가
             * 탐색한다.
             */
            if (asIsColumnSet.contains(wordEntities.get(i).getWord()) && checkIfLowerCase(wordEntities.get(i).getWord())) {
                String[] aliasColumn = wordEntities.get(i).getWord().split(".");
                //여기서 if를 해주는 이유는 AS 이후의 alias를 as-is 컬럼명으로 착각할 수 있기 때문이다 이때는 필수적으로 있는 alias가 없는 경우이기 때문이다.
                if (aliasColumn != null && aliasColumn.length == 2) {
                    String mappedColumnNm = findColumnMapping(wordEntities.get(i).getWord(), aliasColumn[0]);
                    sqlForSubQuery = sqlForSubQuery.substring(0, wordEntities.get(i).getStartIdx()) + sqlForSubQuery.substring(wordEntities.get(i).getStartIdx(), sqlForSubQuery.length()).replaceFirst("\\b" + aliasColumn[0] + wordEntities.get(i).getWord() + "\\b", mappedColumnNm);
                }
            }
        }
        return word;
    }

    public static boolean checkIfLowerCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void executeAliasColumnMappingAndNonAliasColumnMapping(String sql, HashMap<String, TableEntity> map) {
        String mappedSql = sql;
        TableEntity tableEntity = null;
        int leftBracketCnt = 0;
        for (int i = 0; i < wordEntities.size(); i++) {
            String[] aliasAndColNm = null;

            if (i == 21) {
                System.out.println();
            }

            //엘리어스가 존재하는 컬럼이라면
            if (wordEntities.get(i).getWord().contains(".")) {
                aliasAndColNm = wordEntities.get(i).getWord().split("\\.");
            }
            //sql 안에 존재하는 테이블을 각각 꺼내면서 그 테이블에 해당하는 컬럼이 있다면 매핑한다.
            for (String strKey : map.keySet()) {
                tableEntity = map.get(strKey);

                //컬럼이 엘리어스 컬럼일 경우
                if (aliasAndColNm != null && aliasAndColNm.length == 2 && tableEntity.getColumnMappingMap().containsKey(aliasAndColNm[1].toUpperCase().trim())) {
                    addToCamelCaseTxt(tableEntity, aliasAndColNm[1]);
                    wordEntities.get(i).addColumnEntitySet(tableEntity.getColumnMappingMap().get(aliasAndColNm[1]));
                   // wordEntities.get(i).addTableEntity(tableEntity);
                    //엘리어스 없는 컬럼명일 경우
                } else if (aliasAndColNm == null && tableEntity.getColumnMappingMap().containsKey(wordEntities.get(i).getWord().toUpperCase().trim())) {
                    addToCamelCaseTxt(tableEntity, wordEntities.get(i).getWord());
                    wordEntities.get(i).addColumnEntitySet(tableEntity.getColumnMappingMap().get(wordEntities.get(i).getWord()));
                    //wordEntities.get(i).addTableEntity(tableEntity);
                }


            }
        }
    }

    public static void addToCamelCaseTxt(TableEntity tableEntity, String columnNm) {
        if (tableEntity.getColumnMappingMap().get(columnNm) == null)
            return;
        //컬럼의 카멜케이스
        String camelCaseColNm = getColNmToCamelCase(tableEntity.getColumnMappingMap().get(columnNm).getToBeLogicalColName());
        //컬럼의 물리이름
        String physicalColNm = tableEntity.getColumnMappingMap().get(columnNm).getToBePhysicalColName();

        ColumnEntity columnEntity = new ColumnEntity();
        columnEntity.setToBeLogicalColName(camelCaseColNm);
        columnEntity.setToBePhysicalColName(physicalColNm);

        camelCaseColNmSet2.add(columnEntity);
    }

    public static int findIndexOfColumNm(String columNm, String sql, int leftBracketCnt) {
        String findSelect = "";
        boolean continueFlag = false;
        int cnt = 0;
        for (int i = 0; i < sql.length() - 1; i++) {

            if (cnt == leftBracketCnt) {
                return i;
            }

            //Select 바로 왼쪽에 '('이 존재하면 넣고 다시 첫 i-for 문으로 돌아가기
            if (sql.charAt(i) == '(') {
                for (int j = i + 1; j < sql.length() - 1; j++) {
                    if (Character.isLetter(sql.charAt(j)) || sql.charAt(j) == 45 || Character.isDigit(sql.charAt(j)) || sql.charAt(j) == 95 || sql.charAt(j) == '.') {
                        findSelect += Character.toString(sql.charAt(j));
                    } else {
                        if (findSelect.equals("SELECT")) {
                            //words.add("(");
                            cnt++;
                            continueFlag = true;
                        }
                        findSelect = "";
                        break;
                    }
                }
            }
            if (continueFlag) {
                continueFlag = false;
                continue;
            }
        }
        return 0;
    }


//    //매핑
//    public static String executeMapping(String sql, HashMap<String, TableEntity> map){
//        String mappedSql = sql;
//        TableEntity tableEntity = null;
//        String[] aliasAndColNm = null;
//        for(int i=0;i<words.size() ;i++){
//            //엘리어스가 존재하는 컬럼이라면
//            if(words.get(i).contains(".")){
//                aliasAndColNm = words.get(i).split(".");
//            }
//            //sql 안에 존재하는 테이블을 각각 꺼내면서 그 테이블에 해당하는 컬럼이 있다면 매핑한다.
//            for(String strKey : map.keySet()){
//                tableEntity = map.get(strKey);
//
//                //컬럼에 해당하는 엘리어스에 해당하는 테이블이 있을 경우
//                if(tableEntity.getAlias().equals(aliasAndColNm[0])){
//
//                }
//
//                //컬럼명 매핑
//                if(tableEntity.getColumnMappingMap().containsKey(words.get(i))){
//                    String camelCaseColNm = getColNmToCamelCase(tableEntity.getColumnMappingMap().get(words.get(i)).getToBeLogicalColName());
//                    String physicalColNm = tableEntity.getColumnMappingMap().get(words.get(i)).getToBePhysicalColName();
//
//                    ColumnEntity columnEntity =new ColumnEntity();
//                    columnEntity.setToBeLogicalColName(camelCaseColNm);
//                    columnEntity.setToBePhysicalColName(physicalColNm);
//
//                    camelCaseColNmSet2.add(columnEntity);
//
//                    mappedSql = mappedSql.replaceAll(words.get(i),tableEntity.getColumnMappingMap().get(words.get(i)).getToBeLogicalColName());
//                }
//
//                //테이블명 매핑
//                if(tableEntity.getAsIsTableName().equals(words.get(i))){
//                    mappedSql = mappedSql.replaceAll("\\b"+words.get(i)+"\\b ", tableEntity.getToBeTableName());
//                }
//            }
//        }
//        return mappedSql;
//    }

    public static String getColNmToCamelCase(String colNm) {
        String[] strArr = colNm.split("_");
        strArr[0] = strArr[0].toLowerCase();
        for (int i = 1; i < strArr.length; i++) {
            strArr[i] = StringUtils.capitalize(StringUtils.lowerCase(strArr[i]));
        }
        String camelCaseColNm = String.join("", strArr);

        return camelCaseColNm;
    }

    //매핑을 하기 위한 테이블을 매핑함과 동시에 엘리어스를 설정한다.
    public static void findTableEntityAndInsert(List<WordEntity> words) {
        for (int i = 0; i < words.size() - 1; i++) {

            if (tableMap.containsKey(words.get(i).getWord().toUpperCase().trim())) {
                //sql에 존재하는 테이블을 sqlContainsTableMap에 따로 저장 key로는 AS-IS 테이블 명, value는 테이블 엔티티 객체
                sqlContainsTableMap.put(tableMap.get(words.get(i).getWord().toUpperCase().trim()).getAsIsTableName().toUpperCase(), tableMap.get(words.get(i).getWord().toUpperCase().trim()));
            }
        }

    }

    //sql-table.txt에서 각 라인을 split 하고 정보를 저장
    public static String setSqlMappingMap(List<String> excelList, String sql) {
        for (String str : excelList) {
            TableEntity tableEntity = new TableEntity();
            String[] cell = str.split(" ");

            //To-Be 테이블 설정
            tableEntity.setToBeTableName(cell[0].toUpperCase());
            //As-Is 테이블 설정
            tableEntity.setAsIsTableName(cell[3].toUpperCase());

            //To-Be, As-Is 컬럼 설정
            //key는 As-Is 컬럼명
            String key = cell[4];

            //value는 value로 As-Is 컬럼의 논리명과 물리명 그리고 To-Be 컬럼의 논리명과 물리명
            ColumnEntity value = new ColumnEntity();
            //To-Be 컬럼 논리명
            value.setToBeLogicalColName(cell[1].toUpperCase());
            //To-Be 컬럼 물리명
            value.setToBePhysicalColName(cell[2].toUpperCase());
            //As-Is 컬럼 논리명
            value.setAsIsLogicalColName(cell[4].toUpperCase());
            //As-Is 컬럼 물리명
            value.setAsIsPhysicalColName(cell[5].toUpperCase());

            //To-Be 테이블명 설정
            value.setToBeTableName(cell[0].toUpperCase());

            //As-Is 테이블명 설정
            value.setAsIsTableName(cell[3].toUpperCase());

            //subQueryMapping에서 사용
            asIsColumnSet.add(cell[4].toUpperCase());

            //TableMap 값 세팅
            if (!tableMap.containsKey(tableEntity.getAsIsTableName().toUpperCase())) {
                tableEntity.insertMap(key, value);
                tableMap.put(tableEntity.getAsIsTableName().toUpperCase(), tableEntity);
            } else {
                tableMap.get(tableEntity.getAsIsTableName().toUpperCase()).insertMap(key, value);
            }

            //   printHashMap(tableEntity.getColumnMappingMap());
        }

        //sql에서 테이블 이름 앞에 스키마가 붙은 것을 떼어주기 위함 ex: nexs.tableName -> tableName으로
        for (String tableNm : tableMap.keySet()) {
            sql = sql.replaceAll("\\b[a-zA-Z0-9]+\\." + tableNm + "\\b", tableNm);
        }

        return sql;
    }

    public static List<String> getTableColumnMappingString2() {
        List<String> excelList = new ArrayList<>();
        File note = new File("C:/sql-mapping/sql-table.txt");
        BufferedReader br = null;
        String sql = "";
        try {
            br = new BufferedReader(new FileReader(note));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.trim();
                //엑셀에서 메모장으로 복사해오면 tab 문자가 생기는 이를 스페이스 바 하나로 변환
                excelList.add(line.toUpperCase().replaceAll("\t", " "));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelList;
    }

    public static void extractWordEntity(String sql) {
        String word = "";
        sql += ";";
        boolean continueFlag = false;
        String findSelect = "";
        int startIdx = 0;
        int endIdx = 0;

        for (int i = 0; i < sql.length() - 1; i++) {
            //ascii code 45 : "-", 95 : "_"
            if (Character.isLetter(sql.charAt(i)) || sql.charAt(i) == 45 || Character.isDigit(sql.charAt(i)) || sql.charAt(i) == 95 || sql.charAt(i) == '.') {
                if (word.equals("")) {
                    startIdx = i;
                }
                word += Character.toString(sql.charAt(i));
            } else {
                if (!word.equals("")) {
                    endIdx = i;
                    WordEntity wordEntity = new WordEntity();
                    wordEntity.setWord(word);
                    wordEntity.setStartIdx(startIdx);
                    wordEntity.setEndIdx(endIdx);
                    wordEntities.add(wordEntity);
                }
                word = "";
            }
        }

        //WordEntity의 asIsNm을 세팅해주기 위해서 words 리스트를 참고하여 세팅한다
        for (int i = 0; i < words.size(); i++) {
            wordEntities.get(i).setAsIsNm(words.get(i));
        }
    }

    public static List<String> extractWord(String sql) {
        String word = "";
        sql += ";";
        boolean continueFlag = false;
        String findSelect = "";

        for (int i = 0; i < sql.length() - 1; i++) {
            //Select 바로 왼쪽에 '('이 존재하면 넣고 다시 첫 i-for 문으로 돌아가기
            if (sql.charAt(i) == '(') {
                for (int j = i + 1; j < sql.length() - 1; j++) {
                    if (Character.isLetter(sql.charAt(j)) || sql.charAt(j) == 45 || Character.isDigit(sql.charAt(j)) || sql.charAt(j) == 95 || sql.charAt(j) == '.') {
                        findSelect += Character.toString(sql.charAt(j));
                    } else {
                        //서브쿼리의 시작인 (인 경우는 words에 넣는다 이는 바로 뒤의 word가 SELECT인 경우다.
                        if (findSelect.equals("SELECT")) {
                            words.add("(");
                            /**
                             * "("가 의미하는 것이 함수의 시작일 수도 있고 서브쿼리의 시작일 수도 있다
                             * 서브 쿼리의 시작일 경우 "("로 저장하고 아니라면 *로 저장한다
                             * 이는 함수의 끝인 )를 만났을 때 뺄 것이다.
                             */
                            bracketStack.add("(");
                            continueFlag = true;
                        } else {
                            bracketStack.add("*");
                        }
                        findSelect = "";
                        break;
                    }
                }
            }
            //다음 첫 문자가 ")"이라면 word는 그 바로 전 i에서 초기화됨
            else if (sql.charAt(i) == ')') {
                String nextWord = getNextWord(i + 1, sql.length(), sql);

                if ((nextWord.equals("AS") || nextWord.matches("[a-zA-Z0-9]+")) && bracketStack.size() > 0 && bracketStack.peek().equals("(")) {
                    //서브쿼리의 끝일 경우
                    words.add(")");
                    word = "";
                    bracketStack.pop();
                } else if (bracketStack.size() > 0 && bracketStack.peek().equals("*")) {
                    //함수의 끝일 경우엔 "("를 하나 뺀다.
                    bracketStack.pop();
                }
            }
            if (continueFlag) {
                continueFlag = false;
                continue;
            }


            //ascii code 45 : "-", 95 : "_"
            if (Character.isLetter(sql.charAt(i)) || sql.charAt(i) == 45 || Character.isDigit(sql.charAt(i)) || sql.charAt(i) == 95 || sql.charAt(i) == '.') {
                word += Character.toString(sql.charAt(i));
            } else {
                if (!word.equals("")) {
                    words.add(word);
                }
                word = "";
            }
        }

        return words;
    }

    public static String getNextWord(int startIdx, int endIdx, String sql) {
        String nextWord = "";
        for (int i = startIdx; i < endIdx; i++) {
            if (Character.isLetter(sql.charAt(i)) || sql.charAt(i) == 45 || Character.isDigit(sql.charAt(i)) || sql.charAt(i) == 95 || sql.charAt(i) == '.') {
                nextWord += Character.toString(sql.charAt(i));
            } else {
                if (nextWord != "")
                    return nextWord;
            }
        }
        return "";
    }

    //메모장에서 sql 내용 가져오기
    public static String getSqlByTxt() {
        File note = new File("C:/sql-mapping/sql.txt");
        BufferedReader br = null;
        String sql = "";
        try {
            br = new BufferedReader(new FileReader(note));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line;
                line += " \r\n";
                sql += line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sql;
    }

    public static void printList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    //매핑한 sql과 camel case를 메모장으로 저장
    public static void saveTxtFile(String sql) {
        //query
        String query = sql;
        String fileNm = "C:/sql-mapping/mapped-sql.txt";

        //camel case
        String camelCaseStr = setToPlainString();
        String camelCaseFileNm = "C:/sql-mapping/camel-case.txt";

        try {
            File file = new File(fileNm);
            File file2 = new File(camelCaseFileNm);

            //경로에 똑같은 파일이 존재하면 삭제하고 다시 만들기
            if (file.exists() || file2.exists()) {
                file.delete();
                file2.delete();
            }

            FileWriter fileWrite1 = new FileWriter(file, true);
            fileWrite1.write(query);
            fileWrite1.flush();

            FileWriter fileWrite2 = new FileWriter(file2, true);
            fileWrite2.write(camelCaseStr);
            fileWrite2.flush();

            fileWrite2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String setToPlainString() {
        String str = "";

        for (ColumnEntity entity : camelCaseColNmSet2) {
            byte[] bytes = entity.getToBePhysicalColName().getBytes(StandardCharsets.UTF_8);

            String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
            str += entity.getToBeLogicalColName() + "    " + "/*" + utf8EncodedString + "*/" + "\r\n";
        }
        return str;
    }

    //    public static void printHashMapColumnEntity(HashMap<String, ColumnEntity> map){
//        for( String strKey : map.keySet() ){
//            ColumnEntity object = map.get(strKey);
//            System.out.println("key:"+ strKey );
//            System.out.println(object.getToBeLogicalColName()+" "+object.getToBePhysicalColName()
//                    +" "+object.getAsIsLogicalColName()+" "+object.getAsIsPhysicalColName());
//        }
//    }
    public static void printHashMapTableEntity(HashMap<String, TableEntity> map) {
        for (String strKey : map.keySet()) {
            TableEntity object = map.get(strKey);
            System.out.println("key:" + strKey + " value:" + object.getAsIsTableName());
        }
    }
}
