package com.bsuir.web7lab.repository;

import com.bsuir.web7lab.builder.UserBuilder;
import com.bsuir.web7lab.exception.RepositoryException;
import com.bsuir.web7lab.model.User;
import com.bsuir.web7lab.repository.dbconstans.UserTableConstants;
import com.bsuir.web7lab.repository.specification.Parameter;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepository extends AbstractRepository<User> {
    private static final String TABLE_NAME = "users";

    public UserRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<User> query(String sqlString, Parameter paramater) throws RepositoryException {
        String query = sqlString;
        List<User> users = executeQuery(query, new UserBuilder(), paramater.getParameters());
        return users;
    }

    @Override
    public Optional<User> queryForSingleResult(String sqlString, Parameter parameter) throws RepositoryException {
        List<User> user = query(sqlString, parameter);
        return user.size() == 1 ? Optional.of(user.get(0)) : Optional.empty();
    }

    public Map<String, Object> getFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(UserTableConstants.LOGIN.getFieldName(), user.getLogin());
        fields.put(UserTableConstants.PASSWORD.getFieldName(), user.getPassw());
        return fields;
    }
}