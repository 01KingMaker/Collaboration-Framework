/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import utils.DaoUtility;
import utils.ObjectUtility;

/**
 *
 * @author Mamisoa
 * @param <T>
 */
public class GenericDao{
    //INSERT
    public static void save(Connection con, Object obj) throws Exception{
        boolean state = false;
        if(con == null){
            con = DbConnection.connect();
            state = true;
        }
        String query = "INSERT INTO "+DaoUtility.getTableName(obj)+DaoUtility.getListColumns(obj)+" VALUES (";
        List<Method> lst = DaoUtility.getAllGettersMethod(obj);
        for(Method method : lst){
            Class<?> returnParam = method.getReturnType();
            if(method.equals(DaoUtility.getPrimaryKeyGetMethod(obj)) && method.invoke(obj, (Object[]) null) == null && returnParam.equals(String.class)){
                query += "'" + constructPK(con, obj) + "'";  
            }else if(method.equals(DaoUtility.getPrimaryKeyGetMethod(obj)) && method.invoke(obj, (Object[]) null) == null && returnParam.equals(Integer.class)){
                query += constructPK(con, obj);
            }
            else if(method.invoke(obj, (Object[]) null) == null && !method.equals(DaoUtility.getPrimaryKeyGetMethod(obj))){
                query += "default";
            }
            else if(returnParam.equals(java.sql.Date.class))
            query += "TO_DATE('" + method.invoke(obj, (Object[]) null) + "', 'YYYY-MM-DD')";
            else
                query += "'" + method.invoke(obj, (Object[]) null) + "'"; 
            query = query + ", ";
        }
        query = query.substring(0, query.lastIndexOf(','));
        query = query + ")";
        // System.out.println(query);
        Statement stmt =  con.createStatement();
        stmt.executeUpdate(query);
        if( state == true) con.close();
    }
    
