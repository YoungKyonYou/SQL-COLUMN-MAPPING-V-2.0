import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Table;

import javax.swing.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

public class SqlMapping {

    //sql를 단어로 쪼갠 리스트
    private static List<String> words = new ArrayList<>();

    //sql이 사용하고 있는 테이블과 컬럼들을 저장
    private static HashMap<String, TableEntity> sqlContainsTableMap = new HashMap<>();

    //엑셀에 있는 모든 테이블과 컬럼들을 저장
    private static HashMap<String, TableEntity> tableMap=new HashMap<>();

    //컬럼명 Camel Case와 물리명 같이 저장하는 셋, TO-BE 논리명으로 정렬
    private static Set<ColumnEntity> camelCaseColNmSet2 = new TreeSet<>((o1, o2) -> o1.getToBeLogicalColName().compareTo(o2.getToBeLogicalColName()));

    //SQL KEYWORD SET를 구성, KEYWORD는 대문자로 표시
    private static Set<String> keyWordSet = new HashSet<>(Arrays.asList("select", "from", "where", "as", "and", "or", "union", "left", "outer", "join", "right"));

    public static void main(String[] args) {
        //메모장에서 sql 추출
        String sql = getSqlByTxt();
        //extractWord에서 마지막 단어를 추출하기 위해서 한 인덱스를 추가
        sql+=";";

        //sql를 다 소문자로 변환
        sql = sql.toLowerCase();

        //keyword는 다 대문자로 변환
        for(String key : keyWordSet){
            sql = sql.replaceAll("\\b"+key+"\\b", key.toUpperCase());
        }

        //메모장에서 추출한 sql를 단어로 분리해서 리스트에 저장
        extractWord(sql);

        //엑셀에서 매핑 정보 가져오기
        List<String> excelList = getTableColumnMappingString2();

        //엑셀에서 가져온 매핑 정보를 tableEntityMap에 세팅하기
        setSqlMappingMap(excelList);

        //sql에 존재하는 테이블을 sqlContainsTableMap에 따로 저장
        findTableEntityAndInsert(words);

        //매핑 시작
        String mappedSql = executeAliasColumnMapping(sql, sqlContainsTableMap);

        //메모장에 매핑한 sql 저장
        saveTxtFile(mappedSql);
        JOptionPane.showMessageDialog(null, "SQL Mapping Version 2.0 \n Mapped Successfully!\n Made By : 유영균 AsianaIDT \n ");


    }

    public static String executeAliasColumnMapping(String sql, HashMap<String, TableEntity> map){
        String mappedSql = sql;
        TableEntity tableEntity = null;
        int leftBracketCnt = 0;
        for(int i=0;i<words.size() ;i++){
            String[] aliasAndColNm = null;

            //leftBracket인 경우 count 올리고 다시 continue;
            if(words.get(i).equals("(")){
                leftBracketCnt++;
                continue;
            }

            //엘리어스가 존재하는 컬럼이라면
            if(words.get(i).contains(".")){
                aliasAndColNm = words.get(i).split("\\.");
                //System.out.println("alias:"+aliasAndColNm[0]+" colnm:"+aliasAndColNm[1]);
            }
            //sql 안에 존재하는 테이블을 각각 꺼내면서 그 테이블에 해당하는 컬럼이 있다면 매핑한다.
            for(String strKey : map.keySet()){
                tableEntity = map.get(strKey);

                //컬럼에 해당하는 엘리어스에 해당하는 테이블이 있을 경우
                if(aliasAndColNm != null && tableEntity.getAlias() != null && tableEntity.getAlias().contains(aliasAndColNm[0]) && !map.containsKey(aliasAndColNm[1])){
                    addToCamelCaseTxt(tableEntity, aliasAndColNm[1]);

                    //엘리어스 있는 컬럼 매핑
                    if(tableEntity.getAlias().contains(aliasAndColNm[0])){

                        //aliasAndColNm[0]는 alias만을 의미
                        mappedSql = mappedSql.replaceAll(words.get(i), (aliasAndColNm[0].toLowerCase()+"."+tableEntity.getColumnMappingMap().get(aliasAndColNm[1]).getToBeLogicalColName().toUpperCase()).trim());
                    }
                    //엘리어스 없는 컬럼명일 경우
                }else if(aliasAndColNm == null && tableEntity.isNoAliasYn()){
                    for(String key1 : map.keySet()){
                        for(Integer cnt : map.get(key1).getLeftBracketCount()){
                            if(cnt == leftBracketCnt){
                                String findSelect = "";
                                //엘리어스 없는 똑같은 이름을 가진 컬럼명이 여러 개 있을 수 있는데 이때 테이블이 다르고 as-is 컬럼명이 같을 수 있다.
                                //이때 해당 컬럼명이 있는 자리에서 첫 번째 그 자체 컬럼명만 바꾸기 위해서 idx를 추출해서 그 idx부터 한번만 replace를 실행한다.
                                int idx = findIndexOfColumNm(words.get(i), mappedSql, leftBracketCnt);

                                //words.get(i)가 컬럼명일 경우만
                                if(tableEntity.getColumnMappingMap().get(words.get(i)) != null && idx<mappedSql.length() && idx > 0){
                                    addToCamelCaseTxt(tableEntity, words.get(i));

                                    mappedSql = mappedSql.substring(0,idx)+mappedSql.substring(idx, mappedSql.length()).replaceFirst("\\b"+words.get(i)+"\\b", tableEntity.getColumnMappingMap().get(words.get(i)).getToBeLogicalColName().toUpperCase()).trim();
                                }

                            }
                        }
                    }
                }

                //테이블명 매핑
                if(tableEntity.getAsIsTableName().equals(words.get(i))){
                    mappedSql = mappedSql.replaceAll("\\b"+words.get(i)+"\\b", tableEntity.getToBeTableName().toUpperCase());
                }

            }
        }
        return mappedSql;
    }

