package ru.mendel.apps.rcoko27.json;

/**
 * Created by Мендель Василий on 14.06.2017.
 */

public class JsonSchema {

    public static class Registration{
        public static final String APPNAME = "appname";
        public static final String ACTION = "action";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String CODE = "code";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";
    }

    public static class GetData{
        public static final String APPNAME = "appname";
        public static final String ACTION = "action";
        public static final String START = "start";
        public static final String SIZE = "size";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }

    public static class GetEvent{
        public static final String APPNAME = "appname";
        public static final String ACTION = "action";
        public static final String CODE = "code";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }

    public static class SendMessage{
        public static final String APPNAME = "appname";
        public static final String ACTION = "action";
        public static final String PASSWORD = "password";
        public static final String CODE = "code";
        public static final String EVENT = "event";
        public static final String DATE = "date";
        public static final String AUTHOR = "author";
        public static final String AUTHOR_NAME = "authorname";
        public static final String RECIPIENT = "recipient";
        public static final String RECIPIENT_NAME = "recipientname";
        public static final String TEXT = "text";
        public static final String UUID = "uuid";
        public static final String STATE = "state";
    }

    public static class Response{
        public static final String RESULT = "result";
        public static final String ACTION = "action";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String EVENT = "event";
        public static final String VOTING_COUNT = "votingcount";
        public static final String VOTING = "voting";
        public static final String MESSAGES_COUNT = "messagescount";
        public static final String MESSAGES = "messages";
    }
}
