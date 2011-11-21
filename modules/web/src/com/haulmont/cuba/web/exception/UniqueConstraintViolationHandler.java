/*
 * Copyright (c) 2011 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.cuba.web.exception;

import com.haulmont.cuba.core.global.MessageUtils;
import com.vaadin.terminal.Terminal;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.gui.ServiceLocator;
import com.haulmont.cuba.core.global.MessageProvider;
import com.vaadin.ui.Window;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Handles database unique constraint violations. Determines the exception type by searching a special marker string
 * in the messages of all exceptions in the chain.
 *
 * <p>$Id$</p>
 *
 * @author krivopustov
 */
public class UniqueConstraintViolationHandler implements ExceptionHandler
{
    private String marker;

    private Pattern pattern;

    private String getMarker() {
        if (marker == null) {
            marker = ServiceLocator.getDataService().getDbDialect().getUniqueConstraintViolationMarker();
        }
        return marker;
    }

    private Pattern getPattern() {
        if (pattern == null) {
            String s = ServiceLocator.getDataService().getDbDialect().getUniqueConstraintViolationPattern();
            pattern = Pattern.compile(s);
        }
        return pattern;
    }

    @Override
    public boolean handle(Terminal.ErrorEvent event, App app) {
        Throwable t = event.getThrowable();
        try {
            while (t != null) {
                if (t.toString().contains(getMarker())) {
                    doHandle(t, app);
                    return true;
                }
                t = t.getCause();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void doHandle(Throwable throwable, App app) {
        String constraintName = "";
        Matcher matcher = getPattern().matcher(throwable.toString());
        if (matcher.find()) {
            if (matcher.groupCount() > 1)
                constraintName = matcher.group(2);
            else
                constraintName = matcher.group(1);
        }

        String msg = "";
        if (StringUtils.isNotBlank(constraintName)) {
            msg = MessageProvider.getMessage(MessageUtils.getMessagePack(), constraintName.toUpperCase());
        }

        if (msg.equalsIgnoreCase(constraintName)) {
            msg = MessageProvider.getMessage(getClass(), "uniqueConstraintViolation.message");
            if (StringUtils.isNotBlank(constraintName))
                msg = msg + " (" + constraintName + ")";
        }

        app.getAppWindow().showNotification(msg, Window.Notification.TYPE_ERROR_MESSAGE);
    }
}
