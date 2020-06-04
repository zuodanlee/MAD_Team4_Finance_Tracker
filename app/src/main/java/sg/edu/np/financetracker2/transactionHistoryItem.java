package sg.edu.np.financetracker2;

import java.io.Serializable;

public class transactionHistoryItem implements Serializable {
    private int mImageResource;
    private String mLine1;
    private String mLine2;
    private String mDate;
    private  String mPrice;

    public transactionHistoryItem(int imageResource, String line1, String line2, String date, String price){
        mImageResource = imageResource;
        mLine1 = line1;
        mLine2 = line2;
        mDate = date;
        mPrice = price;
    }

    public int getmImageResource(){
        return mImageResource;
    }
    public String getmLine1(){
        return mLine1;
    }
    public String getmLine2(){
        return mLine2;
    }
    public String getmDate(){
        return mDate;
    }
    public String getmPrice(){
        return mPrice;
    }


}
