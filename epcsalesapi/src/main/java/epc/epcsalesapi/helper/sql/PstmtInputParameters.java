package epc.epcsalesapi.helper.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PstmtInputParameters {
    
    
    private ArrayList<InputParameter> parameterList = new ArrayList();
    
    public void setParameter(int i, Object parameter) {
        InputParameter inputParameter = new InputParameter();
        int sqlType = 0;
        inputParameter.setInputObject(parameter);
        if (parameter instanceof String) {
            sqlType = java.sql.Types.VARCHAR;
        } else if (parameter instanceof LocalDateTime) {
            sqlType = java.sql.Types.DATE;
        } else if (parameter instanceof java.util.Date) { 
            sqlType = java.sql.Types.DATE;
        } else if (parameter instanceof BigDecimal) {
            sqlType = java.sql.Types.DECIMAL;
        } else if (parameter instanceof Float) {
            sqlType = java.sql.Types.FLOAT;
        } else if (parameter instanceof Double) {
            sqlType = java.sql.Types.DOUBLE;
        } else if (parameter instanceof BigInteger) {
            sqlType = java.sql.Types.INTEGER;
        } else if (parameter instanceof Integer) {
            sqlType = java.sql.Types.INTEGER;
        }
        inputParameter.setInputSqlType(sqlType);
        parameterList.add(inputParameter);
    }
    
    public ArrayList<InputParameter> getParameterList() {
        return parameterList;
    }

}
