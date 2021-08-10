package com.ata.gogreenowner.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;

import com.ata.gogreenowner.R;

public class DashboardCard extends RelativeLayout {
    public DashboardCard(Context context) {
        super(context,null);
    }

    public DashboardCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        View childView = View.inflate(context, R.layout.dashboard_card,this);
        ImageView imageView = childView.findViewById(R.id.dashboard_card_ImageView);
        TextView textView = childView.findViewById(R.id.dashboard_card_textView);

        TypedArray myAttrs = context.obtainStyledAttributes(attrs,R.styleable.DashboardCard);
        try{
            String dashboardText = myAttrs.getString(R.styleable.DashboardCard_text);
            int imageResID = myAttrs.getResourceId(R.styleable.DashboardCard_image,0);
            if(imageResID != 0){
                Drawable drawable = AppCompatResources.getDrawable(context,imageResID);
                imageView.setImageDrawable(drawable);
            }
            textView.setText(dashboardText);
        }finally {
            myAttrs.recycle();
        }
    }
}
