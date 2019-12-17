package ws.epod.ObjectClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageClass {
    public static void  setLanguage(Context context){
        SharedPreferences Language_Locale = context.getSharedPreferences("PREFERENCE_LANGUAGE", Context.MODE_PRIVATE);
        String language = Language_Locale.getString("LANGUAGE_KEY", "ENGLISH");
        if ( language.equals("ENGLISH") ) {
            Configuration config = new Configuration();
            config.locale = Locale.ENGLISH;
            context.getResources().updateConfiguration(config, null);
            Language_Locale.edit().putString("LANGUAGE_KEY", "ENGLISH").apply();
        } else {
            Configuration config = new Configuration();
            config.locale = new Locale("th");
            context.getResources().updateConfiguration(config, null);
            Language_Locale.edit().putString("LANGUAGE_KEY", "THAI").apply();
        }
    }

}