    //DELETE
    public static void delete(Connection con, Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "DELETE FROM " + DaoUtility.getTableName(obj)+" WHERE " + DaoUtility.getPrimaryKeyName(obj)  + " = '" + DaoUtility.getPrimaryKeyGetMethod(obj).invoke(obj, (Object[]) null) + "'" ;
           System.out.println(query);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    public static void deleteById(Connection con, Object id, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "DELETE FROM " + DaoUtility.getTableName(obj)+" WHERE " + DaoUtility.getPrimaryKeyName(obj)  +" = '" + id +"'";
        // System.out.println(query);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    public static void deleteWhere(Connection con, String condition, Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "DELETE FROM " + DaoUtility.getTableName(obj) + " WHERE " + condition;
            // System.out.println(query);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    
    //UPDATE
    public static void update(Connection con,Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "UPDATE "+ DaoUtility.getTableName(obj) +" SET ";
            List<Method> methods = DaoUtility.getAllGettersMethod(obj);
            List<Field> fields = DaoUtility.getColumnFields(obj.getClass());
            for( int i = 0; i < methods.size(); i++ ){
                query += fields.get(i).getName() + " = '"+methods.get(i).invoke(obj, (Object[]) null)+"', ";
            }
            query = query.substring(0, query.lastIndexOf(','));
            query += " WHERE " + DaoUtility.getPrimaryKeyName(obj) +" = '" + DaoUtility.getPrimaryKeyGetMethod(obj).invoke( obj, (Object[]) null)+"'";
            // System.out.println(query);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    //SELECT
    public static <T> List<T> findAll(Connection con, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj);
            List<T> list = fetch(con, query, obj);
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    public <T> List<T> findAllFromTable(Connection con, Object obj,String tableName)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "SELECT * FROM " + tableName;
            List<T> list = fetch(con, query, obj);
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    public static <T> T findById(Connection con, Object id, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + " WHERE " + DaoUtility.getPrimaryKeyName(obj) + " = '" + id + "'";
            T temp = (T) fetch(con, query, obj).get(0);
            // System.out.println(query);
            return temp;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    // search line to print
    public static <T> List<T> findWhere(Connection con, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = new DbConnection().connect();
                state = true;
            }
            
            String condition = DaoUtility.getConditionByAttributeValue(obj);
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + condition;
            // System.out.println(query);
            List<T> lst = fetch(con, query, obj);
            return lst;
        }finally{
            if( state == true) con.close();
        }
    }
    
    public static <T> List<T>  findWhere(Connection con, String condition, Object obj) throws Exception {
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String query = "SELECT * FROM " + DaoUtility.getTableName(obj) + " WHERE " + condition;
            // System.out.println(query);
            List<T>  lst = fetch(con, query, obj);
            return lst;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    //OTHERS
    public static void executeUpdate(Connection con, String query) throws Exception{
        boolean state = false;  
        try{      
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            Statement stmt =  con.createStatement();
            // System.out.println(query);
            stmt.executeUpdate(query);
        }finally {
            if(state == true) con.close();
        }    
    }
    public static <T> List<T> executeQuery(Connection con, String query, Object obj) throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            List<T> list = new ArrayList<>();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            List<Field> fields = DaoUtility.getColumnFields(obj.getClass());
            List<Method> methods = DaoUtility.getAllSettersMethod(obj);
            while( rs.next() ){
                T now = convertToObject(con, rs, fields, methods, obj);
                list.add(now);
            }
            return list;
        }finally {
            if(state == true) con.close();
        }    
    }
    
    public static <T> List<T> fetch(Connection con, String query, Object obj) throws Exception{
        List<T> list = new ArrayList<>();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<Field> fields = DaoUtility.getColumnFields(obj.getClass());
        List<Method> methods = DaoUtility.getAllSettersMethod(obj);
        while( rs.next() ){
            T now = convertToObject(con, rs, fields, methods, obj);
            list.add(now);
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T convertToObject(Connection con, ResultSet resultSet, List<Field> fields, List<Method> methods, Object obj) throws Exception{
        Object object = obj.getClass().getDeclaredConstructor().newInstance();
        List<String> columns = DaoUtility.getTableColumns(con, DaoUtility.getTableName(object));
        for (String column : columns) {
            for( int i = 0; i < fields.size() ; i++ ){
                if(fields.get(i).getName().equals(ObjectUtility.formatString(column))){
                    String name = DaoUtility.getName(fields.get(i));
                    Method method = methods.get(i);
                    Object value = resultSet.getObject(name); //, fields.get(i).getType());
                    if(value == null){
                        value = ObjectUtility.getPrimitiveDefaultValue(fields.get(i).getType());
                    }
                    method.invoke(object, value);
                }
            }    
        }
        return (T) object;
    }
    
    // private T convertToObject(ResultSet resultSet, List<Field> fields, List<Method> methods) throws Exception{
    //     Object object = this.getClass().getDeclaredConstructor().newInstance();
    //     for( int i = 0; i < fields.size() ; i++ ){
    //         String name = DaoUtility.getName(fields.get(i));
    //         Method method = methods.get(i);
    //         Object value = resultSet.getObject(name);// , fields.get(i).getType());
    //         if(value == null){
    //             value = ObjectUtility.getPrimitiveDefaultValue(fields.get(i).getType());
    //         }
    //         System.out.println(name+" = "+value);
    //         method.invoke(object, value);
    //     }
    //     return (T) object;
    // }   
        
    public static  String constructPK(Connection con, Object obj)throws Exception{
        boolean state = false;
        try{
            if(con == null){
                con = DbConnection.connect();
                state = true;
            }
            String[] detail = DaoUtility.getPrimaryKeyDetails(obj);
            if(detail[0].equals("true"))
                return "default";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nextval('" + detail[2] + "')");
            rs.next();
            String isa = ObjectUtility.fillZero(Integer.parseInt(detail[3]), Integer.parseInt(detail[4]), rs.getString(1));
            return detail[1]+isa;
        }finally {
            if(state == true) con.close();
        }    
    }
}
