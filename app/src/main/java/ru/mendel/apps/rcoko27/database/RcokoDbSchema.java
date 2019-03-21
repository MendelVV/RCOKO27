package ru.mendel.apps.rcoko27.database;

/**
 * Created by Мендель Василий on 14.06.2017.
 */

public class RcokoDbSchema {

    public static class TableEvents {
        public static final String NAME = "events";

        public static class Cols {
            public static final String CODE = "code";
            public static final String TITLE = "title";
            public static final String TEXT = "text";
            public static final String DATE_EVENT = "date_event";
            public static final String DATE_NEWS = "date_news";
            public static final String TYPE = "type";
            public static final String ICON = "icon";
            public static final String STATE = "state";
        }
    }

    public static class TableVoting {
        public static final String NAME = "voting";

        public static class Cols {
            public static final String CODE = "code";
            public static final String EVENT = "event";
            public static final String TEXT = "text";
        }
    }

    public static class TablePossibles {
        public static final String NAME = "possibles";

        public static class Cols {
            public static final String CODE = "code";
            public static final String VOTING = "voting";
            public static final String TEXT = "text";
        }
    }

    public static class TableAnswers {
        public static final String NAME = "answers";

        public static class Cols {
            public static final String VOTING = "voting";
            public static final String TEXT = "text";
            public static final String SIZE = "size";
        }
    }

    public static class TableMessages {
        public static final String NAME = "messages";

        public static class Cols {
            public static final String CODE = "code";
            public static final String EVENT = "event";
            public static final String DATE = "date";
            public static final String AUTHOR = "author";
            public static final String AUTHOR_NAME = "author_name";
            public static final String RECIPIENT = "recipient";
            public static final String RECIPIENT_NAME = "recipient_name";
            public static final String TEXT = "text";
            public static final String UUID = "uuid";
            public static final String STATE = "state";

        }
    }

}
