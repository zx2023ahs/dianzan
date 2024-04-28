package cn.rh.flash.bean.vo.query;


import cn.rh.flash.utils.StringUtil;

import java.util.Date;
import java.util.List;

public class SqlSpecification {

    /**
     * sql 条件拼接
     * @param sql
     * @return
     */
    public static String toAddSql(String sql, List<SearchFilter> filters) {
        if( !sql.contains( " where 1=1 " ) )
            sql = String.format(" %s where 1=1 ",sql);
        if (filters != null) {
            for (SearchFilter filter : filters) {
                String fieldName = filter.fieldName;
                Object value = filter.value;
                SearchFilter.Operator operator = filter.operator;
                String name = operator.name();
                if (!name.equals("ISNULL")&&value == null) continue;

                SearchFilter.Join join = filter.join;
                String s = join.toString(); // and  or
                switch (name) {
                    case "OR":
                        sql += s+" (";
                        String[] split = fieldName.split(",");
                        for (String field : split) {
                            if (field.equals(split[split.length-1])){
                                sql += String.format(" %s LIKE '%s' ",field,"%"+value+"%" );
                            }else {
                                sql += String.format(" %s LIKE '%s' OR ",field,"%"+value+"%" );
                            }
                        }
                        sql += ") ";
                        break;
                    case "ISNULL":
                        sql += String.format(" %s %s is null ",s,fieldName);
                        break;
                    case "EQ":
                        sql += String.format(" %s %s = '%s' ",s,fieldName,value);
                        break;
                    case "NE":
                        sql += String.format(" %s %s != '%s' ",s,fieldName,value);
                        break;
                    case "GTE":
                        sql += String.format(" %s %s >= '%s' ",s,fieldName,value);
                        break;
                    case "LTE":
                        sql += String.format(" %s %s <= '%s' ",s,fieldName,value);
                        break;
                    case "GT":
                        sql += String.format(" %s %s > '%s' ",s,fieldName,value);
                        break;
                    case "LT":
                        sql += String.format(" %s %s < '%s' ",s,fieldName,value);
                        break;
                    case "IN":
                        if (value.getClass().isArray()) {

                            String[] value1 = (String[]) value;
                            String val = " '" + value1[0] + "' ";
                            for (int i = 1; i < value1.length; i++) {
                                val += " ,'" + value1[i] + "' ";
                            }

                            sql += String.format(" %s %s in ( %s )",s,fieldName,val);
                        }else{
                            sql += String.format(" %s %s in ( %s )",s,fieldName,value);
                        }
                        break;
                    case "NOTIN":
                        if (value.getClass().isArray()) {

                            String[] value1 = (String[]) value;
                            String val= " '" + value1[0] + "' ";
                            for (int i = 1; i < value1.length; i++) {
                                val += " ,'" + value1[i] + "' ";
                            }

                            sql += String.format(" %s %s NOT IN ( %s )",s,fieldName,val);
                        } else {
                            sql += String.format(" %s %s NOT IN ( %s )",s,fieldName,value);
                        }

                        break;
                    case "LIKE":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName,"%"+value+"%" );
                        break;
                    case "LIKEL":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName, value+"%" );
                        break;
                    case "LIKER":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName, "%"+value );
                        break;
                    case "BETWEEN":
                        if (value instanceof Date[]) {
                            Date[] dateArray = (Date[]) value;
                            sql += String.format(" %s ( %s BETWEEN '%s' and '%s' ) ",s,fieldName, dateArray[0], dateArray[1] );
                        }
                        break;
                }
            }
        }
        return sql;
    }


    /**
     * sql 修改or的条件拼接
     * @param sql
     * @return
     * zx
     */
    public static String newToAddSql(String sql, List<SearchFilter> filters) {
        if( !sql.contains( " where 1=1 " ) )
            sql = String.format(" %s where 1=1 ",sql);
        if (filters != null) {
            for (SearchFilter filter : filters) {
                String fieldName = filter.fieldName;
                Object value = filter.value;
                SearchFilter.Operator operator = filter.operator;
                String name = operator.name();
                if (!name.equals("ISNULL")&&value == null) continue;

                SearchFilter.Join join = filter.join;
                String s = join.toString(); // and  or
                switch (name) {
                    case "OR":
                        sql += s+" (";
                        String[] split = value.toString().split(",");
                        for (String field : split) {
                            if (field.equals(split[split.length-1])){
                                sql += String.format(" %s  = '%s' ",fieldName,field );
                            }else {
                                sql += String.format(" %s  = '%s' OR ",fieldName,field );
                            }
                        }
                        sql += ") ";
                        break;
                    case "ISNULL":
                        sql += String.format(" %s %s is null ",s,fieldName);
                        break;
                    case "EQ":
                        sql += String.format(" %s %s = '%s' ",s,fieldName,value);
                        break;
                    case "NE":
                        sql += String.format(" %s %s != '%s' ",s,fieldName,value);
                        break;
                    case "GTE":
                        sql += String.format(" %s %s >= '%s' ",s,fieldName,value);
                        break;
                    case "LTE":
                        sql += String.format(" %s %s <= '%s' ",s,fieldName,value);
                        break;
                    case "GT":
                        sql += String.format(" %s %s > '%s' ",s,fieldName,value);
                        break;
                    case "LT":
                        sql += String.format(" %s %s < '%s' ",s,fieldName,value);
                        break;
                    case "IN":
                        if (value.getClass().isArray()) {

                            String[] value1 = (String[]) value;
                            String val = " '" + value1[0] + "' ";
                            for (int i = 1; i < value1.length; i++) {
                                val += " ,'" + value1[i] + "' ";
                            }

                            sql += String.format(" %s %s in ( %s )",s,fieldName,val);
                        }else{
                            sql += String.format(" %s %s in ( %s )",s,fieldName,value);
                        }
                        break;
                    case "NOTIN":
                        if (value.getClass().isArray()) {

                            String[] value1 = (String[]) value;
                            String val= " '" + value1[0] + "' ";
                            for (int i = 1; i < value1.length; i++) {
                                val += " ,'" + value1[i] + "' ";
                            }

                            sql += String.format(" %s %s NOT IN ( %s )",s,fieldName,val);
                        } else {
                            sql += String.format(" %s %s NOT IN ( %s )",s,fieldName,value);
                        }

                        break;
                    case "LIKE":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName,"%"+value+"%" );
                        break;
                    case "LIKEL":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName, value+"%" );
                        break;
                    case "LIKER":
                        sql += String.format(" %s %s LIKE '%s' ",s,fieldName, "%"+value );
                        break;
                    case "BETWEEN":
                        if (value instanceof Date[]) {
                            Date[] dateArray = (Date[]) value;
                            sql += String.format(" %s ( %s BETWEEN '%s' and '%s' ) ",s,fieldName, dateArray[0], dateArray[1] );
                        }
                        break;
                }
            }
        }
        return sql;
    }

    /**
     * sql 分页$排序拼接
     *
     * @param page     当前页
     * @param pageSize 每页数据
     * @param sql      源sql
     * @param defaultSort  默认排序字段
     * @return
     */
    public static String toSqlLimit(int page, int pageSize, String sql, String defaultSort) {
        return toSqlLimit(page, pageSize, sql, defaultSort,null, null);
    }

    /**
     * sql 分页$排序拼接
     *
     * @param page 当前页
     * @param pageSize  每页数据
     * @param sql  源sql
     * @param sort  DESC 或者 ASC
     * @param defaultSort 默认排序字段
     * @return
     */
    public static String toSqlLimit(int page, int pageSize, String sql, String defaultSort, String sort) {
        return toSqlLimit(page, pageSize, sql, defaultSort, sort,null);
    }

    /**
     * sql 分页$排序拼接
     *
     * @param page     当前页
     * @param pageSize 每页数据
     * @param sql      源sql
     * @param sort     DESC 或者 ASC
     * @param defaultSort  默认排序字段
     * @return
     */
    public static String toSqlLimit(int page, int pageSize, String sql, String defaultSort, String sortstr,String... sort) {
        if (StringUtil.isEmpty(sortstr)) {
            sortstr = "DESC";
        }
        if (sort != null) {
            String par = sort[0];
            for (int i = 1; i < sort.length; i++) {
                if( !sort[i].equals( "" ) ){
                    if( par.equals("") ){
                        par = sort[i];
                    }else{
                        par += "," + sort[i];
                    }

                }
            }
            sql += String.format(" order by %s %s limit %s , %s  ", StringUtil.isEmpty( par ) ? par : defaultSort , sortstr,(page - 1) * pageSize, pageSize);
        } else {
            sql += String.format(" order by %s %s limit %s , %s  ", defaultSort, sortstr , (page - 1) * pageSize, pageSize);
        }
        return sql;
    }

    /**
     * 分组
     * @param sql
     * @param sort
     */
    public static String toSqlGroupBy( String sql, String... sort ) {
        if (sort != null) {
            String par = sort[0];
            for (int i = 1; i < sort.length; i++) {
                if(sort[i] != ""){
                    par += "," + sort[i];
                }
            }
            sql += String.format(" group by %s' ", ( par ) );
        }
       return sql;
    }





}
