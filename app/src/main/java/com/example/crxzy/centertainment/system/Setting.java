package com.example.crxzy.centertainment.system;


import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.controllers.Index;

public class Setting {
    public static final class autoload {
        static final public class keyword {
            public static final String header = "header";
            public static final String root = "index";
            public static final String[] page = new String[]{"history", "main", "subscribe"};
        }

        public static final int layout = R.layout.index;
        public static final Class <?> indexController = Index.class;
    }
}
