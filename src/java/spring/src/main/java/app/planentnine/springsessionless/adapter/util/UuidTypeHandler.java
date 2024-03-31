package app.planentnine.springsessionless.adapter.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class UuidTypeHandler extends BaseTypeHandler<UUID> {
    public UuidTypeHandler() {
    }
    
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }
    
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return (UUID)rs.getObject(columnName, UUID.class);
    }
    
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return (UUID)rs.getObject(columnIndex, UUID.class);
    }
    
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return (UUID)cs.getObject(columnIndex, UUID.class);
    }
}