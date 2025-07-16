# Teclado de Memes para Android

Este projeto é um teclado Android personalizado que permite aos usuários enviar memes (imagens, GIFs, áudios e vídeos) diretamente de um teclado. Ele inclui funcionalidades para adicionar novos memes, associar palavras-chave a eles e pesquisar memes por essas palavras-chave.

## Funcionalidades

- **Envio de Memes**: Suporta o envio de diversos formatos de mídia como memes.
- **Gerenciamento de Memes**: Adicione e organize seus próprios memes.
- **Pesquisa por Palavras-chave**: Encontre memes rapidamente usando palavras-chave personalizadas.
- **Interface de Teclado Dedicada**: Acesso fácil aos memes diretamente do teclado.

## Estrutura do Projeto

O projeto segue a estrutura padrão de um aplicativo Android:

```
android-meme-keyboard/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/memekeyboard/
│   │   │   │   ├── AddMemeActivity.java
│   │   │   │   ├── MemeAdapter.java
│   │   │   │   ├── MemeKeyboardService.java
│   │   │   │   └── MemeManager.java
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   ├── add_meme_layout.xml
│   │   │       │   ├── keyboard_layout.xml
│   │   │       │   └── meme_grid_item.xml
│   │   │       ├── xml/
│   │   │       │   └── method.xml
│   │   │       └── values/
│   │   │           ├── strings.xml
│   │   │           └── styles.xml
│   ├── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── README.md
```

## Como Compilar e Instalar

Para compilar e instalar este teclado Android, siga os passos abaixo:

### Pré-requisitos

- Android Studio instalado (versão 2021.1.1 ou superior recomendada).
- SDK do Android configurado (API 21 ou superior).
- Um dispositivo Android ou emulador para testes.

### Passos para Compilação

1.  **Clone ou Baixe o Projeto**: Se você recebeu o projeto como um arquivo ZIP, descompacte-o. Se for um repositório Git, clone-o:
    ```bash
    git clone <URL_DO_REPOSITORIO>
    cd android-meme-keyboard
    ```

2.  **Abrir no Android Studio**: Abra o Android Studio e selecione `File > Open` (ou `Open an existing Android Studio project`). Navegue até a pasta `android-meme-keyboard` e clique em `OK`.

3.  **Sincronizar Projeto com Arquivos Gradle**: O Android Studio deve automaticamente sincronizar o projeto e baixar as dependências necessárias. Se não o fizer, clique em `File > Sync Project with Gradle Files`.

4.  **Construir o Projeto**: Vá em `Build > Make Project` (ou `Build > Rebuild Project`). Isso irá compilar o código-fonte e gerar o arquivo APK.

### Passos para Instalação no Dispositivo/Emulador

1.  **Conectar Dispositivo/Iniciar Emulador**: Conecte seu dispositivo Android via USB (com a depuração USB ativada) ou inicie um emulador no Android Studio.

2.  **Executar o Aplicativo**: No Android Studio, clique no botão `Run 'app'` (o ícone de 'play' verde) na barra de ferramentas. Selecione seu dispositivo ou emulador e clique em `OK`.

3.  **Ativar o Teclado**: Após a instalação, você precisará ativar o teclado nas configurações do seu dispositivo:
    a.  Vá para `Configurações` (Settings).
    b.  Procure por `Idioma e entrada` (Language & input) ou `Sistema > Idiomas e entrada`.
    c.  Em `Teclados`, selecione `Teclado virtual` (Virtual keyboard) ou `Gerenciar teclados` (Manage keyboards).
    d.  Ative o `Meme Keyboard`.
        Se ele não aparecer imediatamente na lista, abra o aplicativo `Meme Keyboard` uma vez e volte para `Gerenciar teclados`.
    e.  Você pode ser avisado sobre os riscos de segurança de teclados de terceiros. Confirme para ativar.

4.  **Alternar para o Teclado de Memes**: Ao usar qualquer aplicativo que exija entrada de texto (como um aplicativo de mensagens):
    a.  Toque em um campo de texto para abrir o teclado padrão.
    b.  Geralmente, há um ícone de teclado na barra de navegação inferior ou na notificação. Toque nele para alternar o teclado.
    c.  Selecione `Meme Keyboard` na lista.

## Como Adicionar Memes

1.  Abra o aplicativo `Meme Keyboard` na sua lista de aplicativos (ele terá um ícone).
2.  Na tela principal do teclado, você verá um botão "Add New Meme". Clique nele.
3.  Selecione o arquivo de mídia (imagem, GIF, áudio ou vídeo) da sua galeria ou arquivos.
4.  Digite as palavras-chave para o meme, separadas por vírgulas (ex: "engraçado, reação, risada").
5.  Clique em "Salvar Meme".

Seus memes serão armazenados internamente no aplicativo e estarão disponíveis para uso no teclado.

## Limitações

Esta versão removeu a dependência do `ffmpeg-kit` utilizada para converter GIFs ou vídeos curtos em WebP. Caso adicione memes animados como figurinhas, eles serão salvos no formato original, sem conversão para WebP.

## Licença

Este projeto é de código aberto e está disponível sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes. (Nota: O arquivo LICENSE não está incluído neste README, mas é uma boa prática adicioná-lo ao projeto.)


