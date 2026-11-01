package com.honeyspace.bridge;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Presentation;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TARGET_PACKAGE = "com.sec.android.app.launcher";
    private static final String TARGET_CLASS = "com.honeyspace.dexservice.SecondaryLauncher";
    private TextView status;
    private Presentation secondaryPresentation;
    private boolean darkMode;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setTaskDescription(new ActivityManager.TaskDescription(
                "Honey Aplha",
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_legacy)));
        buildUi();
        // Nenhuma tela externa é aberta automaticamente.
    }

    private void buildUi() {
        darkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        getWindow().setStatusBarColor(darkMode ? Color.rgb(18, 59, 115) : Color.rgb(21, 101, 192));
        getWindow().setNavigationBarColor(darkMode ? Color.rgb(11, 18, 32) : Color.rgb(247, 248, 252));
        int pad = dp(20);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(pad, pad, pad, pad);
        box.setBackgroundColor(darkMode ? Color.rgb(16, 26, 42) : Color.rgb(247, 248, 252));

        TextView title = new TextView(this);
        title.setText("DeX sem fio");
        title.setTextSize(25);
        title.setTextColor(darkMode ? Color.WHITE : Color.rgb(22, 54, 96));
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        box.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("Honey Aplha  •  conexão e configuração");
        subtitle.setTextSize(16);
        subtitle.setTextColor(darkMode ? Color.rgb(190, 210, 240) : Color.rgb(91, 105, 125));
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitle.setPadding(0, dp(6), 0, dp(18));
        box.addView(subtitle, new LinearLayout.LayoutParams(-1, -2));

        Button settings = new Button(this);
        settings.setText("ABRIR CONFIGURAÇÕES DeX");
        settings.setOnClickListener(v -> openDexSettings(true));
        box.addView(settings, new LinearLayout.LayoutParams(-1, -2));

        Button simulated = new Button(this);
        simulated.setText("GUIA: DISPLAY SECUNDÁRIO (MANUAL)");
        simulated.setOnClickListener(v -> openDeveloperDisplayGuide());
        box.addView(simulated, new LinearLayout.LayoutParams(-1, -2));

        Button wireless = new Button(this);
        wireless.setText("ABRIR DeX SEM FIO");
        wireless.setOnClickListener(v -> openComponent(
                "com.android.settings",
                "com.android.settings.Settings$DexModeActivity",
                "Tela DeX sem fio aberta.", true));
        box.addView(wireless, new LinearLayout.LayoutParams(-1, -2));

        Button style = new Button(this);
        style.setText("RESOLUÇÃO DO VISOR");
        style.setOnClickListener(v -> openComponent(
                "com.android.settings",
                "com.android.settings.Settings$ExternalDisplayScreenResolutionActivity",
                "Tela de resolução do visor aberta.", true));
        box.addView(style, new LinearLayout.LayoutParams(-1, -2));

        Button position = new Button(this);
        position.setText("VISOR CONECTADO");
        position.setOnClickListener(v -> openComponent(
                "com.android.settings",
                "com.android.settings.Settings$ExternalDisplayActivity",
                "Tela de visor conectado aberta.", true));
        box.addView(position, new LinearLayout.LayoutParams(-1, -2));

        Button secondaryMode = new Button(this);
        secondaryMode.setText("USAR DISPLAY SECUNDÁRIO / ESCURECER DISPLAY 0");
        secondaryMode.setOnClickListener(v -> enterSecondaryMode());
        box.addView(secondaryMode, new LinearLayout.LayoutParams(-1, -2));

        Button check = new Button(this);
        check.setText("VERIFICAR COMPONENTE");
        check.setOnClickListener(v -> inspectTarget());
        box.addView(check, new LinearLayout.LayoutParams(-1, -2));

        Button displays = new Button(this);
        displays.setText("DIAGNÓSTICO DOS DISPLAYS");
        displays.setOnClickListener(v -> inspectDisplays());
        box.addView(displays, new LinearLayout.LayoutParams(-1, -2));

        Button system = new Button(this);
        system.setText("DIAGNÓSTICO DO SISTEMA DeX");
        system.setOnClickListener(v -> inspectSystemDex());
        box.addView(system, new LinearLayout.LayoutParams(-1, -2));

        status = new TextView(this);
        status.setTextSize(14);
        status.setTextColor(darkMode ? Color.rgb(225, 235, 250) : Color.rgb(45, 58, 78));
        status.setTextIsSelectable(true);
        status.setPadding(0, dp(18), 0, 0);
        status.setText("Escolha uma opção acima.\n\nNenhuma tela é aberta automaticamente.");
        box.addView(status, new LinearLayout.LayoutParams(-1, -2));

        Button copy = new Button(this);
        copy.setText("COPIAR DIAGNÓSTICO");
        copy.setOnClickListener(v -> copyStatus());
        box.addView(copy, new LinearLayout.LayoutParams(-1, -2));

        for (int i = 0; i < box.getChildCount(); i++) {
            View child = box.getChildAt(i);
            if (child instanceof Button) {
                styleControl((Button) child);
                LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) child.getLayoutParams();
                buttonParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
                buttonParams.setMargins(0, dp(10), 0, dp(10));
                child.setLayoutParams(buttonParams);
            }
        }

        TextView note = new TextView(this);
        note.setText("A primeira opção abre a tela oficial do tablet para ativar o DeX/Wi‑Fi. Este app não ativa ADB, não concede root e não ignora permissões protegidas do sistema.");
        note.setTextSize(12);
        note.setTextColor(darkMode ? Color.rgb(170, 190, 215) : Color.rgb(105, 115, 130));
        note.setPadding(0, dp(24), 0, 0);
        box.addView(note, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(box);
        setContentView(scroll);
    }

    private void styleControl(Button button) {
        button.setTextColor(darkMode ? Color.rgb(226, 237, 255) : Color.rgb(27, 64, 112));
        button.setAllCaps(false);
        button.setMinHeight(dp(52));
        button.setPadding(dp(14), dp(8), dp(14), dp(8));
        button.setMaxWidth(getResources().getDisplayMetrics().widthPixels - dp(40));
        GradientDrawable background = new GradientDrawable();
        background.setColor(darkMode ? Color.rgb(31, 55, 88) : Color.WHITE);
        background.setStroke(dp(1), darkMode ? Color.rgb(69, 101, 145) : Color.rgb(213, 222, 237));
        background.setCornerRadius(dp(14));
        button.setBackground(background);
    }

    private void renderOnSecondaryDisplay(boolean showToast) {
        try {
            DisplayManager manager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
            Display target = null;
            for (Display display : manager.getDisplays()) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY &&
                        display.getState() == Display.STATE_ON) {
                    target = display;
                    break;
                }
            }
            if (target == null) {
                status.setText("Nenhum display secundário ativo foi encontrado.\n\n" +
                        "Ative o display simulado ou conecte o visor e tente novamente.");
                return;
            }
            if (secondaryPresentation != null && secondaryPresentation.isShowing()) {
                secondaryPresentation.dismiss();
            }
            secondaryPresentation = new DexPresentation(this, target);
            secondaryPresentation.show();
            status.setText("Interface renderizada no display secundário.\n\n" +
                    "displayId: " + target.getDisplayId() + "\n" + target.getName() +
                    "\n\nEsta renderização usa Presentation pública e não exige launchDisplayId.");
            if (showToast) Toast.makeText(this, "Interface renderizada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            status.setText("Não foi possível renderizar no display secundário.\n\n" +
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            if (showToast) Toast.makeText(this, "Falha na Presentation", Toast.LENGTH_LONG).show();
        }
    }

    private void enterSecondaryMode() {
        renderOnSecondaryDisplay(false);
        if (secondaryPresentation == null || !secondaryPresentation.isShowing()) return;

        FrameLayout black = new FrameLayout(this);
        black.setBackgroundColor(Color.BLACK);
        Button exit = new Button(this);
        exit.setText("");
        exit.setContentDescription("Sair do modo de display secundário");
        exit.setAlpha(0.0f);
        exit.setBackgroundColor(Color.TRANSPARENT);
        exit.setOnClickListener(v -> {
            if (secondaryPresentation != null) secondaryPresentation.dismiss();
            buildUi();
        });
        FrameLayout.LayoutParams exitParams = new FrameLayout.LayoutParams(dp(96), dp(64));
        exitParams.gravity = Gravity.BOTTOM | Gravity.END;
        black.addView(exit, exitParams);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(black);
    }

    private void openDeveloperDisplayGuide() {
        status.setText("Guia manual\n\n" +
                "1. Abra Opções do desenvolvedor.\n" +
                "2. Procure por ‘Simular displays secundários’.\n" +
                "3. Escolha uma resolução.\n" +
                "4. Volte ao Honey Aplha e use DIAGNÓSTICO DOS DISPLAYS.\n\n" +
                "O app não marca essa opção sozinho e não altera configurações protegidas.\n" +
                "Esse recurso cria um display virtual de teste; ele não garante o DeX Samsung.");
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception ignored) {
                Toast.makeText(this, "Não foi possível abrir as configurações", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openDexSettings(boolean showToast) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(
                "com.android.settings",
                "com.android.settings.Settings$DexEntryScreenSettingsActivity"));
        intent.addFlags(0x14000000);
        try {
            startActivity(intent);
            status.setText("Tela oficial de configurações DeX aberta.\n\n" +
                    "Ative o modo DeX ou o compartilhamento de tela via Wi‑Fi nessa tela e depois volte para este app.");
            if (showToast) Toast.makeText(this, "Configurações DeX abertas", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            status.setText("Não foi possível abrir a tela oficial do DeX.\n\n" +
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            if (showToast) Toast.makeText(this, "Tela DeX indisponível neste firmware", Toast.LENGTH_LONG).show();
        }
    }

    private void openDexModeSettings(boolean showToast) {
        openComponent(
                "com.sec.android.app.launcher",
                "com.sec.android.desktopmode.activity.connectivity.ConnectivityActivity",
                "Tela de conexão DeX por Wi‑Fi aberta.", showToast);
    }

    private void openDexConnectivity(boolean showToast) {
        openComponent(
                "com.sec.android.app.launcher",
                "com.sec.android.desktopmode.activity.connectivity.ConnectivityActivity",
                "Tela de conectividade DeX do One UI Home aberta.", showToast);
    }

    private void openComponent(String packageName, String className, String success, boolean showToast) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(0x14000000);
        try {
            startActivity(intent);
            status.setText(success + "\n\nSe a tela fechar imediatamente, o serviço DeX não criou um display ou estado válido.");
            if (showToast) Toast.makeText(this, "Intent enviado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            status.setText("Não foi possível abrir:\n" + packageName + "/" + className +
                    "\n\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
            if (showToast) Toast.makeText(this, "Componente indisponível", Toast.LENGTH_LONG).show();
        }
    }

    private void inspectDisplays() {
        try {
            DisplayManager manager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
            Display[] displays = manager.getDisplays();
            StringBuilder text = new StringBuilder("Displays visíveis para este app: ")
                    .append(displays.length).append("\n\n");
            for (Display display : displays) {
                DisplayMetrics metrics = new DisplayMetrics();
                display.getRealMetrics(metrics);
                Display.Mode mode = display.getMode();
                text.append("displayId: ").append(display.getDisplayId()).append('\n')
                        .append("nome: ").append(display.getName()).append('\n')
                        .append("estado: ").append(display.getState()).append('\n')
                        .append("resolução: ").append(metrics.widthPixels).append('x')
                        .append(metrics.heightPixels).append('\n')
                        .append("densidade: ").append(metrics.densityDpi).append(" dpi\n")
                        .append("modo: ").append(mode.getPhysicalWidth()).append('x')
                        .append(mode.getPhysicalHeight()).append(" @ ")
                        .append(mode.getRefreshRate()).append(" Hz\n")
                        .append("flags: 0x").append(Integer.toHexString(display.getFlags()))
                        .append("\n\n");
            }
            text.append("Faça este diagnóstico com e sem o visor externo.\n")
                    .append("Se o modo interno não criar um display/estado adicional, o bloqueio está no backend DeX.");
            status.setText(text.toString());
        } catch (Exception e) {
            status.setText("Não foi possível ler os displays.\n\n" +
                    e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void inspectSystemDex() {
        StringBuilder out = new StringBuilder("DIAGNÓSTICO DeX (sem root)\n\n");
        out.append("WRITE_SECURE_SETTINGS: ")
                .append(checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_GRANTED ? "concedida" : "não concedida")
                .append("\nSobreposição: ").append(Settings.canDrawOverlays(this) ? "permitida" : "não permitida")
                .append("\nSemDesktopModeManager: ");
        try {
            Class.forName("com.samsung.android.desktopmode.SemDesktopModeManager");
            out.append("classe presente");
        } catch (Throwable e) {
            out.append("classe não encontrada");
        }
        out.append("\nServiço desktopmode: ");
        try {
            Object service = getSystemService("desktopmode");
            out.append(service == null ? "não disponível" : "disponível (" + service.getClass().getName() + ")");
        } catch (Throwable e) {
            out.append("bloqueado: ").append(e.getClass().getSimpleName());
        }

        appendService(out, "com.sec.android.desktopmode.uiservice",
                "com.sec.android.desktopmode.uiservice.DesktopModeUiService");
        appendService(out, "com.sec.android.app.launcher",
                "com.honeyspace.dexservice.DesktopModeTile");
        appendService(out, "com.sec.android.app.launcher",
                "com.sec.android.desktopmode.ble.BleAdvertiserService");

        ContentResolver resolver = getContentResolver();
        appendSettings(out, resolver, Settings.Secure.CONTENT_URI, "SECURE");
        appendSettings(out, resolver, Settings.Global.CONTENT_URI, "GLOBAL");
        appendSettings(out, resolver, Settings.System.CONTENT_URI, "SYSTEM");

        out.append("\nObservação: leitura não exige root. Escrita em Secure/Global normalmente exige ADB, root ou app de sistema; este diagnóstico não altera nada.");
        status.setText(out.toString());
    }

    private void copyStatus() {
        String text = status == null || status.getText() == null ? "" : status.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Honey Aplha diagnóstico", text));
            Toast.makeText(this, "Diagnóstico copiado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Área de transferência indisponível", Toast.LENGTH_LONG).show();
        }
    }

    private void appendService(StringBuilder out, String pkg, String name) {
        out.append("\n\nServiço: ").append(name).append('\n');
        try {
            ServiceInfo info = getPackageManager().getServiceInfo(
                    new ComponentName(pkg, name), PackageManager.GET_META_DATA);
            out.append("encontrado=true\nexported=").append(info.exported)
                    .append("\nenabled=").append(info.enabled)
                    .append("\npermission=").append(info.permission == null ? "nenhuma" : info.permission);
        } catch (Exception e) {
            out.append("encontrado=false\n").append(e.getClass().getSimpleName());
        }
    }

    private void appendSettings(StringBuilder out, ContentResolver resolver, Uri uri, String section) {
        out.append("\n\n[").append(section).append(" com nomes relevantes]\n");
        int count = 0;
        try (Cursor cursor = resolver.query(uri, new String[]{"name", "value"}, null, null, "name ASC")) {
            if (cursor == null) {
                out.append("consulta retornou nulo");
                return;
            }
            while (cursor.moveToNext() && count < 120) {
                String name = cursor.getString(0);
                String value = cursor.getString(1);
                String lower = name == null ? "" : name.toLowerCase();
                if (lower.contains("dex") || lower.contains("desktop") || lower.contains("multiwindow") ||
                        lower.contains("display") || lower.contains("wireless") || lower.contains("secondary_home")) {
                    if (value != null && value.length() > 180) value = value.substring(0, 180) + "...";
                    out.append(name).append(" = ").append(value).append('\n');
                    count++;
                }
            }
            if (count == 0) out.append("nenhuma chave legível encontrada");
        } catch (SecurityException e) {
            out.append("acesso bloqueado pelo sistema");
        } catch (Exception e) {
            out.append("erro: ").append(e.getClass().getSimpleName());
        }
    }

    private void inspectTarget() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(
                    new ComponentName(TARGET_PACKAGE, TARGET_CLASS),
                    PackageManager.GET_META_DATA);
            String permission = info.permission == null ? "nenhuma" : info.permission;
            status.setText("Componente encontrado.\n\n" +
                    "exported: " + info.exported + "\n" +
                    "permission: " + permission + "\n" +
                    "enabled: " + info.enabled + "\n\n" +
                    (info.exported ? "Pode aceitar uma chamada externa." : "Não aceita chamada de apps comuns."));
        } catch (Exception e) {
            status.setText("Não consegui consultar o componente como Activity.\n\n" +
                    "Ele pode ser um Service, estar oculto no manifest ou exigir acesso ADB/Shizuku.\n\n" +
                    e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
