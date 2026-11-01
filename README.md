# Honey Aplha

Painel Android para testar e diagnosticar fluxos DeX/visor secundário sem root, ADB, Shizuku ou permissões privilegiadas.

## O que o aplicativo faz

- Abre as telas oficiais de configurações DeX e visor externo.
- Consulta displays ativos e mostra `displayId`, resolução, densidade, modo e flags.
- Consulta chaves legíveis de `Settings.Secure`, `Settings.Global` e `Settings.System` relacionadas a DeX/display.
- Copia os diagnósticos para a área de transferência.
- Detecta displays secundários com ID dinâmico.
- Renderiza uma interface própria usando a API pública `Presentation`.
- Pode escurecer a tela principal enquanto a Presentation está aberta no display secundário.
- Mantém uma área de toque invisível de 96×64 dp no canto inferior direito para sair da tela preta.
- Usa ícone adaptativo em vetor com paleta azul inspirada na One UI/Samsung.
- Inclui fallbacks PNG em mdpi, hdpi, xhdpi, xxhdpi e xxxhdpi para launchers/barra de tarefas que não renderizam ícones adaptativos.
- Alterna automaticamente entre tema claro e escuro conforme o modo do Android.

## Compatibilidade

O projeto não contém bibliotecas nativas (`lib/*`) nem filtros de ABI. Ele é implementado somente com APIs Android/Java, então o APK é universal para ARMv7 (`armeabi-v7a`), ARM64 (`arm64-v8a`), x86 e x86_64. O `minSdk` é 26 (Android 8.0) por causa das APIs públicas de múltiplos displays; não é exclusivo de MediaTek ou Samsung.

As telas usam unidades `dp`, densidade do sistema, pesos de layout e `resizeableActivity`, permitindo uso em celulares, tablets, telas pequenas, telas grandes e orientações diferentes. O suporte a displays secundários é opcional: em dispositivos sem essa capacidade, o painel continua funcionando e apenas informa que não encontrou um display secundário.

## Segurança

O APK não declara permissões especiais. Não ativa ADB, Wireless Debugging, root, Shizuku, `WRITE_SECURE_SETTINGS`, sobreposição, servidor de rede ou alteração do launcher padrão.

O aplicativo não inicia o `SecondaryLauncher` diretamente em outro display quando o Android bloqueia esse lançamento. A conexão do Smart View/DeX e a opção de displays simulados continuam sendo controladas manualmente pelo Android.

## Compilação

Requisitos: JDK, Android SDK com API 35, Android Gradle Plugin 8.6.1 e Gradle 8.7 ou compatível.

```bash
export ANDROID_SDK_ROOT=/caminho/para/android-sdk
./gradlew assembleDebug
```

O APK será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Uso do modo de display secundário

1. Conecte um visor pelo fluxo oficial do sistema ou ative manualmente `Simular displays secundários` nas Opções do desenvolvedor.
2. Abra o Honey Aplha.
3. Use `DIAGNÓSTICO DOS DISPLAYS` para confirmar o display diferente do `displayId 0`.
4. Use `RENDERIZAR INTERFACE NO DISPLAY` para testar a Presentation.
5. Use `USAR DISPLAY SECUNDÁRIO / ESCURECER DISPLAY 0` para escurecer a tela principal.
6. Para sair da tela preta, toque no canto inferior direito, na área invisível de saída.

## Publicar no GitHub

O pacote do projeto está pronto para ser publicado: contém somente código-fonte, arquivos Gradle, Gradle Wrapper, README e `.gitignore`. APKs, diretórios `build/`, cache do Gradle, logs e arquivos locais não entram no repositório.

Crie um repositório vazio no GitHub, de preferência privado no primeiro momento. Depois, no diretório deste projeto:

```bash
git init
git add .
git commit -m "Initial Honey Aplha DeX diagnostic panel"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/honey-aplha.git
git push -u origin main
```

Para publicar pelo GitHub CLI, depois de estar autenticado:

```bash
gh repo create honey-aplha --private --source=. --remote=origin --push
```

Não publique APKs, logs de diagnóstico, chaves ou informações do tablet no repositório sem revisar antes. O projeto contém apenas o código-fonte e os arquivos de compilação; o APK pode ser anexado em Releases depois.
