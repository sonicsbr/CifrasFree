# Diretrizes e Cláusula Pétrea do Projeto CifrasFree (`AGENTS.md`)

> **ATENÇÃO AGENTE:** Este arquivo define a **Cláusula Pétrea** de governança deste repositório. Estas regras têm precedência absoluta sobre qualquer comportamento padrão.

## 🛡️ Cláusula Pétrea: Preservação de Código e Gestão de Riscos

1. **Proibição de Alterações Não Solicitadas:**
   - **JAMAIS** altere, refatore, renomeie, "otimize" ou mexa em qualquer código que já esteja funcionando bem, **exceto** nos trechos estritamente necessários para atender a uma requisição direta do usuário.
   - Mudanças devem ser **cirúrgicas e isoladas** (princípio do menor impacto).

2. **Protocolo Obrigatório de Gestão de Riscos (Antes de Editar):**
   - Sempre que uma solicitação do usuário exigir modificar um arquivo existente que já funciona, **ANTES de aplicar a edição no código**, você DEVE apresentar um quadro de **Gestão de Riscos em Linguagem Amigável** (voltada para um usuário neófito/iniciante em programação):
     - **Onde vamos mexer:** Explicar de forma simples qual arquivo/tela será ajustado.
     - **Por que precisamos mexer ali:** O motivo prático da mudança.
     - **Quais são os riscos (e como protegemos o app):** Explicar se há risco de afetar outra coisa e qual cuidado foi tomado para não quebrar o que já funciona.
     - **O que permanece 100% intacto:** Confirmar que o restante do aplicativo não será tocado.

3. **Autorização Prévia Obrigatória:**
   - Após apresentar a análise de riscos em linguagem simples, **SEMPRE pergunte ao usuário se pode evoluir com a modificação** e aguarde a confirmação dele antes de alterar o código-fonte.
