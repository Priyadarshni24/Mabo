package com.advengers.mabo.Cometchat.Utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {

    public static Typeface robotoMedium;

    public static Typeface openSansRegular;

    public static Typeface robotoRegular;

    public static Typeface robotoCondenseRegular;

    private static Context context;

    public FontUtils(Context c) {

        context=c;

        initFonts();
    }

    private void initFonts() {

          if (context!=null) {
              robotoMedium = Typeface.createFromAsset(context.getAssets(), "gibsonregular.ttf");
              robotoRegular = Typeface.createFromAsset(context.getAssets(), "gibsonregular.ttf");
              openSansRegular = Typeface.createFromAsset(context.getAssets(), "gibsonregular.ttf");
              robotoCondenseRegular = Typeface.createFromAsset(context.getAssets(), "gibsonregular.ttf");
          }
    }
}
