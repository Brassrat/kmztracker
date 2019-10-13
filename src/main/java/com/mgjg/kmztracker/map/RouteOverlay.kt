package com.mgjg.kmztracker.map

//import android.graphics.Bitmap;

import android.graphics.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng

abstract class RouteOverlay private constructor(
  protected val gp1: LatLng,
  protected val defaultColor: Int
) {

  protected var ovalRadius = 6
  protected var lineWidth = 5
  protected var text = ""
  protected var img: Bitmap? = null

  fun withText(t: String): RouteOverlay {
    this.text = t
    return this
  }

  fun withBitmap(bitmap: Bitmap): RouteOverlay {
    this.img = bitmap
    return this
  }

//  override fun draw(canvas: Canvas?, view: GoogleMap?, shadow: Boolean, `when`: Long): Boolean {
//    // v2 Projection projection = GoogleMap.getProjection();
//    val projection = view!!.projection
//    if (shadow == false) {
//      val paint = Paint()
//      paint.isAntiAlias = true
//      //val point = Point(); projection.toPixels(gp1, point)
//      val point = projection.toScreenLocation(gp1);
//      // v2 Point point = projection.toScreenLocation(gp1);
//      // mode=1&#65306;start
//      myDraw(canvas, view, projection, paint, point)
//      if (!text.isEmpty()) {
//        canvas!!.drawText(text, point.x.toFloat(), point.y.toFloat(), paint)
//      }
//
//    }
//    return super.draw(canvas, view, shadow, `when`)
//  }

  protected abstract fun myDraw(
    canvas: Canvas?,
    view: GoogleMap,
    projection: Projection,
    paint: Paint,
    point: Point
  )

  class StartOverlay @JvmOverloads constructor(gp1: LatLng, defaultColor: Int = 999) :
    RouteOverlay(gp1, defaultColor) {

    public override fun myDraw(
      canvas: Canvas?,
      view: GoogleMap,
      projection: Projection,
      paint: Paint,
      point: Point
    ) {

      // mode=1&#65306;start
      paint.color = if (defaultColor == 999) Color.BLACK else defaultColor
      if (null != img) {
        canvas!!.drawBitmap(
          img!!,
          (point.x - img!!.width / 2).toFloat(),
          (point.y - img!!.height / 2).toFloat(),
          paint
        )
      } else {
        val oval = RectF(
          (point.x - ovalRadius).toFloat(), (point.y - ovalRadius).toFloat(),
          (point.x + ovalRadius).toFloat(), (point.y + ovalRadius).toFloat()
        )
        // start point
        canvas!!.drawOval(oval, paint)
      }

    }
  }

  class MarkOverlay @JvmOverloads constructor(gp1: LatLng, defaultColor: Int = 999) :
    RouteOverlay(gp1, defaultColor) {

    public override fun myDraw(
      canvas: Canvas?,
      view: GoogleMap,
      projection: Projection,
      paint: Paint,
      point: Point
    ) {

      // mode=1&#65306;start
      paint.color = if (defaultColor == 999) Color.BLACK else defaultColor
      if (null != img) {
        canvas!!.drawBitmap(
          img!!,
          (point.x - img!!.width / 2).toFloat(),
          (point.y - img!!.height / 2).toFloat(),
          paint
        )
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

  class LineOverlay @JvmOverloads constructor(
    gp1: LatLng, protected val gp2: LatLng // LatLng is a int. (6E)
    , defaultColor: Int = 999
  ) : RouteOverlay(gp1, defaultColor) {

    public override fun myDraw(
      canvas: Canvas?,
      view: GoogleMap,
      projection: Projection,
      paint: Paint,
      point: Point
    ) {
      // mode=2&#65306;path

      paint.color = if (defaultColor == 999) Color.RED else defaultColor
      // val point2 = Point(); // projection.toPixels(gp2, point2)
      val point2 = projection.toScreenLocation(gp2);
      // v2 Point point2 = projection.toScreenLocation(gp2);

      paint.strokeWidth = lineWidth.toFloat()
      paint.alpha = if (defaultColor == Color.parseColor("#6C8715")) 220 else 120
      canvas!!.drawLine(
        point.x.toFloat(),
        point.y.toFloat(),
        point2.x.toFloat(),
        point2.y.toFloat(),
        paint
      )

    }
  }

  class LoopEndOverlay @JvmOverloads constructor(
    gp1: LatLng,
    protected val gp2: LatLng,
    defaultColor: Int = 999
  ) : RouteOverlay(gp1, defaultColor) {

    public override fun myDraw(
      canvas: Canvas?,
      view: GoogleMap,
      projection: Projection,
      paint: Paint,
      point: Point
    ) {

      /* mode=3&#65306;end */

      /* the last path */

      paint.color = if (defaultColor == 999) Color.BLACK else defaultColor
      // if (defaultColor == 999)
      // paint.setColor(Color.BLACK); // Color.GREEN
      // else
      // paint.setColor(defaultColor);

      //val point2 = Point(); //projection.toPixels(gp2, point2)
      val point2 = projection.toScreenLocation(gp2);
      // v2 Point point2 = projection.toScreenLocation(gp2);
      paint.strokeWidth = lineWidth.toFloat()
      paint.alpha = if (defaultColor == Color.parseColor("#6C8715")) 220 else 120
      canvas!!.drawLine(
        point.x.toFloat(),
        point.y.toFloat(),
        point2.x.toFloat(),
        point2.y.toFloat(),
        paint
      )

      if (null != img) {
        canvas.drawBitmap(
          img!!,
          (point.x - ovalRadius - img!!.width / 2).toFloat(),
          (point.y + ovalRadius - img!!.height / 2).toFloat(),
          paint
        )
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

  class EndOverlay @JvmOverloads constructor(gp1: LatLng, defaultColor: Int = 999) :
    RouteOverlay(gp1, defaultColor) {

    public override fun myDraw(
      canvas: Canvas?,
      view: GoogleMap,
      projection: Projection,
      paint: Paint,
      point: Point
    ) {

      /* mode=3&#65306;end */

      /* the last path */

      paint.color = if (defaultColor == 999) Color.BLACK else defaultColor
      // if (defaultColor == 999)
      // paint.setColor(Color.BLACK); // Color.GREEN
      // else
      // paint.setColor(defaultColor);

      // Point point2 = new Point();
      // val point2 = projection.toScreenLocation(gp2);
      // paint.setStrokeWidth(lineWidth);
      // paint.setAlpha(defaultColor == Color.parseColor("#6C8715") ? 220 : 120);
      // canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);

      if (null != img) {
        canvas!!.drawBitmap(
          img!!,
          (point.x - img!!.width / 2).toFloat(),
          (point.y - img!!.height / 2).toFloat(),
          paint
        )
      } else {
        val oval = RectF(
          (point.x - ovalRadius).toFloat(), (point.y - ovalRadius).toFloat(),
          (point.x + ovalRadius).toFloat(), (point.y + ovalRadius).toFloat()
        )
        /* end point */
        paint.alpha = 255
        canvas!!.drawOval(oval, paint)
      }

    }

  }
}