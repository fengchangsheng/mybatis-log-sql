package com.fcs;


import com.fcs.hibernate.BasicFormatterImpl;
import com.fcs.hibernate.Formatter;
import com.fcs.util.RestoreSqlUtil;
import com.fcs.util.StringConst;
import org.apache.commons.lang3.StringUtils;

/**
 * restore sql from selection
 * @author ob
 */
public class RestoreSqlForSelection {
    private static String preparingLine = "";
    private static String parametersLine = "";
    private static boolean isEnd = false;

    public static void main(String[] args) {
        RestoreSqlForSelection sqlForSelection = new RestoreSqlForSelection();
//        String sql = "2017-06-23 14:31:27.729 ERROR notParamTest - ==>  Preparing: INSERT INTO t_ml_vop_bil_interface (a,b,c) VALUES (?,?,?)\n";
//        String param = "2017-06-23 14:31:27.729 ERROR notParamTest - ==>  Parameters: 996aep(String), {succ,?,ess=1}(String), 2017-06-29(Timestamp)\n";
        if (args == null || args.length == 0) {
            System.out.println("u are donkey, please put in mybatis log sql sentence which contains Preparing and Parameters.");
            return;
        }
        String sqlText = args[0];
        sqlForSelection.actionPerformed(sqlText);
    }


    public void actionPerformed(String sqlText) {
        //激活Restore Sql tab
        final String PREPARING = StringConst.PREPARING;
        final String PARAMETERS = StringConst.PARAMETERS;
        if(StringUtils.isNotBlank(sqlText) && sqlText.contains(PREPARING) && sqlText.contains(PARAMETERS)) {
            String[] sqlArr = sqlText.split("\n");
            if(sqlArr != null && sqlArr.length >= 2) {
                for(int i=0; i<sqlArr.length; ++i) {
                    String currentLine = sqlArr[i];
                    if(StringUtils.isBlank(currentLine)) {
                        continue;
                    }
                    if(currentLine.contains(PREPARING)) {
                        preparingLine = currentLine;
                        continue;
                    } else {
                        currentLine += "\n";
                    }
                    if(StringUtils.isEmpty(preparingLine)) {
                        continue;
                    }
                    if(currentLine.contains(PARAMETERS)) {
                        parametersLine = currentLine;
                    } else {
                        if(StringUtils.isBlank(parametersLine)) {
                            continue;
                        }
                        parametersLine += currentLine;
                    }
                    if(!parametersLine.endsWith("Parameters: \n") && !parametersLine.endsWith("null\n") && !RestoreSqlUtil.endWithAssembledTypes(parametersLine)) {
                        if(i == sqlArr.length -1) {
                            System.out.println("Can't restore sql from selection.");
                        }
                        continue;
                    } else {
                        isEnd = true;
                    }
                    if(StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine) && isEnd) {
                        String preStr = "  restore sql from selection  - ==>";
                        System.out.println(preStr);
                        System.out.println("======================================================================");
                        String restoreSql = RestoreSqlUtil.restoreSql(preparingLine, parametersLine);
//                        if(ConfigUtil.getSqlFormat(project)) {
//                            restoreSql = PrintUtil.format(restoreSql);
//                        }
                        restoreSql = format(restoreSql);
                        System.out.println(restoreSql);
                    }
                }
            } else {
                System.out.println("Can't restore sql from selection.");
            }
        } else {
            System.out.println("Can't restore sql from selection.");
        }
    }

    private void reset(){
        preparingLine = "";
        parametersLine = "";
        isEnd = false;
    }

    public static String format(String sql) {
        Formatter formatter = new BasicFormatterImpl();
        return formatter.format(sql);
    }
}