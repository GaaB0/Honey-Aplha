package com.honeyspace.bridge;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DexPresentation extends Presentation {
    public DexPresentation(Context context, Display display) {
        super(context, display);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(48), dp(32), dp(48), dp(28));
        root.setBackgroundColor(Color.rgb(9, 22, 48));

        LinearLayout top = new LinearLayout(getContext());
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text("Honey Aplha", 28, Color.WHITE);
        title.setTypeface(null, 1);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView mode = text("Modo desktop", 16, Color.rgb(190, 210, 240));
        top.addView(mode, new LinearLayout.LayoutParams(-2, -2));
        root.addView(top, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = text("Interface renderizada no display secundário", 18, Color.rgb(198, 215, 242));
        subtitle.setPadding(0, dp(12), 0, dp(28));
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(24), dp(24), dp(24), dp(24));
        card.setBackground(round(Color.rgb(24, 43, 76), 22));
        TextView heading = text("Display secundário ativo", 22, Color.WHITE);
        card.addView(heading, new LinearLayout.LayoutParams(-1, -2));
        TextView info = text("O Android criou esta tela virtual e o Honey Aplha conseguiu renderizar nela sem iniciar Activities por launchDisplayId.", 16, Color.rgb(205, 220, 242));
        info.setPadding(0, dp(12), 0, dp(18));
        card.addView(info, new LinearLayout.LayoutParams(-1, -2));
        TextView map = text("MAPA DE TELAS\n\n[ DISPLAY 0 ]  Tela integrada\n          ↕ área de passagem do ponteiro\n[ DISPLAY SECUNDÁRIO ]  esta tela\n\nO lado esquerdo/direito real é definido pelo sistema. Mova o ponteiro para confirmar a direção.", 15, Color.rgb(225, 235, 250));
        map.setPadding(0, 0, 0, dp(4));
        card.addView(map, new LinearLayout.LayoutParams(-1, -2));
        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        Button close = new Button(getContext());
        close.setText("FECHAR INTERFACE");
        close.setTextColor(Color.WHITE);
        close.setAllCaps(false);
        close.setBackground(round(Color.rgb(45, 83, 140), 14));
        close.setOnClickListener(v -> dismiss());
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(-1, -2);
        closeParams.setMargins(0, dp(24), 0, 0);
        root.addView(close, closeParams);
        setContentView(root);
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int dp(int value) {
        return (int) (value * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
