package net.skysemi.englishwordsdb;

import android.provider.BaseColumns;

/**
 * Created by mori on 2014/08/02.
 */
public interface Word extends BaseColumns {
    public static final String TB_NAME = "t_words";
    public static final String WORD = "f_word";
    public static final String SITE_ID = "f_site_id";
}
