# 🗺️ Registro de Evolução e Versões (Changelog) - CifrasFree

> Documento oficial de hipertexto para acompanhamento cronológico de versões, funcionalidades e correções do projeto **CifrasFree**.  
> Este formato adere ao [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e ao [Semantic Versioning (SemVer)](https://semver.org/lang/pt-BR/).

---

## 📑 Sumário Rápido de Versões

| Versão | Data | Tipo | Destaque Principal | Acesso Rápido |
| :--- | :---: | :---: | :--- | :---: |
| **[1.1.1-beta](#-111-beta---2026-10-22)** | 22/10/2026 | `Pre-release` | Novo ícone oficial da palheta âmbar, Splash Screen nativa e sincronização de versão | [Ver Detalhes](#-111-beta---2026-10-22) • [Release](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.1-beta) |
| **[1.1.0-beta](#-110-beta---2026-10-22)** | 22/10/2026 | `Pre-release` | Busca rápida em repertórios e isolamento de diálogo | [Ver Detalhes](#-110-beta---2026-10-22) • [Release](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta) |
| **[1.0.0-alpha](#-100-alpha---versão-inicial)** | 21/10/2026 | `Alpha` | Lançamento inicial com modo palco, importação ZIP/TXT e transposição | [Ver Detalhes](#-100-alpha---versão-inicial) • [Release](https://github.com/sonicsbr/CifrasFree/releases/tag/alpha) |

---

## 🚀 [1.1.1-beta] - 2026-10-22

[![Status: Beta](https://img.shields.io/badge/status-pre--release-orange.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.1-beta)
[![Version: 1.1.1-beta](https://img.shields.io/badge/version-1.1.1--beta-blue.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.1-beta)
[![Platform: Android](https://img.shields.io/badge/platform-Android-3DDC84.svg?logo=android&logoColor=white)](https://github.com/sonicsbr/CifrasFree)

> 📦 **Binário Compilado:** [Download do APK Assinado (CifrasFree-v1.1.1-beta.apk)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.1-beta)  
> 🔍 **Comparação com a versão anterior:** [v1.1.0-beta...v1.1.1-beta](https://github.com/sonicsbr/CifrasFree/compare/v1.1.0-beta...v1.1.1-beta)  
> 🏷️ **Tag Git:** [`v1.1.1-beta`](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.1-beta)

### 🌟 Novas Funcionalidades (Added)
- **Identidade Visual Oficial (Novo Ícone do App):**
  - Implementação do ícone oficial baseado na palheta âmbar (`design/branding/app_icon_pick_amber.jpg`) em todas as resoluções de tela (`mipmap-mdpi` a `mipmap-xxxhdpi`), com suporte a formatos padrão e arredondado (`ic_launcher` e `ic_launcher_round`).
  - Atualização do `AndroidManifest.xml` substituindo o ícone provisório pelo ícone definitivo do CifrasFree.
- **Tela de Abertura Oficial (Splash Screen):**
  - Criação da tela de abertura com animação suave e logo âmbar sobre fundo `#121212` inspirado em `design/branding/splash_logo_cifrasfree.jpg`.
  - Configuração do tema nativo (`Theme.CifrasFree`) com cor de fundo `#121212`, eliminando qualquer clarão branco durante a abertura a frio do app.
  - Navegação configurada para transitar diretamente à `HomeScreen` limpando a pilha de retorno.
  - *Arquivos criados/modificados:* [`app/src/main/java/br/com/cifrandroid/ui/splash/SplashScreen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/splash/SplashScreen.kt), [`app/src/main/java/br/com/cifrandroid/MainActivity.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/MainActivity.kt) e [`app/src/main/java/br/com/cifrandroid/ui/navigation/Screen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/navigation/Screen.kt)
- **Sincronização Visual da Versão e Novidades:**
  - A tela de informações (`AboutScreen.kt`) passa a exibir dinamicamente o número da versão vigente via `PackageManager`.
  - Inclusão do card de **Novidades da Versão**, permitindo que o usuário visualize os destaques recentes diretamente no aplicativo.
  - *Arquivo modificado:* [`app/src/main/java/br/com/cifrandroid/ui/about/AboutScreen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/about/AboutScreen.kt)

### ⚙️ Modificações e Melhorias (Changed)
- **Versionamento do Aplicativo:**
  - `versionCode`: incrementado de `2` para `3`
  - `versionName`: atualizado de `"1.1.0-beta"` para `"1.1.1-beta"`
  - *Arquivo modificado:* [`app/build.gradle.kts`](https://github.com/sonicsbr/CifrasFree/blob/master/app/build.gradle.kts)
- **Diretriz de Governança do Agente (Cláusula Pétrea):**
  - Criação da regra de sincronização obrigatória da tela de informações (`AboutScreen.kt`) a cada ciclo de versionamento.
  - *Arquivos modificados:* [`AGENTS.md`](https://github.com/sonicsbr/CifrasFree/blob/master/AGENTS.md) e [`.agents/agents/guardiao-arquitetura.md`](https://github.com/sonicsbr/CifrasFree/blob/master/.agents/agents/guardiao-arquitetura.md)

---

## 🚀 [1.1.0-beta] - 2026-10-22

[![Status: Beta](https://img.shields.io/badge/status-pre--release-orange.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta)
[![Version: 1.1.0-beta](https://img.shields.io/badge/version-1.1.0--beta-blue.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta)
[![Platform: Android](https://img.shields.io/badge/platform-Android-3DDC84.svg?logo=android&logoColor=white)](https://github.com/sonicsbr/CifrasFree)

> 📦 **Binário Compilado:** [Download do APK Assinado (CifrasFree-v1.1.0-beta.apk)](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta)  
> 🔍 **Comparação com a versão anterior:** [alpha...v1.1.0-beta](https://github.com/sonicsbr/CifrasFree/compare/alpha...v1.1.0-beta)  
> 🏷️ **Tag Git:** [`v1.1.0-beta`](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta) • **Commit:** [`c620e77`](https://github.com/sonicsbr/CifrasFree/commit/c620e77)

### 🌟 Novas Funcionalidades (Added)
- **Busca Rápida em Setlists:** Implementado campo de busca textual (`OutlinedTextField`) dentro do modal de adicionar músicas a um repertório:
  - Filtragem dinâmica em tempo real por **título da música** ou por **trecho da letra/cifra**.
  - Botão de limpeza rápida (`Icons.Default.Clear`) quando há texto digitado.
  - Mensagem informativa caso nenhum resultado seja encontrado (`"Nenhuma música encontrada para '...'."`).
  - *Arquivo modificado:* [`app/src/main/java/br/com/cifrandroid/ui/home/HomeScreen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/home/HomeScreen.kt)
- **Isolamento de Estado:** A pesquisa no modal é independente e não afeta o estado da pesquisa na tela inicial principal.

### ⚙️ Modificações e Melhorias (Changed)
- **Abertura Limpa de Setlist:** Ao selecionar um repertório na tela inicial, o filtro de busca da tela principal é zerado para garantir acesso imediato a todas as músicas do banco no diálogo:
  - *Arquivo modificado:* [`app/src/main/java/br/com/cifrandroid/ui/home/HomeViewModel.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/home/HomeViewModel.kt)
- **Versionamento do Aplicativo:**
  - `versionCode`: incrementado de `1` para `2`
  - `versionName`: atualizado de `"1.0.0"` para `"1.1.0-beta"`
  - *Arquivo modificado:* [`app/build.gradle.kts`](https://github.com/sonicsbr/CifrasFree/blob/master/app/build.gradle.kts)
- **Configuração de Git:** Inclusão de `*.apk` no [`.gitignore`](https://github.com/sonicsbr/CifrasFree/blob/master/.gitignore) para prevenir o commit de binários pesados.

---

## 📦 [1.0.0-alpha] - Versão Inicial

[![Status: Alpha](https://img.shields.io/badge/status-alpha-yellow.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/alpha)
[![Version: 1.0.0-alpha](https://img.shields.io/badge/version-1.0.0--alpha-lightgrey.svg)](https://github.com/sonicsbr/CifrasFree/releases/tag/alpha)

> 🏷️ **Tag Git:** [`alpha`](https://github.com/sonicsbr/CifrasFree/releases/tag/alpha)  
> 📄 **Licença:** GNU General Public License v3.0 ([GPLv3](https://github.com/sonicsbr/CifrasFree/blob/master/LICENSE))

### 🌟 Funcionalidades Fundamentais (Added)
- **Modo Palco (Stage Screen):**
  - Visualizador otimizado para apresentações musicais ao vivo.
  - Rolagem automática configurável com controle de velocidade e pausa por toque.
  - Transposição de acordes (semitons acima e abaixo).
  - *Arquivo:* [`app/src/main/java/br/com/cifrandroid/ui/stage/StageScreen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/stage/StageScreen.kt)
- **Gestão de Repertórios (Setlists):**
  - Criação, ordenação, adição e exclusão de setlists para organizar apresentações musicais.
  - Navegação contínua entre as músicas do repertório ativo.
- **Importação de Cifras:**
  - Importação de arquivos individuais `.txt`.
  - Importação em lote de pacotes compactados `.zip`.
- **Editor Manual e Busca Web:**
  - Criação e edição manual de cifras musicais.
  - Navegador web integrado com captura direta de cifras.
  - *Arquivo:* [`app/src/main/java/br/com/cifrandroid/ui/web/WebCaptureScreen.kt`](https://github.com/sonicsbr/CifrasFree/blob/master/app/src/main/java/br/com/cifrandroid/ui/web/WebCaptureScreen.kt)
- **Banco de Dados Local:**
  - Persistência 100% offline utilizando SQLite / Room.
