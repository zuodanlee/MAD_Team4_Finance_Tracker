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

    public int getMonth(){
        String month = mDate.substring(0, 3);
        if (month.equals("Jan")){
            return 0;
        }
        else if (month.equals("Feb")){
            return 1;
        }
        else if (month.equals("Mar")){
            return 2;
        }
        else if (month.equals("Apr")){
            return 3;
        }
        else if (month.equals("May")){
            return 4;
        }
        else if (month.equals("Jun")){
            return 5;
        }
        else if (month.equals("Jul")){
            return 6;
        }
        else if (month.equals("Aug")){
            return 7;
        }
        else if (month.equals("Sep")){
            return 8;
        }
        else if (month.equals("Oct")){
            return 9;
        }
        else if (month.equals("Nov")){
            return 10;
        }
        else if (month.equals("Dec")){
            return 11;
        }
        else{
            return -1;
        }
    }
}
