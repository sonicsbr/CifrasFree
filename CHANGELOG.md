# 🗺️ Registro de Evolução e Versões (Changelog) - CifrasFree

> Documento oficial de hipertexto para acompanhamento cronológico de versões, funcionalidades e correções do projeto **CifrasFree**.  
> Este formato adere ao [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e ao [Semantic Versioning (SemVer)](https://semver.org/lang/pt-BR/).

---

## 📑 Sumário Rápido de Versões

| Versão | Data | Tipo | Destaque Principal | Acesso Rápido |
| :--- | :---: | :---: | :--- | :---: |
| **[1.1.0-beta](#-110-beta---2026-10-22)** | 22/10/2026 | `Pre-release` | Busca rápida em repertórios e isolamento de diálogo | [Ver Detalhes](#-110-beta---2026-10-22) • [Release](https://github.com/sonicsbr/CifrasFree/releases/tag/v1.1.0-beta) |
| **[1.0.0-alpha](#-100-alpha---versão-inicial)** | 21/10/2026 | `Alpha` | Lançamento inicial com modo palco, importação ZIP/TXT e transposição | [Ver Detalhes](#-100-alpha---versão-inicial) • [Release](https://github.com/sonicsbr/CifrasFree/releases/tag/alpha) |

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
