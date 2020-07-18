package sg.edu.np.financetracker2;

import android.graphics.Paint;

public class LegendItem {

    String name;
    double value;
    double percent;
    Paint colour;

    public LegendItem(String name, double value, double percent, Paint colour){
        this.name = name;
        this.value = value;
        this.percent = percent;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public double getPercent() {
        return percent;
    }

    public Paint getColour() {
        return colour;
    }
}
