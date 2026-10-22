# Registro de Alterações (Changelog) - CifrasFree

Todas as alterações notáveis deste projeto serão documentadas neste arquivo.
O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/) e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [1.1.0-beta] - 2026-10-22

### Adicionado
- **Busca rápida ao adicionar músicas ao Setlist:** Novo campo de busca textual (`OutlinedTextField`) dentro do modal de adicionar músicas a um repertório/setlist.
- **Filtro dinâmico por título e letra:** Pesquisa instantânea por título da música ou trecho do conteúdo da cifra/letra.
- **Isolamento de estado no diálogo:** A busca dentro do modal é independente e não interfere na pesquisa ativa na tela principal.
- **Feedback visual de busca vazia:** Exibição de mensagem informativa quando nenhuma música corresponde aos termos pesquisados.
- **Botão para limpar busca:** Ícone de limpeza rápida no campo de busca do diálogo quando houver texto digitado.

### Modificado
- Ao abrir/selecionar um setlist, o filtro anterior da tela principal é automaticamente redefinido para garantir acesso a todo o acervo de músicas no diálogo.
- Incremento do versionamento do app: `versionCode = 2` e `versionName = "1.1.0-beta"`.

---

## [1.0.0-alpha] - Versão Inicial

### Adicionado
- **Visualizador de Cifras (Stage):** Rolagem automática configurável, transposição de tons (+/- semitons) e modo palco.
- **Gestão de Setlists (Repertórios):** Criação, organização e execução sequencial de repertórios.
- **Importação Flexível:** Suporte a arquivos de texto individuais `.txt` e importação em lote via pacotes compactados `.zip`.
- **Editor e Busca Web:** Criação manual de cifras e navegador integrado para captura de cifras online.
- **Armazenamento Local:** Banco de dados Room para funcionamento 100% offline.