    public static void addToCamelCaseTxt(TableEntity tableEntity, String columnNm){
        //컬럼의 카멜케이스
        String camelCaseColNm = getColNmToCamelCase(tableEntity.getColumnMappingMap().get(columnNm).getToBeLogicalColName());
        //컬럼의 물리이름
        String physicalColNm = tableEntity.getColumnMappingMap().get(columnNm).getToBePhysicalColName();

        ColumnEntity columnEntity =new ColumnEntity();
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

    public static String getColNmToCamelCase(String colNm){
        String[] strArr = colNm.split("_");
        strArr[0] = strArr[0].toLowerCase();
        for(int i=1; i<strArr.length; i++){
            strArr[i] = StringUtils.capitalize(StringUtils.lowerCase(strArr[i]));
        }
        String camelCaseColNm = String.join("", strArr);

        return camelCaseColNm;
    }

    //매핑을 하기 위한 테이블을 매핑함과 동시에 엘리어스를 설정한다.
    public static void findTableEntityAndInsert(List<String> words){
        int leftBracketCnt = 0 ;
        for(int i=0; i<words.size()-1; i++){
            //leftBracket를 만났을 경우 카운트 증가하고 다시 for문으로 돌아감
            if(words.get(i).equals("(")){
                leftBracketCnt++;
                continue;
            }
            if(tableMap.containsKey(words.get(i).trim())){
                //테이블 다음 문자가 where이 아니고 table 명이 아니라면 엘리어스 문자이다.
                if(!words.get(i+1).equals("WHERE") && !tableMap.containsKey(words.get(i+1).trim())){
                    //엘리어스 문자 세팅
                    tableMap.get(words.get(i)).putAliasInSet(words.get(i+1));
                    tableMap.get(words.get(i)).setAliasYn(true);
                }else{
                    tableMap.get(words.get(i)).setNoAliasYn(true);
                }
                tableMap.get(words.get(i)).putLeftBracketCount(leftBracketCnt);
                //sql에 존재하는 테이블을 sqlContainsTableMap에 따로 저장 key로는 AS-IS 테이블 명, value는 테이블 엔티티 객체
                sqlContainsTableMap.put(tableMap.get(words.get(i)).getAsIsTableName(), tableMap.get(words.get(i)));
            }
        }
        System.out.println();
    }

    //sql-table.txt에서 각 라인을 split 하고 정보를 저장
    public static void setSqlMappingMap(List<String> excelList){
        for(String str:excelList){
            TableEntity tableEntity = new TableEntity();
            String[] cell = str.split(" ");

            //To-Be 테이블 설정
            tableEntity.setToBeTableName(cell[0]);
            //As-Is 테이블 설정
            tableEntity.setAsIsTableName(cell[3]);

            //To-Be, As-Is 컬럼 설정
            //key는 As-Is 컬럼명
            String key = cell[4];

            //value는 value로 As-Is 컬럼의 논리명과 물리명 그리고 To-Be 컬럼의 논리명과 물리명
            ColumnEntity value = new ColumnEntity();
            //To-Be 컬럼 논리명
            value.setToBeLogicalColName(cell[1]);
            //To-Be 컬럼 물리명
            value.setToBePhysicalColName(cell[2]);
            //As-Is 컬럼 논리명
            value.setAsIsLogicalColName(cell[4]);
            //As-Is 컬럼 물리명
            value.setAsIsPhysicalColName(cell[5]);

            //TableMap 값 세팅
            if(!tableMap.containsKey(tableEntity.getAsIsTableName())){
                tableEntity.insertMap(key, value);
                tableMap.put(tableEntity.getAsIsTableName(), tableEntity);
            }
            else{
                tableMap.get(tableEntity.getAsIsTableName()).insertMap(key, value);
            }

            //   printHashMap(tableEntity.getColumnMappingMap());
        }
    }

    public static List<String> getTableColumnMappingString2(){
        List<String> excelList = new ArrayList<>();
        File note = new File("C:/sql-mapping/sql-table.txt");
        BufferedReader br = null;
        String sql = "";
        try {
            br = new BufferedReader(new FileReader(note));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.trim();
                excelList.add(line.toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelList;
    }

    public static List<String> extractWord(String sql){
        String word = "";
        sql+=";";
        boolean continueFlag = false;
        String findSelect = "";

        for(int i=0;i<sql.length()-1;i++){
            //Select 바로 왼쪽에 '('이 존재하면 넣고 다시 첫 i-for 문으로 돌아가기
            if(sql.charAt(i) == '('){
                for(int j=i+1; j<sql.length()-1 ;j++){
                    if(Character.isLetter(sql.charAt(j)) || sql.charAt(j) == 45 || Character.isDigit(sql.charAt(j)) || sql.charAt(j) == 95 || sql.charAt(j) == '.'){
                        findSelect+=Character.toString(sql.charAt(j));
                    }else{
                        if(findSelect.equals("SELECT")){
                            words.add("(");
                            continueFlag = true;
                        }
                        findSelect = "";
                        break;
                    }
                }
            }
            if(continueFlag){
                continueFlag = false;
                continue;
            }


            //ascii code 45 : "-", 95 : "_"
            if(Character.isLetter(sql.charAt(i)) || sql.charAt(i) == 45 || Character.isDigit(sql.charAt(i)) || sql.charAt(i) == 95 || sql.charAt(i) == '.'){
                word+=Character.toString(sql.charAt(i));
            }else{
                if(!word.equals(""))
                    words.add(word);
                word = "";
            }
        }

        return words;
    }

    //메모장에서 sql 내용 가져오기
    public static String getSqlByTxt(){
        File note = new File("C:/sql-mapping/sql.txt");
        BufferedReader br = null;
        String sql = "";
        try {
            br = new BufferedReader(new FileReader(note));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line;
                line+=" \r\n";
                sql += line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sql.toUpperCase();
    }

    public static void printList(List<String> list){
        for (int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
    }

    //매핑한 sql과 camel case를 메모장으로 저장
    public static void saveTxtFile(String sql){
        //query
        String query = sql;
        String fileNm = "C:/sql-mapping/mapped-sql.txt";

        //camel case
        String camelCaseStr = setToPlainString();
        String camelCaseFileNm = "C:/sql-mapping/camel-case.txt";

        try{
            File file = new File(fileNm);
            File file2 = new File(camelCaseFileNm);

            //경로에 똑같은 파일이 존재하면 삭제하고 다시 만들기
            if(file.exists() || file2.exists()){
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String setToPlainString(){
        String str = "";
        for(ColumnEntity entity : camelCaseColNmSet2){
            str+=entity.getToBeLogicalColName()+"    "+"/*"+entity.getToBePhysicalColName()+"*/"+"\r\n";
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
//    public static void printHashMapTableEntity(HashMap<String, TableEntity> map){
//        for( String strKey : map.keySet() ){
//            TableEntity object = map.get(strKey);
//            System.out.println("key:"+ strKey +" value:"+object.getAsIsTableName());
//        }
//    }
}
