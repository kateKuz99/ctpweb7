package com.bsuir.web7lab.command.authorithation;

import com.bsuir.web7lab.command.Command;

import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.AUTHENTICATION_ERROR_TEXT;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.COMMAND_WELCOME;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.ERROR_MESSAGE;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.ERROR_MESSAGE_TEXT;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.LOGIN;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.PASSWORD;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.bsuir.web7lab.command.CommandResult;
import com.bsuir.web7lab.command.session.SessionAttribute;
import com.bsuir.web7lab.exception.IncorrectDataException;
import com.bsuir.web7lab.exception.ServiceException;
import com.bsuir.web7lab.model.User;
import com.bsuir.web7lab.service.UserService;
import com.bsuir.web7lab.util.HashPassword;
import com.bsuir.web7lab.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class LoginCommand implements Command {
    private static final Logger logger = LogManager.getLogger(LoginCommand.class.getName());

    private void setAttributesToSession(String name, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionAttribute.NAME, name);
    }

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException, IncorrectDataException, IOException {
        boolean isUserFind = false;
        Optional<String> login = of(request).map(httpServletRequest -> httpServletRequest.getParameter(LOGIN));
        System.out.println(login.get());
        Optional<String> password = of(request).map(httpServletRequest -> httpServletRequest.getParameter(PASSWORD));
        if (isEmpty(login.get()) || isEmpty(password.get())) {
            return forwardLoginWithError(request, ERROR_MESSAGE, ERROR_MESSAGE_TEXT);
        }
        byte[] pass = HashPassword.getHash(password.get());
        isUserFind = initializeUserIfExist(login.get(), pass, request);
        if (!isUserFind) {
            logger.info("user with such login and password doesn't exist");
            return forwardLoginWithError(request, ERROR_MESSAGE, AUTHENTICATION_ERROR_TEXT);
        } else {
            logger.info("user has been authorized: login:" + login + " password:" + password);
            return new CommandResult(COMMAND_WELCOME, false);
        }
    }

    public boolean initializeUserIfExist(String login, byte[] password, HttpServletRequest request) throws ServiceException {
        UserService userService = new UserService();
        Optional<User> user = userService.login(login, password);
        boolean userExist = false;
        if (user.isPresent()) {
            setAttributesToSession(user.get().getLogin(), request);
            userExist = true;
        }
        return userExist;
    }

    private CommandResult forwardLoginWithError(HttpServletRequest request, final String ERROR, final String ERROR_MESSAGE) {
        request.setAttribute(ERROR, ERROR_MESSAGE);
        return new CommandResult(Page.LOGIN_PAGE.getPage(), false);
    }
}