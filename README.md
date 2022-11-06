# Sprint 2 (Concluído :white_check_mark:)

## Descrição Sprint 2

O cliente submete um pedido de processamento ao balanceador de carga, que será encaminhado para um dos processadores e recebe um identificador para o pedido, juntamente com o identificador do processador responsável pelo processamento.
<br>O cliente verifica o estado do pedido a qualquer momento junto do processador.

## Como testar:
No ficheiro Client.java (Localizado em Client/src) alterar o seguinte código:
- Linha 19: Alterar o caminho (pathname) em `File(String pathname)`, ou seja, a localização para o ficheiro guardado localmente (atenção que no caso de se executar o cliente numa máquina Windows, as subpastas devem ser separadas por duas "\\")
- Linha 24: Alterar o nome do ficheiro (fileName) em `FileData(UUID fileID, String fileName, String fileBase64)`

## Arquitetura inicial do sistema em UML: [Sprint 1 UML](https://miro.com/app/board/uXjVPJdU0WE=/?share_link_id=410852075154)
