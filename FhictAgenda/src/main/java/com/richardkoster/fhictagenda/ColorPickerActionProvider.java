package com.richardkoster.fhictagenda;

import android.content.Context;
import android.graphics.Color;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.Toast;


public class ColorPickerActionProvider extends ActionProvider implements View.OnClickListener {

    Context mContext;
    PopupWindow mPopupWindow;
    private View mActionButton;
    private OnColorSelectedListener mListener;
    String[] mColors = {
        "#DC4FAD",
                "#AC193D",
                "#D24726",
                "#FF8F32",
                "#82BA00",
                "#008A17",
                "#03B3B2",
                "#008299",
                "#5DB2FF",
                "#0072C6",
                "#4617B4",
                "#8C0095"
    };

    public ColorPickerActionProvider(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View onCreateActionView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        GridLayout popupView = new GridLayout(mContext);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                Button colorButton = new Button(mContext);
                colorButton.setBackgroundColor(Color.parseColor(mColors[y*3+x]));
                final int finalX = x;
                final int finalY = y;
                colorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mListener != null){
                            mListener.onColorSelected(mColors[finalY *3+ finalX]);
                        }
                        mPopupWindow.dismiss();
                    }
                });
                popupView.addView(colorButton, new GridLayout.LayoutParams(GridLayout.spec(y), GridLayout.spec(x)));
            }
        }

        mPopupWindow = new PopupWindow(popupView);
        mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.abs__menu_dropdown_panel_holo_light));

        mActionButton = inflater.inflate(R.layout.action_colorpicker, null);
        mActionButton.setOnClickListener(this);

        return mActionButton;
    }

    @Override
    public void onClick(View view) {
        mPopupWindow.showAsDropDown(mActionButton);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        mListener = listener;
    }

    public interface OnColorSelectedListener {
        public void onColorSelected(String color);
    }
}
