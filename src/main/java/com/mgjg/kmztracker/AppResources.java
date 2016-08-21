package com.mgjg.kmztracker;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;

/**
 * Created by marianne on 1/9/2016.
 */
public class AppResources
{

    public static Resources getResources()
    {
        return MainActivity.getInstance().getResources();
    }

    public static int getColor(int colorResource)
    {
        try
        {
            return MainActivity.getInstance().getResources().getColor(colorResource);
        }
        catch(Resources.NotFoundException e)
        {

        }
        return 0;
    }
}
