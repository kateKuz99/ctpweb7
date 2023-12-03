package com.bsuir.web7lab.command.authorithation;

import com.bsuir.web7lab.command.Command;
import com.bsuir.web7lab.command.CommandResult;
import com.bsuir.web7lab.exception.IncorrectDataException;
import com.bsuir.web7lab.exception.ServiceException;
import com.bsuir.web7lab.model.User;
import com.bsuir.web7lab.service.UserService;
import com.bsuir.web7lab.util.HashPassword;
import com.bsuir.web7lab.util.pages.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.ERROR_MESSAGE_TEXT;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.NAME_FOR_REGISTER;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.PASSWORD_FOR_REGISTER;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.REGISTER_ERROR;
import static com.bsuir.web7lab.command.authorithation.constants.AuthConstants.REGISTER_ERROR_MESSAGE_IF_EXIST;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class RegisterNewUserCommand implements Command {
    private static final Logger logger = LogManager.getLogger(RegisterNewUserCommand.class.getName());

    private CommandResult forwardToRegisterWithError(HttpServletRequest request, String ERROR, String ERROR_MESSAGE) {
        request.setAttribute(ERROR, ERROR_MESSAGE);
        return new CommandResult(Page.REGISTER_PAGE.getPage(), false);
    }

    private CommandResult forwardToLogin(HttpServletRequest request) {
        return new CommandResult(Page.LOGIN_PAGE.getPage(), false);
    }

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException, IncorrectDataException {
        Optional<String> login = of(request).map(httpServletRequest -> httpServletRequest.getParameter(NAME_FOR_REGISTER));
        Optional<String> password = of(request).map(httpServletRequest -> httpServletRequest.getParameter(PASSWORD_FOR_REGISTER));
        if (isEmpty(login.get()) || isEmpty(password.get())) {
            logger.info("invalid login or password format was received:" + login + " " + password);
            return forwardToRegisterWithError(request, REGISTER_ERROR, ERROR_MESSAGE_TEXT);
        }
        byte[] pass = HashPassword.getHash(password.get());
        User user = new User(login.get(), pass);
        UserService userService = new UserService();
        int userCount = userService.save(user);
        if (userCount != 0) {
            logger.info("user was registered: login:" + login + " password:" + password);
            return forwardToLogin(request);
        } else {
            logger.info("invalid login or password format was received:" + login + " " + password);
            return forwardToRegisterWithError(request, REGISTER_ERROR, REGISTER_ERROR_MESSAGE_IF_EXIST);
        }
    }
}