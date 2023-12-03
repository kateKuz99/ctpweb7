package com.bsuir.web7lab.service;

import com.bsuir.web7lab.exception.RepositoryException;
import com.bsuir.web7lab.exception.ServiceException;
import com.bsuir.web7lab.model.User;
import com.bsuir.web7lab.repository.RepositoryCreator;
import com.bsuir.web7lab.repository.SQLHelper;
import com.bsuir.web7lab.repository.UserRepository;
import com.bsuir.web7lab.repository.specification.UserByLogin;
import com.bsuir.web7lab.repository.specification.UserByLoginPassword;

import java.util.Optional;

public class UserService {
    public Optional<User> login(String login, byte[] password) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            UserRepository userRepository = repositoryCreator.getUserRepository();
            UserByLoginPassword params = new UserByLoginPassword(login, password);
            return userRepository.queryForSingleResult(SQLHelper.SQL_GET_USER, params);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public Integer save(User user) throws ServiceException {
        try (RepositoryCreator repositoryCreator = new RepositoryCreator()) {
            UserRepository userRepository = repositoryCreator.getUserRepository();
            UserByLogin param = new UserByLogin(user.getLogin());
            System.out.println(!userRepository.queryForSingleResult(SQLHelper.SQL_CHECK_LOGIN, param).isPresent());
            if (!userRepository.queryForSingleResult(SQLHelper.SQL_CHECK_LOGIN, param).isPresent()) {
                return userRepository.save(user);
            } else {
                return 0;
            }
        } catch (RepositoryException exception) {
            throw new ServiceException(exception.getMessage(), exception);
        }
    }

}