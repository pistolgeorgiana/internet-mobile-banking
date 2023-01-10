package eu.ase.ro.dam.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PieChart extends View {
    private float startAngle = 0;
    private List<Double> values = new ArrayList<>();
    private Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectangle = new RectF(300, 700, 800, 1200);

    public PieChart(Context context, List<Double> values) {
        super(context);
        this.values = values;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0; i < values.size(); i++) {
            if(i==0) {
                colorPaint.setColor(Color.rgb(0, 255, 0));
                canvas.drawArc(rectangle, 0,
                        values.get(i).floatValue(), true, colorPaint);
            } else {
                startAngle = values.get(i-1).floatValue();
                colorPaint.setColor(Color.rgb(255, 0, 0));
                canvas.drawArc(rectangle, startAngle,
                        values.get(i).floatValue(), true, colorPaint);
            }
        }
    }
}
