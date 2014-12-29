package com.mgjg.kmztracker.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;

/**
 * obsolete - v1 map api
 * @author Jay Goldman
 *
 */
@Deprecated
public class LocationOverlay extends ItemizedOverlay<OverlayItem>
{

    private final Activity mapActivity;
    private final List<OverlayItem> overlays = new ArrayList<OverlayItem>();

    public LocationOverlay(Activity mapActivity, Drawable defaultMarker)
    {
        super(boundCenterBottom(defaultMarker));
        this.mapActivity = mapActivity;
    }

    public void addOverlay(OverlayItem overlay)
    {
        overlays.add(overlay);
        populate();
    }

    public void clear()
    {
        overlays.clear();
        populate();
    }

    @Override
    protected OverlayItem createItem(int ii)
    {
        return overlays.get(ii);
    }

    @Override
    public int size()
    {
        return overlays.size();
    }

    @Override
    protected boolean onTap(int index)
    {
        OverlayItem item = overlays.get(index);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mapActivity.getApplicationContext());
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.show();
        return true;
    }
}