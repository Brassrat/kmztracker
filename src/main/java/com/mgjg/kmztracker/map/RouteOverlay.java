package com.mgjg.kmztracker.map;

//import android.graphics.Bitmap;

import android.graphics.*;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public abstract class RouteOverlay extends Overlay
{

  protected int ovalRadius = 6;
  protected int lineWidth = 5;

  protected final GeoPoint gp1;
  protected final int defaultColor;
  protected String text = "";
  protected Bitmap img = null;

  private RouteOverlay(GeoPoint gp1, int defaultColor)
  {
    this.gp1 = gp1;
    this.defaultColor = defaultColor;
  }

  public RouteOverlay withText(String t)
  {
    this.text = t;
    return this;
  }

  public RouteOverlay withBitmap(Bitmap bitmap)
  {
    this.img = bitmap;
    return this;
  }

  @Override
  public boolean draw(Canvas canvas, MapView view, boolean shadow, long when)
  {
    // v2 com.google.android.gms.maps.Projection projection = GoogleMap.getProjection();
    com.google.android.maps.Projection projection = view.getProjection();
    if (shadow == false)
    {
      Paint paint = new Paint();
      paint.setAntiAlias(true);
      Point point = new Point();
      projection.toPixels(gp1, point);
      // v2 Point point = projection.toScreenLocation(gp1);
      // mode=1&#65306;start
      myDraw(canvas, view, projection, paint, point);
      if (!text.isEmpty())
      {
        canvas.drawText(text, point.x, point.y, paint);
      }

    }
    return super.draw(canvas, view, shadow, when);
  }

  protected abstract void myDraw(Canvas canvas, MapView view, com.google.android.maps.Projection projection, Paint paint, Point point);

  public static class StartOverlay extends RouteOverlay
  {

    public StartOverlay(GeoPoint gp1)
    {
      this(gp1, 999);
    }

    public StartOverlay(GeoPoint gp1, int defaultColor)
    {
      super(gp1, defaultColor);
    }

    @Override
    public void myDraw(Canvas canvas, MapView view, Projection projection, Paint paint, Point point)
    {

      // mode=1&#65306;start
      paint.setColor((defaultColor == 999) ? Color.BLACK : defaultColor);
      if (null != img)
      {
        canvas.drawBitmap(img, point.x - (img.getWidth() / 2), point.y - (img.getHeight() / 2), paint);
      }
      else
      {
        RectF oval = new RectF(point.x - ovalRadius, point.y - ovalRadius,
            point.x + ovalRadius, point.y + ovalRadius);
        // start point
        canvas.drawOval(oval, paint);
      }

    }
  }

  public static class MarkOverlay extends RouteOverlay
  {

    public MarkOverlay(GeoPoint gp1)
    {
      this(gp1, 999);
    }

    public MarkOverlay(GeoPoint gp1, int defaultColor)
    {
      super(gp1, defaultColor);
    }

    @Override
    public void myDraw(Canvas canvas, MapView view, com.google.android.maps.Projection projection, Paint paint, Point point)
    {

      // mode=1&#65306;start
      paint.setColor((defaultColor == 999) ? Color.BLACK : defaultColor);
      if (null != img)
      {
        canvas.drawBitmap(img, point.x - (img.getWidth() / 2), point.y - (img.getHeight() / 2), paint);
      }
      // else
      // {
      // RectF oval = new RectF(point.x - ovalRadius, point.y - ovalRadius,
      // point.x + ovalRadius, point.y + ovalRadius);
      // // start point
      // canvas.drawOval(oval, paint);
      // }

    }
  }

  public static class LineOverlay extends RouteOverlay
  {

    protected final GeoPoint gp2; // GeoPoint is a int. (6E)

    public LineOverlay(GeoPoint gp1, GeoPoint gp2)
    {
      this(gp1, gp2, 999);
    }

    public LineOverlay(GeoPoint gp1, GeoPoint gp2, int defaultColor)
    {
      super(gp1, defaultColor);
      this.gp2 = gp2;
    }

    @Override
    public void myDraw(Canvas canvas, MapView view, com.google.android.maps.Projection projection, Paint paint, Point point)
    {
      // mode=2&#65306;path

      paint.setColor((defaultColor == 999) ? Color.RED : defaultColor);
      Point point2 = new Point();
      projection.toPixels(gp2, point2);
      // v2 Point point2 = projection.toScreenLocation(gp2);

      paint.setStrokeWidth(lineWidth);
      paint.setAlpha(defaultColor == Color.parseColor("#6C8715") ? 220 : 120);
      canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);

    }
  }

  public static class LoopEndOverlay extends RouteOverlay
  {

    protected final GeoPoint gp2;

    public LoopEndOverlay(GeoPoint gp1, GeoPoint gp2)
    {
      this(gp1, gp2, 999);
    }

    public LoopEndOverlay(GeoPoint gp1, GeoPoint gp2, int defaultColor)
    {
      super(gp1, defaultColor);
      this.gp2 = gp2;
    }

    @Override
    public void myDraw(Canvas canvas, MapView view, com.google.android.maps.Projection projection, Paint paint, Point point)
    {

            /* mode=3&#65306;end */

            /* the last path */

      paint.setColor((defaultColor == 999) ? Color.BLACK : defaultColor);
      // if (defaultColor == 999)
      // paint.setColor(Color.BLACK); // Color.GREEN
      // else
      // paint.setColor(defaultColor);

      Point point2 = new Point();
      projection.toPixels(gp2, point2);
      // v2 Point point2 = projection.toScreenLocation(gp2);
      paint.setStrokeWidth(lineWidth);
      paint.setAlpha(defaultColor == Color.parseColor("#6C8715") ? 220 : 120);
      canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);

      if (null != img)
      {
        canvas.drawBitmap(img, point.x - ovalRadius - (img.getWidth() / 2), point.y + ovalRadius - (img.getHeight() / 2), paint);
      }
      // else
      // {
      // RectF oval = new RectF(point2.x - ovalRadius, point2.y - ovalRadius,
      // point2.x + ovalRadius, point2.y + ovalRadius);
      // /* end point */
      // paint.setAlpha(255);
      // canvas.drawOval(oval, paint);
      // }

    }

  }

  public static class EndOverlay extends RouteOverlay
  {

    public EndOverlay(GeoPoint gp1)
    {
      this(gp1, 999);
    }

    public EndOverlay(GeoPoint gp1, int defaultColor)
    {
      super(gp1, defaultColor);
    }

    @Override
    public void myDraw(Canvas canvas, MapView view, com.google.android.maps.Projection projection, Paint paint, Point point)
    {

            /* mode=3&#65306;end */

            /* the last path */

      paint.setColor((defaultColor == 999) ? Color.BLACK : defaultColor);
      // if (defaultColor == 999)
      // paint.setColor(Color.BLACK); // Color.GREEN
      // else
      // paint.setColor(defaultColor);

      // Point point2 = new Point();
      // projection.toPixels(gp2, point2);
      // paint.setStrokeWidth(lineWidth);
      // paint.setAlpha(defaultColor == Color.parseColor("#6C8715") ? 220 : 120);
      // canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);

      if (null != img)
      {
        canvas.drawBitmap(img, point.x - (img.getWidth() / 2), point.y - (img.getHeight() / 2), paint);
      }
      else
      {
        RectF oval = new RectF(point.x - ovalRadius, point.y - ovalRadius,
            point.x + ovalRadius, point.y + ovalRadius);
                /* end point */
        paint.setAlpha(255);
        canvas.drawOval(oval, paint);
      }

    }

  }
}